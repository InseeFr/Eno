package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.InputNumber;
import fr.insee.lunatic.model.flat.Questionnaire;
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
                EnoParameters.of(EnoParameters.Context.DEFAULT, Format.LUNATIC, EnoParameters.ModeParameter.CAWI));
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
        inputNumbersNoUnit.forEach(inputNumber -> assertNull(inputNumber.getUnit()));
        assertTrue(inputNumberWithUnit.isPresent());
        assertNotNull(inputNumberWithUnit.get().getUnit());
        assertEquals("kg", inputNumberWithUnit.get().getUnit());
    }

}
