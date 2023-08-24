package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.business.LunaticProcessingException;
import fr.insee.eno.core.model.lunatic.MissingBlock;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.lunatic.model.flat.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Slf4j
public class LunaticAddMissingVariables implements ProcessingStep<Questionnaire> {

    private EnoCatalog enoCatalog;
    private boolean isMissingVariables;

    private static final String MISSING_RESPONSE_SUFFIX = "_MISSING";

    /**
     * process missing responses on lunatic questionnaire
     * @param lunaticQuestionnaire Out object to be processed.
     */
    public void apply(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.setMissing(isMissingVariables);
        if (!isMissingVariables) {
            log.info("Skip missing variables processing");
            return;
        }
        log.info("Adding missing variables for this lunatic questionnaire");

        List<ComponentType> components = filterComponentsToProcess(lunaticQuestionnaire.getComponents());

        components.forEach(this::setComponentMissingResponse);
        List<MissingBlock> missingBlocks = createMissingBlocks(components);

        List<VariableType> missingVariables = new ArrayList<>();
        List<MissingBlock> allMissingBlocks = new ArrayList<>();

        missingBlocks.forEach(missingBlock -> {
            VariableType missingVariable = new VariableType();
            missingVariable.setVariableType(VariableTypeEnum.COLLECTED);
            missingVariable.setName(missingBlock.getMissingName());
            missingVariables.add(missingVariable);

            allMissingBlocks.add(missingBlock);
            allMissingBlocks.addAll(createReversedMissingBlocks(missingBlock));
        });

        if(!allMissingBlocks.isEmpty()) {
            MissingType missingType = new MissingType();
            missingType.getAny().addAll(allMissingBlocks);

            lunaticQuestionnaire.setMissingBlock(missingType);
            lunaticQuestionnaire.getVariables().addAll(missingVariables);
        }
    }

    /**
     * create the reversed missing blocks from a missing block
     * @param missingBlock missing block from which we ne to create reversed missing blocks
     * @return list of reversed missing blocks
     */
    private List<MissingBlock> createReversedMissingBlocks(MissingBlock missingBlock) {
        return missingBlock.getNames().stream()
                .map(name -> new MissingBlock(name, List.of(missingBlock.getMissingName())))
                .toList();
    }

    /**
     * create missing blocks from components
     * @param components list of components to process
     * @return list of missing blocks
     */
    private List<MissingBlock> createMissingBlocks(List<ComponentType> components) {
        List<MissingBlock> missingBlocks = new ArrayList<>();
        // generate blocks on components with missing response attribute (included main loop)
        missingBlocks.addAll(components.stream()
                .filter(componentType -> componentType.getMissingResponse() != null)
                .map(component -> {
                    String missingResponseName = component.getMissingResponse().getName();
                    List<String> names = getMissingBlockNames(component);
                    return new MissingBlock(missingResponseName, names);
                })
                .toList());

        // generate blocks for subcomponents on linked loop
        missingBlocks.addAll(components.stream()
                .filter(componentType -> componentType.getComponentType().equals(ComponentTypeEnum.LOOP))
                .map(Loop.class::cast)
                .filter(this::isLinkedLoop)
                .map(linkedLoop -> createMissingBlocks(filterComponentsToProcess(linkedLoop.getComponents())))
                .flatMap(Collection::stream)
                .toList());

        // generate blocks for subcomponents on pairwiselinks
        missingBlocks.addAll(components.stream()
                .filter(componentType -> componentType.getComponentType().equals(ComponentTypeEnum.PAIRWISE_LINKS))
                .map(PairwiseLinks.class::cast)
                .map(pairwiseLinks -> createMissingBlocks(filterComponentsToProcess(pairwiseLinks.getComponents())))
                .flatMap(Collection::stream)
                .toList());

        return missingBlocks;
    }

