package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.business.LunaticLoopException;
import fr.insee.eno.core.model.lunatic.MissingBlock;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * "Missing" variables in Lunatic are variables designed to collect the "don't know" or "refusal" information for
 * question components. This feature works with a "missing" section in the Lunatic questionnaire which associate
 * responses with a so-called "missing" variable (that is designed to hold the "don't know" or "refusal" information).
 * This processing class creates the "missing" block in the Lunatic questionnaire.
 */
@Slf4j
public class LunaticAddMissingVariables implements ProcessingStep<Questionnaire> {

    private final EnoCatalog enoCatalog;
    private final boolean isMissingVariables;

    private static final String MISSING_RESPONSE_SUFFIX = "_MISSING";

    public LunaticAddMissingVariables(EnoCatalog enoCatalog, boolean isMissingVariables) {
        this.enoCatalog = enoCatalog;
        this.isMissingVariables = isMissingVariables;
    }

    /**
     * Create the missing responses in the lunatic questionnaire.
     * @param lunaticQuestionnaire Lunatic questionnaire.
     */
    public void apply(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.setMissing(isMissingVariables);
        if (!isMissingVariables) {
            log.info("Skip missing variables processing");
            return;
        }
        log.info("Adding missing variables for this lunatic questionnaire");

        // For each component:
        // - Add a missing response value in the component
        // - Create a variable with the same name as the missing response
        // - Create an entry in the missing section with response as key and missing response as value
        // - Create an entry in the missing section with missing response as key and corresponding response(s) as value
        // (Variations depending on the component type but here's the principle.)

        // Note: the concept of missing block and reversed missing block is quite tricky to understand as it is now.
        // This will be properly implemented in Lunatic-Model later on.

        List<ComponentType> components = LunaticUtils.getResponseComponents(lunaticQuestionnaire.getComponents());

        components.forEach(this::processComponentsMissingResponse);

        List<MissingBlock> missingBlocks = createMissingBlocks(components);

        List<VariableType> missingVariables = new ArrayList<>();

        // New list so that we put missing blocks next to the corresponding reversed missing block
        List<MissingBlock> allMissingBlocks = new ArrayList<>();

        missingBlocks.forEach(missingBlock -> {
            VariableType missingVariable = new VariableType();
            missingVariable.setVariableType(VariableTypeEnum.COLLECTED);
            missingVariable.setName(missingBlock.getMissingName());
            missingVariables.add(missingVariable);

            allMissingBlocks.add(missingBlock);
            allMissingBlocks.addAll(createReversedMissingBlocks(missingBlock));
        });

        if (!allMissingBlocks.isEmpty()) {
            MissingType missingType = new MissingType();
            missingType.getAny().addAll(allMissingBlocks);

            lunaticQuestionnaire.setMissingBlock(missingType);
            lunaticQuestionnaire.getVariables().addAll(missingVariables);
        }
    }

    /**
     * set missing response for a component
     * @param component set missing response for this component
     */
    private void processComponentsMissingResponse(ComponentType component) {

        switch (component.getComponentType()) {

            case LOOP -> {
                Loop loop = (Loop) component;

                // For paginated loops, missing responses are generated on the loop components
                if (Boolean.TRUE.equals(loop.getPaginatedLoop())) {
                    LunaticUtils.getResponseComponents(loop.getComponents()).forEach(this::processComponentsMissingResponse);
                    return;
                }

                // For non-paginated loop, missing response is generated on the loop component
                // (!!!) we assume the loop contains a simple question (not roster, table, checkbox group, ...)
                String firstResponseName = loop.getComponents().stream()
                        .filter(ComponentSimpleResponseType.class::isInstance)
                        .map(ComponentSimpleResponseType.class::cast)
                        .map(ComponentSimpleResponseType::getResponse)
                        .map(ResponseType::getName)
                        .findFirst()
                        .orElseThrow(() -> new LunaticLoopException(String.format(
                                "Main loop '%s' does not have a simple question in its components.", loop.getId())));
                setMissingResponse(loop, firstResponseName);
            }

            case PAIRWISE_LINKS -> {
                ComponentType pairwiseInnerComponent = LunaticUtils.getPairwiseInnerComponent((PairwiseLinks) component);
                String pairwiseResponseName = ((ComponentSimpleResponseType) pairwiseInnerComponent).getResponse().getName();
                setMissingResponse(pairwiseInnerComponent, pairwiseResponseName);
            }

            default -> {
                Question question = enoCatalog.getQuestion(component.getId());
                setMissingResponse(component, question.getName());
            }
        }
    }

    /**
     * Set the missing response attribute of the given component. The resulting missing response name is the
     * concatenation of the given prefix and the common missing response suffix.
     * @param component A Lunatic component.
     * @param missingResponsePrefix Business name that corresponds to the component.
     */
    private void setMissingResponse(ComponentType component, String missingResponsePrefix) {
        ResponseType missingResponse = new ResponseType();
        missingResponse.setName(missingResponsePrefix + MISSING_RESPONSE_SUFFIX);
        component.setMissingResponse(missingResponse);
    }

    /**
     * Create missing block entries, which associate a missing response name to response names,
     * from the components given.
     * @param components List of components used to generate missing block entries.
     * @return list of missing blocks
     */
    private List<MissingBlock> createMissingBlocks(List<ComponentType> components) {
        List<MissingBlock> missingBlocks = new ArrayList<>();
        // generate blocks on components with missing response attribute (included main loop)
        missingBlocks.addAll(components.stream()
                .filter(componentType -> componentType.getMissingResponse() != null)
                .map(component -> {
                    String missingResponseName = component.getMissingResponse().getName();
                    List<String> names = LunaticUtils.getResponseNames(component);
                    return new MissingBlock(missingResponseName, names);
                })
                .toList());

        // generate blocks for subcomponents on linked loop
        missingBlocks.addAll(components.stream()
                .filter(componentType -> componentType.getComponentType().equals(ComponentTypeEnum.LOOP))
                .map(Loop.class::cast)
                .filter(Loop::getPaginatedLoop)
                .map(linkedLoop -> createMissingBlocks(LunaticUtils.getResponseComponents(linkedLoop.getComponents())))
                .flatMap(Collection::stream)
                .toList());

        // generate blocks for subcomponents on pairwise links
        missingBlocks.addAll(components.stream()
                .filter(componentType -> componentType.getComponentType().equals(ComponentTypeEnum.PAIRWISE_LINKS))
                .map(PairwiseLinks.class::cast)
                .map(pairwiseLinks -> createMissingBlocks(LunaticUtils.getResponseComponents(pairwiseLinks.getComponents())))
                .flatMap(Collection::stream)
                .toList());

        return missingBlocks;
    }

    /**
     * Create the reversed missing block entries, which associate a response name to a missing response name,
     * from the given missing block.
     * @param missingBlock Missing block from which we need to create reversed missing blocks.
     * @return List of reversed missing blocks.
     */
    private List<MissingBlock> createReversedMissingBlocks(MissingBlock missingBlock) {
        return missingBlock.getNames().stream()
                .map(name -> new MissingBlock(name, List.of(missingBlock.getMissingName())))
                .toList();
    }

}
