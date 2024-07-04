package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.serialize.DDIDeserializer;
import instance33.DDIInstanceDocument;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DDIInsertMultipleChoiceLabelsTest {

    @Test
    void integrationTest() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIInstanceDocument ddiInstance = DDIDeserializer.deserialize(
                DDIInsertDetailResponsesTest.class.getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-other-specify.xml"));
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(ddiInstance, enoQuestionnaire);
        new DDIInsertDetailResponses().apply(enoQuestionnaire);

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
        assertEquals("\"Option A\"", simpleMCQ.getCodeResponses().get(0).getLabel().getValue());
        assertEquals("\"Option B\"", simpleMCQ.getCodeResponses().get(1).getLabel().getValue());
        assertEquals("\"Option C (with detail)\"", simpleMCQ.getCodeResponses().get(2).getLabel().getValue());
        assertEquals("\"Option D (with detail)\"", simpleMCQ.getCodeResponses().get(3).getLabel().getValue());
    }

}
