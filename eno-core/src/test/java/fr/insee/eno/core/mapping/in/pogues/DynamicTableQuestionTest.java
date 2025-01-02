package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.pogues.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DynamicTableQuestionTest {

    @Test
    void poguesMapping() {
        QuestionType poguesTable = new QuestionType();
        poguesTable.setQuestionType(QuestionTypeEnum.TABLE);
        poguesTable.setName("DYNAMIC_TABLE_NAME");
        poguesTable.getLabel().add("Dynamic table question.");
        ResponseStructureType responseStructure = new ResponseStructureType();
        DimensionType dimension = new DimensionType();
        dimension.setDynamic("1-5");
        responseStructure.getDimension().add(dimension);
        poguesTable.setResponseStructure(responseStructure);

        Questionnaire poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);
        poguesSequence.getChild().add(poguesTable);
        poguesQuestionnaire.getChild().add(poguesSequence);

        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();

        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        DynamicTableQuestion enoTable = assertInstanceOf(DynamicTableQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().getFirst());
        assertEquals("DYNAMIC_TABLE_NAME", enoTable.getName());
        assertEquals("Dynamic table question.", enoTable.getLabel().getValue());
    }

}
