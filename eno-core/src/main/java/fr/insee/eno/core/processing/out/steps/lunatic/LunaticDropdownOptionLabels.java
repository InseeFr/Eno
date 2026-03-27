package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.lunatic.LunaticQuestionHelper;
import fr.insee.eno.core.utils.lunatic.LunaticTablesHelper;
import fr.insee.lunatic.model.flat.*;

import java.util.stream.Stream;

/**
 * Many Lunatic objects use the "Label" object in various properties.
 * In almost every case, a Lunatic label is a dynamic VTL string, that can be interpreted as Markdown once evaluated.
 * Yet, in dropdowns, only pure text is allowed in options.
 * This processing step finds Lunatic dropdowns and sets the label type of options to 'VTL'
 * (instead of the default 'VTL MD').
 */
public class LunaticDropdownOptionLabels implements ProcessingStep<Questionnaire> {

    /** Changes label type of dropdown options across the questionnaire (including tables). */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {

        Stream<Dropdown> dropdowns = LunaticQuestionHelper.findAllInQuestionnaire(Dropdown.class, lunaticQuestionnaire);
        dropdowns.forEach(this::setOptionTypesToVTL);

        LunaticQuestionHelper.findAllInQuestionnaire(Table.class, lunaticQuestionnaire)
                .flatMap(staticTable ->  LunaticTablesHelper.findCellsOfType(ComponentTypeEnum.DROPDOWN, staticTable))
                .forEach(this::setOptionTypesToVTL);

        LunaticQuestionHelper.findAllInQuestionnaire(RosterForLoop.class, lunaticQuestionnaire)
                .flatMap(dynamicTable ->  LunaticTablesHelper.findCellsOfType(ComponentTypeEnum.DROPDOWN, dynamicTable))
                .forEach(this::setOptionTypesToVTL);
    }

    private void setOptionTypesToVTL(Dropdown dropdown) {
        dropdown.getOptions().forEach(option -> option.getLabel().setType(LabelTypeEnum.VTL));
    }

    private void setOptionTypesToVTL(BodyCell dropdownCell) {
        dropdownCell.getOptions().forEach(option -> option.getLabel().setType(LabelTypeEnum.VTL));
    }

}
