package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Binding;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.model.question.table.NumericCell;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void integrationtest() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-dynamic-unit.xml")),
                enoQuestionnaire);
        EnoIndex enoIndex = enoQuestionnaire.getIndex();

        // When
        new DDIMoveUnitInQuestions(enoIndex).apply(enoQuestionnaire);

        // Then
        assertEquals("€", ((NumericQuestion) enoQuestionnaire.getSingleResponseQuestions().get(0)).getUnit().getValue());
        assertTrue(((NumericQuestion) enoQuestionnaire.getSingleResponseQuestions().get(2)).getUnit().getValue().contains("¤"));

        TableQuestion tableQuestion = (TableQuestion) enoQuestionnaire.getMultipleResponseQuestions().get(0);
        DynamicTableQuestion dynamicTableQuestion = (DynamicTableQuestion) enoQuestionnaire.getMultipleResponseQuestions().get(1);

        assertEquals("%", ((NumericCell) tableQuestion.getResponseCells().get(0)).getUnit().getValue());
        assertEquals("%", ((NumericCell) tableQuestion.getResponseCells().get(1)).getUnit().getValue());
        assertTrue(((NumericCell) tableQuestion.getResponseCells().get(2)).getUnit().getValue().contains("¤"));
        assertTrue(((NumericCell) tableQuestion.getResponseCells().get(3)).getUnit().getValue().contains("¤"));

        assertEquals("€", ((NumericCell) dynamicTableQuestion.getResponseCells().get(0)).getUnit().getValue());
        assertTrue(((NumericCell) dynamicTableQuestion.getResponseCells().get(1)).getUnit().getValue().contains("¤"));
    }

}
