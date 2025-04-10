package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.converter.PoguesConverter;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.pogues.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class TextQuestionTest {

    private QuestionType poguesQuestion;
    private TextQuestion enoQuestion;

    @BeforeEach
    void createPoguesTextQuestion() {
        poguesQuestion = new QuestionType();
        poguesQuestion.setId("question-id");
        poguesQuestion.setQuestionType(QuestionTypeEnum.SIMPLE);
        poguesQuestion.setName("QUESTION_NAME");
        poguesQuestion.getLabel().add("\"Question label.\"");
        poguesQuestion.getResponse().add(new ResponseType());
        poguesQuestion.getResponse().getFirst().setCollectedVariableReference("variable-id");
        TextDatatypeType textDatatype = new TextDatatypeType();
        textDatatype.setTypeName(DatatypeTypeEnum.TEXT);
        textDatatype.setMaxLength(BigInteger.valueOf(249));
        poguesQuestion.getResponse().getFirst().setDatatype(textDatatype);
        poguesQuestion.getResponse().getFirst().setMandatory(Boolean.FALSE);
    }

    @Test
    void unitTest() {
        //
        enoQuestion = assertInstanceOf(TextQuestion.class,
                new PoguesConverter().convertToEno(poguesQuestion, Question.class));
        new PoguesMapper().mapInputObject(poguesQuestion, enoQuestion);

        assertNull(enoQuestion.getResponse().getVariableName());
        // see @AfterEach for other tests
    }

    @Test
    void unitTestFromQuestionnaire() {
        Questionnaire poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);
        poguesSequence.getChild().add(poguesQuestion);
        poguesQuestionnaire.getChild().add(poguesSequence);

        VariableType poguesVariable = new CollectedVariableType();
        poguesVariable.setId("variable-id");
        poguesVariable.setName("VARIABLE_NAME");
        poguesVariable.setDatatype(new TextDatatypeType());
        poguesQuestionnaire.setVariables(new Questionnaire.Variables());
        poguesQuestionnaire.getVariables().getVariable().add(poguesVariable);

        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapInputObject(poguesQuestionnaire, enoQuestionnaire);

        assertEquals(1, enoQuestionnaire.getSingleResponseQuestions().size());
        enoQuestion = assertInstanceOf(TextQuestion.class, enoQuestionnaire.getSingleResponseQuestions().getFirst());

        assertEquals("VARIABLE_NAME", enoQuestion.getResponse().getVariableName());
        // see @AfterEach for other tests
    }

    @AfterEach
    void commonTests() {
        assertEquals("question-id", enoQuestion.getId());
        assertEquals("QUESTION_NAME", enoQuestion.getName());
        assertEquals("variable-id", enoQuestion.getResponse().getVariableReference());
        assertEquals("\"Question label.\"", enoQuestion.getLabel().getValue());
        assertEquals(BigInteger.valueOf(249), enoQuestion.getMaxLength());
        assertEquals(TextQuestion.LengthType.SHORT, enoQuestion.getLengthType());
    }

}
