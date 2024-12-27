package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticEditLabelTypes;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSortComponents;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NumberQuestionTest {

    @Test
    void integrationTest_unit() throws DDIParsingException {
        // Given + When
        Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-variables.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
        // Then
        // gather components to look at
        List<InputNumber> inputNumbers = lunaticQuestionnaire.getComponents().stream()
                .filter(componentType -> ComponentTypeEnum.INPUT_NUMBER.equals(componentType.getComponentType()))
                .map(InputNumber.class::cast)
                .toList();
        List<InputNumber> inputNumbersNoUnit = inputNumbers.stream()
                .filter(inputNumber -> !"NUMBER_UNIT".equals(inputNumber.getResponse().getName()))
                .toList();
        Optional<InputNumber> inputNumberWithUnit = inputNumbers.stream()
                .filter(inputNumber -> "NUMBER_UNIT".equals(inputNumber.getResponse().getName()))
                .findAny();
        // assertions
        assertEquals(3, inputNumbersNoUnit.size());
        inputNumbersNoUnit.forEach(inputNumber -> assertNull(inputNumber.getUnitLabel()));
        assertTrue(inputNumberWithUnit.isPresent());
        assertNotNull(inputNumberWithUnit.get().getUnitLabel());
        assertEquals("\"kg\"", inputNumberWithUnit.get().getUnitLabel().getValue());
    }

    @Test
    void integrationTest_dynamicUnit() throws DDIParsingException {
        // Given + When
        EnoQuestionnaire enoQuestionnaire = new DDIToEno().transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-dynamic-unit.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticEditLabelTypes().apply(lunaticQuestionnaire); // to set unit type to VTL

        // Then
        InputNumber inputNumber1 = (InputNumber) lunaticQuestionnaire.getComponents().get(1);
        InputNumber inputNumber2 = (InputNumber) lunaticQuestionnaire.getComponents().get(3);
        Table table = (Table) lunaticQuestionnaire.getComponents().get(4);
        RosterForLoop rosterForLoop = (RosterForLoop) lunaticQuestionnaire.getComponents().get(5);

        testUnitContent("\"€\"", inputNumber1.getUnitLabel());
        testUnitContent("WHICH_UNIT", inputNumber2.getUnitLabel());

        // Reminder: in Lunatic tables there is a left column so responses cells start at column of index 1
        testUnitContent("\"%\"", table.getBodyLines().get(0).getBodyCells().get(1).getUnitLabel());
        testUnitContent("\"%\"", table.getBodyLines().get(1).getBodyCells().get(1).getUnitLabel());
        testUnitContent("WHICH_UNIT", table.getBodyLines().get(0).getBodyCells().get(2).getUnitLabel());
        testUnitContent("WHICH_UNIT", table.getBodyLines().get(1).getBodyCells().get(2).getUnitLabel());

        testUnitContent("\"€\"", rosterForLoop.getComponents().get(0).getUnitLabel());
        testUnitContent("WHICH_UNIT", rosterForLoop.getComponents().get(1).getUnitLabel());
    }
    private void testUnitContent(String expectedValue, LabelType unitLabel) {
        assertEquals(expectedValue, unitLabel.getValue().trim());
        assertEquals(LabelTypeEnum.VTL, unitLabel.getType());
    }

}
