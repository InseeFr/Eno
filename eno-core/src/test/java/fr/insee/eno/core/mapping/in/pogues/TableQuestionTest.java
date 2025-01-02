package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.pogues.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class TableQuestionTest {

    @Test
    void poguesMapping() {
        QuestionType poguesTable = new QuestionType();
        poguesTable.setQuestionType(QuestionTypeEnum.TABLE);
        poguesTable.setName("TABLE_NAME");
        poguesTable.getLabel().add("Static table question.");
        ResponseStructureType responseStructure = new ResponseStructureType();
        poguesTable.setResponseStructure(responseStructure);

        Questionnaire poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);
        poguesSequence.getChild().add(poguesTable);
        poguesQuestionnaire.getChild().add(poguesSequence);

        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();

        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        TableQuestion enoTable = assertInstanceOf(TableQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().getFirst());
        assertEquals("TABLE_NAME", enoTable.getName());
        assertEquals("Static table question.", enoTable.getLabel().getValue());
    }

}
