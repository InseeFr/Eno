package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.response.ModalityAttachment;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleMultipleChoiceQuestionTest {

    @Test
    void mapSimpleMCQ_withDetailResponses() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-other-specify.xml")),
                enoQuestionnaire);
        //
        List<SimpleMultipleChoiceQuestion> simpleMCQList = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(SimpleMultipleChoiceQuestion.class::isInstance)
                .map(SimpleMultipleChoiceQuestion.class::cast)
                .toList();
        assertEquals(1, simpleMCQList.size());
        SimpleMultipleChoiceQuestion simpleMCQ = simpleMCQList.getFirst();
        assertEquals("MCQ", simpleMCQ.getName());
        assertEquals("lutkfklf", simpleMCQ.getCodeListReference());
        // After mapping: 4 proper modality code responses + 2 detail code responses (to be moved by a processing)
        assertEquals(6, simpleMCQ.getCodeResponses().size());
        //
        assertEquals(6, simpleMCQ.getDdiBindings().size());
        //
        assertEquals(4, simpleMCQ.getModalityAttachments().stream()
                .filter(ModalityAttachment.CodeAttachment.class::isInstance).count());
        assertEquals(2, simpleMCQ.getModalityAttachments().stream()
                .filter(ModalityAttachment.DetailAttachment.class::isInstance).count());
    }

}
