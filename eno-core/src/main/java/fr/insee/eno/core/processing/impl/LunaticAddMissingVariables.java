package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.lunatic.MissingBlock;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.lunatic.model.flat.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class LunaticAddMissingVariables implements OutProcessingInterface<Questionnaire> {
    private boolean isMissingVariables;

    public void apply(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.setMissing(isMissingVariables);
        if (!isMissingVariables) {
            return;
        }
        List<MissingBlock> missingBlocks = getMissingBlocks(lunaticQuestionnaire.getComponents());

        List<VariableType> missingVariables = new ArrayList<>();
        List<MissingBlock> allMissingBlocks = new ArrayList<>();

        missingBlocks.forEach(missingBlock -> {
            VariableType missingVariable = new VariableType();
            missingVariable.setVariableType(VariableTypeEnum.COLLECTED);
            missingVariable.setName(missingBlock.getMissingName());
            missingVariables.add(missingVariable);

            allMissingBlocks.add(missingBlock);
            allMissingBlocks.addAll(getInversedMissingBlocks(missingBlock));
        });

        if(!allMissingBlocks.isEmpty()) {
            MissingType missingType = new MissingType();
            missingType.getAny().addAll(allMissingBlocks);

            lunaticQuestionnaire.setMissingBlock(missingType);
            lunaticQuestionnaire.getVariables().addAll(missingVariables);
        }
    }

    private List<MissingBlock> getInversedMissingBlocks(MissingBlock missingBlock) {
        return missingBlock.getNames().stream()
                .map(name -> new MissingBlock(name, List.of(missingBlock.getMissingName())))
                .toList();
    }

    private List<MissingBlock> getMissingBlocks(List<ComponentType> components) {
        return components.stream()
                .filter(componentType -> componentType.getMissingResponse() != null)
                .map(component -> {
                    String missingResponseName = component.getMissingResponse().getName();
                    List<String> names = getMissingBlockNames(component);
                    return new MissingBlock(missingResponseName, names);
                })
                .toList();
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
            case PAIRWISE_LINKS ->
                    names = ((PairwiseLinks)component).getComponents().stream()
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
            case INPUT -> names = List.of(((Input)component).getResponse().getName());
            case INPUT_NUMBER -> names = List.of(((InputNumber)component).getResponse().getName());
            case TEXTAREA -> names = List.of(((Textarea)component).getResponse().getName());
            case DATEPICKER -> names = List.of(((Datepicker)component).getResponse().getName());
            case CHECKBOX_ONE -> names = List.of(((CheckboxOne)component).getResponse().getName());
            case CHECKBOX_BOOLEAN -> names = List.of(((CheckboxBoolean)component).getResponse().getName());
            case DROPDOWN -> names = List.of(((Dropdown)component).getResponse().getName());
            case RADIO -> names = List.of(((Radio)component).getResponse().getName());
            default -> names = new ArrayList<>();
        }
        return names;
    }
}
