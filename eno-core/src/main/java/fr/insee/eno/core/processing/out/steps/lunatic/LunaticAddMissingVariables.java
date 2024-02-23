package fr.insee.eno.core.processing.out.steps.lunatic;

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

        components.forEach(component -> processComponentsMissingResponse(component, lunaticQuestionnaire));

        List<MissingEntry> missingBlocks = createMissingBlocks(components);
        // New list so that we put missing blocks next to the corresponding reversed missing block
        List<MissingEntry> allMissingBlocks = new ArrayList<>();
        missingBlocks.forEach(missingBlock -> {
            allMissingBlocks.add(missingBlock);
            allMissingBlocks.addAll(createReversedMissingBlocks(missingBlock));
        });

        if (!allMissingBlocks.isEmpty()) {
            MissingType missingType = new MissingType();
            allMissingBlocks.forEach(missingType::addMissingEntry);
            lunaticQuestionnaire.setMissingBlock(missingType);
        }
    }

    /**
     * set missing response for a component
     * @param component set missing response for this component
     */
    private void processComponentsMissingResponse(ComponentType component, Questionnaire lunaticQuestionnaire) {

        switch (component.getComponentType()) {

            case LOOP -> {
                Loop loop = (Loop) component;
                LunaticUtils.getResponseComponents(loop.getComponents()).forEach(loopComponent -> {
                    Question question = enoCatalog.getQuestion(loopComponent.getId());
                    String missingResponseName = setMissingResponse(loopComponent, question.getName());
                    addMissingVariable(new VariableTypeArray(), missingResponseName, lunaticQuestionnaire);
                });
            }

            case ROSTER_FOR_LOOP -> {
                Question question = enoCatalog.getQuestion(component.getId());
                String missingResponseName = setMissingResponse(component, question.getName());
                addMissingVariable(new VariableTypeArray(), missingResponseName, lunaticQuestionnaire);
            }

            case PAIRWISE_LINKS -> {
                ComponentType pairwiseInnerComponent = LunaticUtils.getPairwiseInnerComponent((PairwiseLinks) component);
                String pairwiseResponseName = ((ComponentSimpleResponseType) pairwiseInnerComponent).getResponse().getName();
                String missingResponseName = setMissingResponse(pairwiseInnerComponent, pairwiseResponseName);
                addMissingVariable(new VariableTypeTwoDimensionsArray(), missingResponseName, lunaticQuestionnaire);
            }

            default -> {
                Question question = enoCatalog.getQuestion(component.getId());
                String missingResponseName = setMissingResponse(component, question.getName());
                addMissingVariable(new VariableType(), missingResponseName, lunaticQuestionnaire);
            }
        }
    }

    /**
     * Set the missing response attribute of the given component. The resulting missing response name is the
     * concatenation of the given prefix and the common missing response suffix.
     * @param component A Lunatic component.
     * @param missingResponsePrefix Business name that corresponds to the component.
     * @return The missing response name.
     */
    private String setMissingResponse(ComponentType component, String missingResponsePrefix) {
        ResponseType missingResponse = new ResponseType();
        String missingResponseName = missingResponsePrefix + MISSING_RESPONSE_SUFFIX;
        missingResponse.setName(missingResponseName);
        component.setMissingResponse(missingResponse);
        return missingResponseName;
    }

    private void addMissingVariable(IVariableType variable, String missingResponseName,
                                    Questionnaire lunaticQuestionnaire) {
        variable.setName(missingResponseName);
        variable.setVariableType(VariableTypeEnum.COLLECTED);
        lunaticQuestionnaire.getVariables().add(variable);
    }

    /**
     * Create missing block entries, which associate a missing response name to response names,
     * from the components given.
     * @param components List of components used to generate missing block entries.
     * @return list of missing blocks
     */
    private List<MissingEntry> createMissingBlocks(List<ComponentType> components) {
        List<MissingEntry> missingBlocks = new ArrayList<>();
        // generate blocks on components with missing response attribute (included main loop)
        missingBlocks.addAll(components.stream()
                .filter(componentType -> componentType.getMissingResponse() != null)
                .map(component -> {
                    String missingResponseName = component.getMissingResponse().getName();
                    List<String> names = LunaticUtils.getResponseNames(component);
                    MissingEntry missingEntry = new MissingEntry(missingResponseName);
                    names.forEach(name -> missingEntry.getCorrespondingVariables().add(name));
                    return missingEntry;
                })
                .toList());

        // generate blocks for subcomponents on loop
        missingBlocks.addAll(components.stream()
                .filter(componentType -> componentType.getComponentType().equals(ComponentTypeEnum.LOOP))
                .map(Loop.class::cast)
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
    private List<MissingEntry> createReversedMissingBlocks(MissingEntry missingBlock) {
        return missingBlock.getCorrespondingVariables().stream()
                .map(name -> {
                    MissingEntry missingEntry = new MissingEntry(name);
                    missingEntry.getCorrespondingVariables().add(missingBlock.getVariableName());
                    return missingEntry;
                })
                .toList();
    }

}
