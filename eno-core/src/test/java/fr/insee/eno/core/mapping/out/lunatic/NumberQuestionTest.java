package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.InToEno;
import fr.insee.eno.core.PoguesDDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticEditLabelTypes;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSortComponents;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NumberQuestionTest {

    @Test
    void integrationTest_unit() throws DDIParsingException {
        // Given + When
        Questionnaire lunaticQuestionnaire = DDIToLunatic.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-variables.xml"))
                .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));
        // Then
        // gather components to look at
        List<InputNumber> inputNumbers = new ArrayList<>();

        for (Object component : lunaticQuestionnaire.getComponents()) {
            if (component instanceof Question question) {

                inputNumbers.addAll(
                        question.getComponents().stream()
                                .filter(componentType -> ComponentTypeEnum.INPUT_NUMBER.equals(componentType.getComponentType()))
                                .map(InputNumber.class::cast)
                                .toList()
                );
            }}
//        List<InputNumber> inputNumbers = lunaticQuestionnaire.getComponents().stream()
//                .filter(componentType -> ComponentTypeEnum.INPUT_NUMBER.equals(componentType.getComponentType()))
//                .map(InputNumber.class::cast)
//                .toList();
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

    private static Stream<Arguments> integrationTest_dynamicUnit() throws ParsingException {
        ClassLoader classLoader = NumberQuestionTest.class.getClassLoader();
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(classLoader.getResourceAsStream(
                "integration/ddi/ddi-dynamic-unit.xml"));
        fr.insee.pogues.model.Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(classLoader.getResourceAsStream(
                "integration/pogues/pogues-dynamic-unit.json"));
        return Stream.of(
                Arguments.of(DDIToEno.fromObject(ddiQuestionnaire))
                //,Arguments.of(new PoguesToEno(), "integration/pogues/pogues-dynamic-unit.json")
                // disabled while questionnaire's structure is not fully mapped in Pogues
                ,Arguments.of(PoguesDDIToEno.fromObjects(poguesQuestionnaire, ddiQuestionnaire))
        );
    }
    @ParameterizedTest
    @MethodSource
    void integrationTest_dynamicUnit(InToEno inToEno) {
        // Given + When
        EnoQuestionnaire enoQuestionnaire = inToEno.transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticEditLabelTypes().apply(lunaticQuestionnaire); // to set unit type to VTL

        // Then
        InputNumber inputNumber0 = (InputNumber) lunaticQuestionnaire.getComponents().get(1);
        InputNumber inputNumber1 = (InputNumber) lunaticQuestionnaire.getComponents().get(2);
        InputNumber inputNumber2 = (InputNumber) lunaticQuestionnaire.getComponents().get(4);
        Table table = (Table) lunaticQuestionnaire.getComponents().get(5);
        RosterForLoop rosterForLoop = (RosterForLoop) lunaticQuestionnaire.getComponents().get(6);

        assertNull(inputNumber0.getUnitWrapper());
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