    /**
     * Extract the names of a missing block from a component
     * @param component component which we extract missing block names
     * @return list of names
     */
    private List<String> getMissingBlockNames(ComponentType component) {
        List<String> names;
        switch(component.getComponentType()) {
            case CHECKBOX_GROUP ->
                    names = ((CheckboxGroup)component).getResponses().stream()
                            .map(ResponsesCheckboxGroup::getResponse)
                            .map(ResponseType::getName)
                            .toList();
            case ROSTER_FOR_LOOP ->
                    names = ((RosterForLoop)component).getComponents().stream()
                            .filter(Objects::nonNull)
                            .filter(subcomponent -> subcomponent.getResponse() != null)
                            .map(subcomponent -> subcomponent.getResponse().getName())
                            .toList();
            case LOOP ->
                    names = filterComponentsToProcess(((Loop)component).getComponents()).stream()
                            .map(this::getMissingBlockNames)
                            .flatMap(Collection::stream)
                            .toList();
            case TABLE ->
                    names = ((Table)component).getBodyLines().stream()
                            .map(BodyLine::getBodyCells)
                            .flatMap(Collection::stream)
                            .filter(Objects::nonNull)
                            .filter(subcomponent -> subcomponent.getResponse() != null)
                            .map(subcomponent -> subcomponent.getResponse().getName())
                            .toList();
            default -> {
                ComponentSimpleResponseType simpleResponseComponent = (ComponentSimpleResponseType) component;
                names = List.of(simpleResponseComponent.getResponse().getName());
            }
        }
        return names;
    }

    /**
     * set missing response for a component
     * @param component set missing response for this component
     */
    private void setComponentMissingResponse(ComponentType component) {
        String missingResponseName = null;

        switch(component.getComponentType()) {
            case LOOP -> {
                Loop loop = (Loop) component;
                // when linked loop, missing responses are generated on the loop components
                if(isLinkedLoop(loop)) {
                    filterComponentsToProcess(loop.getComponents()).forEach(this::setComponentMissingResponse);
                    return;
                }

                //on main loop, missing response is generated on the loop component
                // /!\ we assume the first question component found is a simple question (not roster, table, checkboxgroup, ...)
                /*missingResponseName = loop.getComponents().stream()
                        .filter(ComponentSimpleResponseType.class::isInstance)
                        .map(ComponentSimpleResponseType.class::cast)
                        .map(ComponentSimpleResponseType::getResponse)
                        .map(ResponseType::getName)
                        .findFirst()
                        .orElseThrow(() -> new LunaticProcessingException(String.format("main loop %s does not have a simple question in his components", loop.getId())));*/
            }

            // missing responses are handled on the components of pairwise
            case PAIRWISE_LINKS -> ((PairwiseLinks) component).getComponents().forEach(this::setComponentMissingResponse);

            default -> missingResponseName = enoCatalog.getQuestion(component.getId()).getName();
        }

        if(missingResponseName != null) {
            missingResponseName += MISSING_RESPONSE_SUFFIX;
            ResponseType missingResponse = new ResponseType();
            missingResponse.setName(missingResponseName);
            component.setMissingResponse(missingResponse);
        }
    }

    /**
     * filter components to process (only questions/loops)
     * @param components components needing filtering
     * @return filtered components
     */
    private List<ComponentType> filterComponentsToProcess(List<ComponentType> components) {
        return components.stream()
                .filter(component -> !component.getComponentType().equals(ComponentTypeEnum.SEQUENCE))
                .filter(component -> !component.getComponentType().equals(ComponentTypeEnum.SUBSEQUENCE))
                .filter(component -> !component.getComponentType().equals(ComponentTypeEnum.FILTER_DESCRIPTION))
                .toList();
    }

    /**
     * Check if loop is a main or linked loop
     * @param loop loop to check
     * @return true if linked loop, false otherwise
     */
    private boolean isLinkedLoop(Loop loop) {
        return loop.getLines() == null;
    }
}
