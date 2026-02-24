package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.converter.PoguesConverter;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.pogues.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UniqueChoiceQuestionTest {

    private QuestionType poguesUCQ;
    private UniqueChoiceQuestion enoUCQ;

    /** Creates a Pogues unique choice question object with 'basic' properties. */
    @BeforeEach
    void createPoguesUCQ() {
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
    void poguesMapping_unitTest() {
        enoUCQ = assertInstanceOf(UniqueChoiceQuestion.class,
                new PoguesConverter().convertToEno(poguesUCQ, Question.class));
        new PoguesMapper().mapInputObject(poguesUCQ, enoUCQ);

        assertEquals("UCQ_NAME", enoUCQ.getName());
        assertEquals("Unique choice question label.", enoUCQ.getLabel().getValue());
        assertEquals(UniqueChoiceQuestion.DisplayFormat.RADIO, enoUCQ.getDisplayFormat());
        assertEquals("code-list-id", enoUCQ.getCodeListReference());
        assertFalse(enoUCQ.isMandatory());
    }

    @Test
    void poguesMappingFromQuestionnaire() {
        // Given
        Questionnaire poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);
        poguesSequence.getChild().add(poguesUCQ);
        poguesQuestionnaire.getChild().add(poguesSequence);

        // When
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        // Then
        enoUCQ = assertInstanceOf(UniqueChoiceQuestion.class, enoQuestionnaire.getSingleResponseQuestions().getFirst());
        assertEquals("UCQ_NAME", enoUCQ.getName());
        assertEquals("Unique choice question label.", enoUCQ.getLabel().getValue());
        assertEquals(UniqueChoiceQuestion.DisplayFormat.RADIO, enoUCQ.getDisplayFormat());
        assertEquals("code-list-id", enoUCQ.getCodeListReference());
        assertFalse(enoUCQ.isMandatory());
    }

    /** Test for a UCQ with variable options.
     * Done at questionnaire level since there is a reference resolution. */
    @Test
    void variableOptionsCase() {
        // Given
        Questionnaire poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);

        // response variable
        poguesUCQ.getResponse().getFirst().setCollectedVariableReference("ucq-variable-id");

        // options source variable
        poguesUCQ.getResponse().getFirst().setChoiceType(ChoiceTypeEnum.VARIABLE);
        poguesUCQ.getResponse().getFirst().setVariableReference("variable-id");

        poguesSequence.getChild().add(poguesUCQ);
        poguesQuestionnaire.getChild().add(poguesSequence);

        poguesQuestionnaire.setVariables(new Questionnaire.Variables());
        CollectedVariableType poguesUcqVariable = new CollectedVariableType();
        poguesUcqVariable.setId("ucq-variable-id");
        poguesUcqVariable.setName("UCQ_VARIABLE");
        poguesQuestionnaire.getVariables().getVariable().add(poguesUcqVariable);
        CollectedVariableType poguesVariable = new CollectedVariableType();
        poguesVariable.setId("variable-id");
        poguesVariable.setName("SOME_ITERATION_VARIABLE");
        poguesQuestionnaire.getVariables().getVariable().add(poguesVariable);

        // When
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        // Then
        enoUCQ = assertInstanceOf(UniqueChoiceQuestion.class, enoQuestionnaire.getSingleResponseQuestions().getFirst());
        assertEquals("SOME_ITERATION_VARIABLE", enoUCQ.getOptionSource());
    }

}
