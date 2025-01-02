package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.converter.PoguesConverter;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.model.question.Question;
import fr.insee.pogues.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class NumericQuestionTest {

    private QuestionType poguesQuestion;
    private NumericQuestion enoQuestion;

    @BeforeEach
    void createPoguesNumericQuestion() {
        poguesQuestion = new QuestionType();
        poguesQuestion.setId("question-id");
        poguesQuestion.setQuestionType(QuestionTypeEnum.SIMPLE);
        poguesQuestion.setName("NUMERIC_QUESTION");
        poguesQuestion.getLabel().add("\"Numeric question label.\"");
        poguesQuestion.getResponse().add(new ResponseType());
        poguesQuestion.getResponse().getFirst().setCollectedVariableReference("variable-id");
        NumericDatatypeType numericDatatypeType = new NumericDatatypeType();
        numericDatatypeType.setTypeName(DatatypeTypeEnum.NUMERIC);
        numericDatatypeType.setMinimum(BigDecimal.ZERO);
        numericDatatypeType.setMaximum(BigDecimal.TEN);
        numericDatatypeType.setUnit("http://id.insee.fr/unit/euro"); // :(
        poguesQuestion.getResponse().getFirst().setDatatype(numericDatatypeType);
    }

    @Test
    void unitTest() {
        //
        enoQuestion = assertInstanceOf(NumericQuestion.class,
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
        poguesVariable.setName("NUMERIC_VARIABLE");
        poguesVariable.setDatatype(new TextDatatypeType());
        poguesQuestionnaire.setVariables(new Questionnaire.Variables());
        poguesQuestionnaire.getVariables().getVariable().add(poguesVariable);

        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapInputObject(poguesQuestionnaire, enoQuestionnaire);

        assertEquals(1, enoQuestionnaire.getSingleResponseQuestions().size());
        enoQuestion = assertInstanceOf(NumericQuestion.class, enoQuestionnaire.getSingleResponseQuestions().getFirst());

        assertEquals("NUMERIC_VARIABLE", enoQuestion.getResponse().getVariableName());
        // see @AfterEach for other tests
    }

    @AfterEach
    void commonTests() {
        assertEquals("question-id", enoQuestion.getId());
        assertEquals("NUMERIC_QUESTION", enoQuestion.getName());
        assertEquals("variable-id", enoQuestion.getResponse().getVariableReference());
        assertEquals("\"Numeric question label.\"", enoQuestion.getLabel().getValue());
        assertEquals(0d, enoQuestion.getMinValue());
        assertEquals(10d, enoQuestion.getMaxValue());
        assertEquals(BigInteger.ZERO, enoQuestion.getNumberOfDecimals());
        assertEquals("€", enoQuestion.getUnit().getValue());
        assertFalse(enoQuestion.getIsUnitDynamic());
    }

}
