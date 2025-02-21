package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.pogues.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamicTableQuestionTest {

    private Questionnaire poguesQuestionnaire;
    private QuestionType poguesTable;
    private EnoQuestionnaire enoQuestionnaire;

    @BeforeEach
    void createPoguesQuestionnaireWithDynamicTable() {
        poguesTable = new QuestionType();
        poguesTable.setQuestionType(QuestionTypeEnum.TABLE);
        poguesTable.setName("DYNAMIC_TABLE_NAME");
        poguesTable.getLabel().add("Dynamic table question.");
        ResponseStructureType responseStructure = new ResponseStructureType();
        DimensionType dimension = new DimensionType();
        responseStructure.getDimension().add(dimension);
        poguesTable.setResponseStructure(responseStructure);

        poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);
        poguesSequence.getChild().add(poguesTable);
        poguesQuestionnaire.getChild().add(poguesSequence);
    }

    @Test
    void poguesMapping() {
        DimensionType dimension = poguesTable.getResponseStructure().getDimension().getFirst();
        dimension.setDynamic("DYNAMIC_LENGTH");

        enoQuestionnaire = new EnoQuestionnaire();

        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        assertNotNull(enoQuestionnaire);
    }

    @Test
    void poguesMapping_fixedLength() {
        DimensionType dimension = poguesTable.getResponseStructure().getDimension().getFirst();
        dimension.setDynamic("FIXED_LENGTH");

        enoQuestionnaire = new EnoQuestionnaire();

        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        assertNotNull(enoQuestionnaire);
    }

    @AfterEach
    void commonTests() {
        DynamicTableQuestion enoTable = assertInstanceOf(DynamicTableQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().getFirst());
        assertEquals("DYNAMIC_TABLE_NAME", enoTable.getName());
        assertEquals("Dynamic table question.", enoTable.getLabel().getValue());
    }

}
