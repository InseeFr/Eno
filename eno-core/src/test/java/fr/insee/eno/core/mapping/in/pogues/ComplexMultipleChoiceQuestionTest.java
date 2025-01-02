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
        poguesMCQ.getResponse().add(new ResponseType());
        TextDatatypeType datatype = new TextDatatypeType();
        datatype.setTypeName(DatatypeTypeEnum.TEXT);
        poguesMCQ.getResponse().getFirst().setDatatype(datatype);

        Questionnaire poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);
        poguesSequence.getChild().add(poguesMCQ);
        poguesQuestionnaire.getChild().add(poguesSequence);

        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();

        // When
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        // Then
        ComplexMultipleChoiceQuestion enoMCQ = assertInstanceOf(ComplexMultipleChoiceQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().getFirst());
        assertEquals("MCQ_TABLE_NAME", enoMCQ.getName());
        assertEquals("Complex multiple choice question.", enoMCQ.getLabel().getValue());
    }

}
