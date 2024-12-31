package fr.insee.eno.core.mapping.in;

import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.InMapper;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class TextQuestionTest {

    private static Stream<Arguments> integrationTest() throws ParsingException {
        return Stream.of(
                Arguments.of(new DDIMapper(), DDIDeserializer.deserialize(TextQuestionTest.class.getClassLoader()
                        .getResourceAsStream("integration/ddi/ddi-simple.xml")).getDDIInstance()),
                Arguments.of(new PoguesMapper(), PoguesDeserializer.deserialize(TextQuestionTest.class.getClassLoader()
                        .getResourceAsStream("integration/pogues/pogues-simple.json")))
        );
    }
    @ParameterizedTest
    @MethodSource
    void integrationTest(InMapper inMapper, Object inputObject) {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        inMapper.mapInputObject(inputObject, enoQuestionnaire);
        //
        assertEquals(1, enoQuestionnaire.getSingleResponseQuestions().size());
        TextQuestion textQuestion = assertInstanceOf(TextQuestion.class,
                enoQuestionnaire.getSingleResponseQuestions().getFirst());
        assertEquals("lmyo3e0y", textQuestion.getId());
        assertEquals("Q1", textQuestion.getName());
        assertEquals("\"Unique question\"", textQuestion.getLabel().getValue());
        assertEquals("Q1", textQuestion.getResponse().getVariableName());
        assertEquals(BigInteger.valueOf(249), textQuestion.getMaxLength());
        assertEquals(TextQuestion.LengthType.SHORT, textQuestion.getLengthType());
    }

}
