package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.exceptions.business.LunaticProcessingException;
import fr.insee.eno.core.model.lunatic.MissingBlock;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.lunatic.model.flat.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class LunaticAddMissingVariables implements OutProcessingInterface<Questionnaire> {

    private EnoCatalog enoCatalog;
    private boolean isMissingVariables;

    private static final String MISSING_RESPONSE_SUFFIX = "_MISSING";

    public void apply(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.setMissing(isMissingVariables);
        if (!isMissingVariables) {
            return;
        }
        List<ComponentType> components = lunaticQuestionnaire.getComponents();

        components.forEach(this::createMissingResponse);
        List<MissingBlock> missingBlocks = createMissingBlocks(lunaticQuestionnaire.getComponents());

        List<VariableType> missingVariables = new ArrayList<>();
        List<MissingBlock> allMissingBlocks = new ArrayList<>();

        missingBlocks.forEach(missingBlock -> {
            VariableType missingVariable = new VariableType();
            missingVariable.setVariableType(VariableTypeEnum.COLLECTED);
            missingVariable.setName(missingBlock.getMissingName());
            missingVariables.add(missingVariable);

            allMissingBlocks.add(missingBlock);
            allMissingBlocks.addAll(createInversedMissingBlocks(missingBlock));
        });

        if(!allMissingBlocks.isEmpty()) {
            MissingType missingType = new MissingType();
            missingType.getAny().addAll(allMissingBlocks);

            lunaticQuestionnaire.setMissingBlock(missingType);
            lunaticQuestionnaire.getVariables().addAll(missingVariables);
        }
    }

    private List<MissingBlock> createInversedMissingBlocks(MissingBlock missingBlock) {
        return missingBlock.getNames().stream()
                .map(name -> new MissingBlock(name, List.of(missingBlock.getMissingName())))
                .toList();
    }

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
                .map(linkedLoop -> createMissingBlocks(linkedLoop.getComponents()))
                .flatMap(Collection::stream)
                .toList());

        // generate blocks for subcomponents on pairwiselinks
        missingBlocks.addAll(components.stream()
                .filter(componentType -> componentType.getComponentType().equals(ComponentTypeEnum.PAIRWISE_LINKS))
                .map(PairwiseLinks.class::cast)
                .map(pairwiseLinks -> createMissingBlocks(pairwiseLinks.getComponents()))
                .flatMap(Collection::stream)
                .toList());

        return missingBlocks;
    }

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
                            .filter(subcomponent -> subcomponent.getResponse() != null)
                            .map(subcomponent -> subcomponent.getResponse().getName())
                            .toList();
            case LOOP ->
                    names = ((Loop)component).getComponents().stream()
                            .map(this::getMissingBlockNames)
                            .flatMap(Collection::stream)
                            .toList();
            case TABLE ->
                    names = ((Table)component).getBody().stream()
                            .map(BodyType::getBodyLine)
                            .flatMap(Collection::stream)
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

    private void createMissingResponse(ComponentType component) {
        String missingResponseName = null;

        switch(component.getComponentType()) {
            case LOOP -> {
                Loop loop = (Loop) component;
                // when linked loop, missing responses are generated on the loop components
                if(isLinkedLoop(loop)) {
                    ((Loop) component).getComponents().forEach(this::createMissingResponse);
                    return;
                }

                //on main loop, missing response is generated on the loop component
                // /!\ we assume the first question component found is a simple question (not roster, table, checkboxgroup, ...)
                missingResponseName = loop.getComponents().stream()
                        .filter(ComponentSimpleResponseType.class::isInstance)
                        .map(ComponentSimpleResponseType.class::cast)
                        .map(ComponentSimpleResponseType::getResponse)
                        .map(ResponseType::getName)
                        .findFirst()
                        .orElseThrow(() -> new LunaticProcessingException(String.format("main loop %s does not have a simple question in his components", loop.getId())));
            }

            // missing responses are handled on the components of pairwise
            case PAIRWISE_LINKS -> ((PairwiseLinks) component).getComponents().forEach(this::createMissingResponse);

            default -> missingResponseName = enoCatalog.getQuestion(component.getId()).getName();

        }

        if(missingResponseName != null) {
            missingResponseName += MISSING_RESPONSE_SUFFIX;
            ResponseType missingResponse = new ResponseType();
            missingResponse.setName(missingResponseName);
            component.setMissingResponse(missingResponse);
        }
    }

    private boolean isLinkedLoop(Loop loop) {
        return loop.getLines() == null;
    }
}
