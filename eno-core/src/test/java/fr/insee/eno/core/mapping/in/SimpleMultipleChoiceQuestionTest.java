package fr.insee.eno.core.mapping.in;

import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.InMapper;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleMultipleChoiceQuestionTest {

    private static Stream<Arguments> mapSimpleMCQ() throws ParsingException {
        return Stream.of(
                Arguments.of(new DDIMapper(), DDIDeserializer.deserialize(
                        SimpleMultipleChoiceQuestionTest.class.getClassLoader().getResourceAsStream(
                                "integration/ddi/ddi-mcq.xml")).getDDIInstance()),
                Arguments.of(new PoguesMapper(), PoguesDeserializer.deserialize(
                        SimpleMultipleChoiceQuestionTest.class.getClassLoader().getResourceAsStream(
                        "integration/pogues/pogues-mcq.json")))
        );
    }
    @ParameterizedTest
    @MethodSource
    void mapSimpleMCQ(InMapper inMapper, Object inputObject) {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        inMapper.mapInputObject(inputObject, enoQuestionnaire);
        //
        List<SimpleMultipleChoiceQuestion> simpleMCQList = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(SimpleMultipleChoiceQuestion.class::isInstance)
                .map(SimpleMultipleChoiceQuestion.class::cast)
                .toList();
        assertEquals(1, simpleMCQList.size());
        SimpleMultipleChoiceQuestion simpleMCQ = simpleMCQList.getFirst();
        assertEquals("MCQ_BOOL", simpleMCQ.getName());
        assertEquals(4, simpleMCQ.getCodeResponses().size());
    }

}
