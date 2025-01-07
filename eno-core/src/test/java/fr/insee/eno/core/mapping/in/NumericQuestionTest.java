package fr.insee.eno.core.mapping.in;

import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.InMapper;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NumericQuestionTest {

    private static Stream<Arguments> integrationTest() throws ParsingException {
        return Stream.of(
                Arguments.of(Format.POGUES, new PoguesMapper(), PoguesDeserializer.deserialize(
                        NumericQuestionTest.class.getClassLoader().getResourceAsStream(
                                "integration/pogues/pogues-dynamic-unit.json"))),
                Arguments.of(Format.DDI, new DDIMapper(), DDIDeserializer.deserialize(
                        NumericQuestionTest.class.getClassLoader().getResourceAsStream(
                                "integration/ddi/ddi-dynamic-unit.xml")).getDDIInstance())
        );
    }
    @ParameterizedTest
    @MethodSource
    void integrationTest(Format inFormat, InMapper inMapper, Object inputObject) {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        inMapper.mapInputObject(inputObject, enoQuestionnaire);

        //
        NumericQuestion numericQuestion1 = assertInstanceOf(NumericQuestion.class,
                enoQuestionnaire.getSingleResponseQuestions().get(0));
        NumericQuestion numericQuestion2 = assertInstanceOf(NumericQuestion.class,
                enoQuestionnaire.getSingleResponseQuestions().get(1));
        NumericQuestion numericQuestion3 = assertInstanceOf(NumericQuestion.class,
                enoQuestionnaire.getSingleResponseQuestions().get(3));

        assertEquals("NUMBER_NO_UNIT", numericQuestion1.getName());
        assertEquals("NUMBER_FIXED_UNIT", numericQuestion2.getName());
        assertEquals("NUMBER_DYNAMIC_UNIT", numericQuestion3.getName());

        assertEquals("\"Number question with no unit\"", numericQuestion1.getLabel().getValue());
        assertEquals("\"Number question with fixed unit\"", numericQuestion2.getLabel().getValue());
        assertEquals("\"Number question with dynamic unit\"", numericQuestion3.getLabel().getValue());

        assertEquals(0d, numericQuestion1.getMinValue());
        assertEquals(10d, numericQuestion1.getMaxValue());
        assertEquals(BigInteger.ZERO, numericQuestion1.getNumberOfDecimals());
        assertEquals(1d, numericQuestion2.getMinValue());
        assertEquals(10d, numericQuestion2.getMaxValue());
        assertEquals(BigInteger.ZERO, numericQuestion2.getNumberOfDecimals());
        assertEquals(1d, numericQuestion3.getMinValue());
        assertEquals(10d, numericQuestion3.getMaxValue());
        assertEquals(BigInteger.ZERO, numericQuestion3.getNumberOfDecimals());

        assertNull(numericQuestion1.getUnit());
        if (inFormat == Format.POGUES) assertEquals("€", numericQuestion2.getUnit().getValue());
        if (inFormat == Format.POGUES) assertEquals("$WHICH_UNIT$", numericQuestion3.getUnit().getValue());

        assertEquals("NUMBER_NO_UNIT", numericQuestion1.getResponse().getVariableName());
        assertEquals("NUMBER_FIXED_UNIT", numericQuestion2.getResponse().getVariableName());
        assertEquals("NUMBER_DYNAMIC_UNIT", numericQuestion3.getResponse().getVariableName());
    }

}
