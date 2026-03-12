package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.EnoTable;
import fr.insee.eno.core.model.question.table.UniqueChoiceCell;
import fr.insee.pogues.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UniqueChoiceCellTest {

    @Test
    void ucqCellWithVariableOptions() {
        // Given
        // A questionnaire
        Questionnaire poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);

        // with a table question
        QuestionType poguesTableQuestion = new QuestionType();
        poguesTableQuestion.setQuestionType(QuestionTypeEnum.TABLE);
        // (static or dynamic doesn't matter here)
        poguesTableQuestion.setResponseStructure(new ResponseStructureType());
        poguesTableQuestion.getResponseStructure().getDimension().add(new DimensionType());
        poguesTableQuestion.getResponseStructure().getDimension().getFirst().setDimensionType(DimensionTypeEnum.PRIMARY);
        poguesTableQuestion.getResponseStructure().getDimension().getFirst().setDynamic("NON_DYNAMIC");

        // with a variable options UCQ response
        ResponseType poguesUCQResponse = new ResponseType();
        poguesUCQResponse.setCollectedVariableReference("ucq-variable-id");
        poguesUCQResponse.setDatatype(new TextDatatypeType());
        poguesUCQResponse.getDatatype().setTypeName(DatatypeTypeEnum.TEXT);
        poguesUCQResponse.getDatatype().setVisualizationHint(VisualizationHintEnum.RADIO);
        poguesUCQResponse.setChoiceType(ChoiceTypeEnum.VARIABLE);
        poguesUCQResponse.setVariableReference("variable-id");

        poguesTableQuestion.getResponse().add(poguesUCQResponse);
        poguesSequence.getChild().add(poguesTableQuestion);
        poguesQuestionnaire.getChild().add(poguesSequence);

        // and the variable that defines the options.
        poguesQuestionnaire.setVariables(new Questionnaire.Variables());
        CollectedVariableType poguesVariable = new CollectedVariableType();
        poguesVariable.setId("variable-id");
        poguesVariable.setName("SOME_ITERATION_VARIABLE");
        poguesQuestionnaire.getVariables().getVariable().add(poguesVariable);
        CollectedVariableType poguesUcqVariable = new CollectedVariableType();
        poguesUcqVariable.setId("ucq-variable-id");
        poguesUcqVariable.setName("UCQ_VARIABLE_OPTIONS");
        poguesQuestionnaire.getVariables().getVariable().add(poguesUcqVariable);

        // When
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        // Then
        EnoTable enoTable = (EnoTable) enoQuestionnaire.getMultipleResponseQuestions().getFirst();
        assertEquals(1, enoTable.getResponseCells().size());
        UniqueChoiceCell enoUCQCell = assertInstanceOf(UniqueChoiceCell.class, enoTable.getResponseCells().getFirst());
        assertEquals("SOME_ITERATION_VARIABLE", enoUCQCell.getOptionSource());
    }

}
