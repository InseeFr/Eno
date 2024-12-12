package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Binding;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.model.question.table.NumericCell;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.reference.EnoIndex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DDIMoveUnitInQuestionsTest {

    @Test
    void numericQuestionNoUnit() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        CollectedVariable variable = new CollectedVariable();
        variable.setQuestionReference("numeric-question-id");
        enoQuestionnaire.getVariables().add(variable);
        EnoIndex enoIndex = new EnoIndex();
        NumericQuestion numericQuestion = new NumericQuestion();
        numericQuestion.setId("numeric-question-id");
        enoIndex.put(numericQuestion);
        //
        new DDIMoveUnitInQuestions(enoIndex).apply(enoQuestionnaire);
        //
        assertNull(numericQuestion.getUnit());
    }

    @Test
    void numericQuestionWithUnit() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        CollectedVariable variable = new CollectedVariable();
        variable.setUnit("%");
        variable.setQuestionReference("numeric-question-id");
        enoQuestionnaire.getVariables().add(variable);
        EnoIndex enoIndex = new EnoIndex();
        NumericQuestion numericQuestion = new NumericQuestion();
        numericQuestion.setId("numeric-question-id");
        enoIndex.put(numericQuestion);
        //
        new DDIMoveUnitInQuestions(enoIndex).apply(enoQuestionnaire);
        //
        assertEquals("%", numericQuestion.getUnit().getValue());
        assertEquals("%", numericQuestion.getUnit().getValue());
    }

    @Test
    void numericCell_table() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        CollectedVariable variable = new CollectedVariable();
        variable.setUnit("%");
        variable.setQuestionReference("table-question-id");
        variable.setReference("variable-ref");
        enoQuestionnaire.getVariables().add(variable);
        EnoIndex enoIndex = new EnoIndex();
        TableQuestion tableQuestion = new TableQuestion();
        tableQuestion.setId("table-question-id");
        Binding binding = new Binding();
        binding.setSourceParameterId("cell-ref");
        binding.setTargetParameterId("variable-ref");
        tableQuestion.getBindings().add(binding);
        NumericCell numericCell = new NumericCell();
        numericCell.setId("cell-ref");
        tableQuestion.getResponseCells().add(numericCell);
        enoIndex.put(tableQuestion);
        //
        new DDIMoveUnitInQuestions(enoIndex).apply(enoQuestionnaire);
        //
        assertEquals("%", numericCell.getUnit().getValue());
        assertEquals("%", numericCell.getUnit().getValue());
    }

    @Test
    void numericCell_dynamicTable() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        CollectedVariable variable = new CollectedVariable();
        variable.setUnit("%");
        variable.setQuestionReference("table-question-id");
        variable.setReference("variable-ref");
        enoQuestionnaire.getVariables().add(variable);
        EnoIndex enoIndex = new EnoIndex();
        DynamicTableQuestion dynamicTableQuestion = new DynamicTableQuestion();
        dynamicTableQuestion.setId("table-question-id");
        Binding binding = new Binding();
        binding.setSourceParameterId("cell-ref");
        binding.setTargetParameterId("variable-ref");
        dynamicTableQuestion.getBindings().add(binding);
        NumericCell numericCell = new NumericCell();
        numericCell.setId("cell-ref");
        dynamicTableQuestion.getResponseCells().add(numericCell);
        enoIndex.put(dynamicTableQuestion);
        //
        new DDIMoveUnitInQuestions(enoIndex).apply(enoQuestionnaire);
        //
        assertEquals("%", numericCell.getUnit().getValue());
        assertEquals("%", numericCell.getUnit().getValue());
    }

}
