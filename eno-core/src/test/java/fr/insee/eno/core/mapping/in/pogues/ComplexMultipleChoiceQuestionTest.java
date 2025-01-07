package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.pogues.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ComplexMultipleChoiceQuestionTest {

    @Test
    void poguesMapping() {
        // Given
        QuestionType poguesMCQ = new QuestionType();
        poguesMCQ.setQuestionType(QuestionTypeEnum.MULTIPLE_CHOICE);
        poguesMCQ.setName("MCQ_TABLE_NAME");
        poguesMCQ.getLabel().add("Complex multiple choice question.");
        poguesMCQ.setResponseStructure(new ResponseStructureType());
        poguesMCQ.getResponseStructure().getDimension().add(new DimensionType());
        poguesMCQ.getResponseStructure().getDimension().getFirst().setCodeListReference("code-list-id");
        poguesMCQ.getResponse().add(new ResponseType());
        poguesMCQ.getResponse().getFirst().setCollectedVariableReference("variable-1-id");
        TextDatatypeType datatype = new TextDatatypeType();
        datatype.setTypeName(DatatypeTypeEnum.TEXT);
        poguesMCQ.getResponse().getFirst().setDatatype(datatype);

        Questionnaire poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);
        poguesSequence.getChild().add(poguesMCQ);
        poguesQuestionnaire.getChild().add(poguesSequence);

        poguesQuestionnaire.setVariables(new Questionnaire.Variables());
        CollectedVariableType poguesVariable = new CollectedVariableType();
        poguesVariable.setId("variable-1-id");
        poguesVariable.setName("VARIABLE_1");
        poguesQuestionnaire.getVariables().getVariable().add(poguesVariable);

        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();

        // When
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        // Then
        ComplexMultipleChoiceQuestion enoMCQ = assertInstanceOf(ComplexMultipleChoiceQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().getFirst());
        assertEquals("MCQ_TABLE_NAME", enoMCQ.getName());
        assertEquals("Complex multiple choice question.", enoMCQ.getLabel().getValue());
        assertEquals("code-list-id", enoMCQ.getLeftColumnCodeListReference());
        assertEquals(1, enoMCQ.getVariableNames().size());
        assertEquals("VARIABLE_1", enoMCQ.getVariableNames().getFirst());
    }

}
