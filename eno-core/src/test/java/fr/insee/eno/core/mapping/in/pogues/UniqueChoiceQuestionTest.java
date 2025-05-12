package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.converter.PoguesConverter;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.pogues.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UniqueChoiceQuestionTest {

    private QuestionType poguesUCQ;
    private UniqueChoiceQuestion enoUCQ;

    @BeforeEach
    void createPoguesUniqueChoiceQuestion() {
        poguesUCQ = new QuestionType();
        poguesUCQ.setQuestionType(QuestionTypeEnum.SINGLE_CHOICE);
        poguesUCQ.setName("UCQ_NAME");
        poguesUCQ.getLabel().add("Unique choice question label.");
        ResponseType response = new ResponseType();
        response.setCodeListReference("code-list-id");
        TextDatatypeType datatype = new TextDatatypeType();
        datatype.setVisualizationHint(VisualizationHintEnum.RADIO);
        response.setDatatype(datatype);
        poguesUCQ.getResponse().add(response);
        poguesUCQ.getResponse().getFirst().setMandatory(Boolean.FALSE);
    }

    @Test
    void poguesMapping() {
        enoUCQ = assertInstanceOf(UniqueChoiceQuestion.class,
                new PoguesConverter().convertToEno(poguesUCQ, Question.class));
        new PoguesMapper().mapInputObject(poguesUCQ, enoUCQ);
    }

    @Test
    void poguesMappingFromQuestionnaire() {
        Questionnaire poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);
        poguesSequence.getChild().add(poguesUCQ);
        poguesQuestionnaire.getChild().add(poguesSequence);

        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        enoUCQ = assertInstanceOf(UniqueChoiceQuestion.class, enoQuestionnaire.getSingleResponseQuestions().getFirst());
    }

    @AfterEach
    void unitTests() {
        assertEquals("UCQ_NAME", enoUCQ.getName());
        assertEquals("Unique choice question label.", enoUCQ.getLabel().getValue());
        assertEquals(UniqueChoiceQuestion.DisplayFormat.RADIO, enoUCQ.getDisplayFormat());
        assertEquals("code-list-id", enoUCQ.getCodeListReference());
        assertFalse(enoUCQ.getMandatory());
    }

}
