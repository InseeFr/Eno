package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

import java.util.List;

/** Temporary processing to change the label types in certain Lunatic components. */
public class LunaticEditLabelTypes implements ProcessingStep<Questionnaire> {

    /**
     * Changes the label type of certain components within the Lunatic questionnaire.
     * @param lunaticQuestionnaire A Lunatic questionnaire.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        editLabels(lunaticQuestionnaire.getComponents());
        //
        lunaticQuestionnaire.getComponents().stream()
                .filter(ComponentNestingType.class::isInstance).map(ComponentNestingType.class::cast)
                .map(ComponentNestingType::getComponents)
                .forEach(this::editLabels);
        //
        lunaticQuestionnaire.getComponents().stream()
                .filter(RosterForLoop.class::isInstance).map(RosterForLoop.class::cast)
                .map(RosterForLoop::getComponents)
                .forEach(this::editDropdownCellLabels);
    }

    private void editLabels(List<ComponentType> lunaticComponents) {
        lunaticComponents.stream()
                .filter(Dropdown.class::isInstance).map(Dropdown.class::cast)
                .forEach(this::editDropdownLabels);
        lunaticComponents.stream()
                .filter(Sequence.class::isInstance).map(Sequence.class::cast)
                .forEach(this::editSequenceLabel);
    }

    private void editSequenceLabel(Sequence sequence) {
        sequence.getLabel().setType(LabelTypeEnum.VTL);
    }

    private void editDropdownLabels(Dropdown dropdown) {
        dropdown.getOptions().forEach(options -> options.getLabel().setType(LabelTypeEnum.VTL));
    }

    private void editDropdownCellLabels(List<BodyCell> bodyCells) {
        bodyCells.stream()
                .filter(bodyCell -> ComponentTypeEnum.DROPDOWN.equals(bodyCell.getComponentType()))
                .forEach(this::editDropdownCellLabels);
    }

    private void editDropdownCellLabels(BodyCell dropdownCell) {
        dropdownCell.getOptions().forEach(options -> options.getLabel().setType(LabelTypeEnum.VTL));
    }

}
