package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.pogues.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SimpleMultipleChoiceQuestionTest {

    @Test
    void mapFromPoguesQuestionnaire() {
        Questionnaire poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);
        QuestionType poguesMCQ = new QuestionType();
        poguesMCQ.setQuestionType(QuestionTypeEnum.MULTIPLE_CHOICE);
        poguesMCQ.setName("MCQ_NAME");
        poguesMCQ.getLabel().add("Simple multiple choice question.");
        poguesMCQ.setResponseStructure(new ResponseStructureType());
        poguesMCQ.getResponseStructure().getDimension().add(new DimensionType());
        poguesMCQ.getResponseStructure().getDimension().getFirst().setCodeListReference("code-list-id");
        poguesSequence.getChild().add(poguesMCQ);
        poguesQuestionnaire.getChild().add(poguesSequence);

        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();

        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        SimpleMultipleChoiceQuestion enoMCQ = assertInstanceOf(SimpleMultipleChoiceQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().getFirst());
        assertEquals("MCQ_NAME", enoMCQ.getName());
        assertEquals("Simple multiple choice question.", enoMCQ.getLabel().getValue());
        assertEquals("code-list-id", enoMCQ.getCodeListReference());
    }

}
