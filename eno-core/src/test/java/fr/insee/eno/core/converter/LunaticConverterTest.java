package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.question.*;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticConverterTest {

    @Test
    void convertShortTextQuestion() {
        TextQuestion textQuestion = new TextQuestion();
        textQuestion.setLengthType(TextQuestion.LengthType.SHORT);
        Object result = LunaticConverter.instantiateFromEnoObject(textQuestion);
        assertTrue(result instanceof Input);
    }

    @Test
    void convertLongTextQuestion() {
        TextQuestion textQuestion = new TextQuestion();
        textQuestion.setLengthType(TextQuestion.LengthType.LONG);
        Object result = LunaticConverter.instantiateFromEnoObject(textQuestion);
        assertTrue(result instanceof Textarea);
    }

    @Test
    void convertTextQuestion_noLength_shouldThrowException() {
        TextQuestion textQuestion = new TextQuestion();
        assertThrows(ConversionException.class, () ->
                LunaticConverter.instantiateFromEnoObject(textQuestion));
    }

    @Test
    void convertUniqueChoiceQuestionToRadio() {
        UniqueChoiceQuestion uniqueChoiceQuestion = new UniqueChoiceQuestion();
        uniqueChoiceQuestion.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.RADIO);
        Object result = LunaticConverter.instantiateFromEnoObject(uniqueChoiceQuestion);
        assertTrue(result instanceof Radio);
    }

    @Test
    void convertUniqueChoiceQuestionToDropdown() {
        UniqueChoiceQuestion uniqueChoiceQuestion = new UniqueChoiceQuestion();
        uniqueChoiceQuestion.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.DROPDOWN);
        Object result = LunaticConverter.instantiateFromEnoObject(uniqueChoiceQuestion);
        assertTrue(result instanceof Dropdown);
    }

    @Test
    void convertUniqueChoiceQuestionToCheckboxOne() {
        UniqueChoiceQuestion uniqueChoiceQuestion = new UniqueChoiceQuestion();
        uniqueChoiceQuestion.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.CHECKBOX);
        Object result = LunaticConverter.instantiateFromEnoObject(uniqueChoiceQuestion);
        assertTrue(result instanceof CheckboxOne);
    }

    @Test
    void convertUniqueChoiceQuestion_noFormat_shouldThrowException() {
        UniqueChoiceQuestion uniqueChoiceQuestion = new UniqueChoiceQuestion();
        assertThrows(ConversionException.class, () ->
                LunaticConverter.instantiateFromEnoObject(uniqueChoiceQuestion));
    }

    @Test
    void convertSimpleMultipleChoiceQuestion() {
        SimpleMultipleChoiceQuestion enoSimpleMCQ = new SimpleMultipleChoiceQuestion();
        Object result = LunaticConverter.instantiateFromEnoObject(enoSimpleMCQ);
        assertTrue(result instanceof CheckboxGroup);
    }

    @Test
    void convertComplexMultipleChoiceQuestion() {
        ComplexMultipleChoiceQuestion enoComplexMCQ = new ComplexMultipleChoiceQuestion();
        Object result = LunaticConverter.instantiateFromEnoObject(enoComplexMCQ);
        assertTrue(result instanceof Table);
    }

    @Test
    void convertEnoTableQuestion() {
        TableQuestion enoTableQuestion = new TableQuestion();
        Object result = LunaticConverter.instantiateFromEnoObject(enoTableQuestion);
        assertTrue(result instanceof Table);
    }

    @Test
    void convertEnoDynamicTableQuestion() {
        DynamicTableQuestion enoDynamicTableQuestion = new DynamicTableQuestion();
        Object result = LunaticConverter.instantiateFromEnoObject(enoDynamicTableQuestion);
        assertTrue(result instanceof RosterForLoop);
    }

}
