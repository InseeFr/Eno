package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.IllegalDDIElementException;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.InMapper;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.response.CodeResponse;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DDIInsertMultipleChoiceLabelsTest {

    @Test
    void failingCase_tooManyCodes() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        CodeList codeList = new CodeList();
        codeList.setId("code-list-id");
        codeList.setName("CODE_LIST_NAME");
        codeList.getCodeItems().add(new CodeItem());
        codeList.getCodeItems().add(new CodeItem());
        enoQuestionnaire.getCodeLists().add(codeList);
        //
        SimpleMultipleChoiceQuestion simpleMCQ = new SimpleMultipleChoiceQuestion();
        simpleMCQ.setId("question-id");
        simpleMCQ.setName("QUESTION_NAME");
        simpleMCQ.setCodeListReference("code-list-id");
        simpleMCQ.getCodeResponses().add(new CodeResponse());
        enoQuestionnaire.getMultipleResponseQuestions().add(simpleMCQ);
        // When + Then
        DDIInsertMultipleChoiceLabels processing = new DDIInsertMultipleChoiceLabels();
        assertThatThrownBy(() -> processing.apply(enoQuestionnaire))
                .isInstanceOf(IllegalDDIElementException.class)
                .hasMessageContaining("code-list-id")
                .hasMessageContaining("CODE_LIST_NAME")
                .hasMessageContaining("question-id")
                .hasMessageContaining("QUESTION_NAME");
    }

    private static Stream<Arguments> integrationTest() throws ParsingException {
        return Stream.of(
                Arguments.of(new DDIMapper(), DDIDeserializer.deserialize(
                        DDIInsertMultipleChoiceLabelsTest.class.getClassLoader().getResourceAsStream(
                                "integration/ddi/ddi-mcq.xml")).getDDIInstance()),
                Arguments.of(new PoguesMapper(), PoguesDeserializer.deserialize(
                        DDIInsertMultipleChoiceLabelsTest.class.getClassLoader().getResourceAsStream(
                                "integration/pogues/pogues-mcq.json")))
        );
    }
    @ParameterizedTest
    @MethodSource
    void integrationTest(InMapper inMapper, Object inputObject) {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        inMapper.mapInputObject(inputObject, enoQuestionnaire);

        //
        new DDIInsertMultipleChoiceLabels().apply(enoQuestionnaire);

        //
        List<SimpleMultipleChoiceQuestion> simpleMCQList = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(SimpleMultipleChoiceQuestion.class::isInstance)
                .map(SimpleMultipleChoiceQuestion.class::cast)
                .toList();
        assertEquals(1, simpleMCQList.size());
        SimpleMultipleChoiceQuestion simpleMCQ = simpleMCQList.getFirst();
        //
        assertEquals("\"Code A\"", simpleMCQ.getCodeResponses().get(0).getLabel().getValue());
        assertEquals("\"Code B\"", simpleMCQ.getCodeResponses().get(1).getLabel().getValue());
        assertEquals("\"Code C\"", simpleMCQ.getCodeResponses().get(2).getLabel().getValue());
        assertEquals("\"Code D\"", simpleMCQ.getCodeResponses().get(3).getLabel().getValue());
    }

}
