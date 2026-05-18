package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticDropdownOptionLabelsTest {

    @Test
    void unitTest() {
        // Given
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Dropdown dropdown = new Dropdown();
        dropdown.getOptions().addAll(threeDropdownOptions());
        lunaticQuestionnaire.getComponents().add(dropdown);

        Table table = new Table();
        table.getBodyLines().add(new BodyLine());
        BodyCell dropdownCell = new BodyCell();
        dropdownCell.setComponentType(ComponentTypeEnum.DROPDOWN);
        dropdownCell.getOptions().addAll(threeDropdownOptions());
        table.getBodyLines().getFirst().getBodyCells().add(dropdownCell);
        lunaticQuestionnaire.getComponents().add(table);

        RosterForLoop rosterForLoop = new RosterForLoop();
        BodyCell dropdownDynamicCell = new BodyCell();
        dropdownDynamicCell.setComponentType(ComponentTypeEnum.DROPDOWN);
        dropdownDynamicCell.getOptions().addAll(threeDropdownOptions());
        rosterForLoop.getComponents().add(dropdownDynamicCell);
        lunaticQuestionnaire.getComponents().add(rosterForLoop);

        // When
        new LunaticDropdownOptionLabels().apply(lunaticQuestionnaire);

        // Then
        assertLabelsAreVTL(dropdown.getOptions());
        assertLabelsAreVTL(dropdownCell.getOptions());
        assertLabelsAreVTL(dropdownDynamicCell.getOptions());
    }

    /** Just to instantiate some option objects. The label type is not set. */
    private static List<Option> threeDropdownOptions() {
        return List.of(createOptionWithLabel(), createOptionWithLabel(), createOptionWithLabel());
    }
    /** Instantiates an option object with a non-null label. */
    private static Option createOptionWithLabel() {
        Option option = new Option();
        option.setLabel(new LabelType());
        return option;
    }

    private static void assertLabelsAreVTL(List<Option> options) {
        options.forEach(option -> assertEquals(LabelTypeEnum.VTL, option.getLabel().getType()));
    }

}
