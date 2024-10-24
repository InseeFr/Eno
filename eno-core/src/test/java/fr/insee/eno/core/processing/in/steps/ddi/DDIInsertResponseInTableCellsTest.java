package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.model.question.table.BooleanCell;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DDIInsertResponseInTableCellsTest {

    @Test
    void staticTableQuestionCase() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        TableQuestion tableQuestion = new TableQuestion();
        tableQuestion.setResponseCells(List.of(new BooleanCell(), new BooleanCell()));
        tableQuestion.setVariableNames(List.of("FOO", "BAR"));
        enoQuestionnaire.getMultipleResponseQuestions().add(tableQuestion);
        // When
        DDIInsertResponseInTableCells processing = new DDIInsertResponseInTableCells();
        processing.apply(enoQuestionnaire);
        // Then
        tableQuestion.getResponseCells().forEach(tableCell -> assertNotNull(tableCell.getResponse()));
        assertEquals("FOO", tableQuestion.getResponseCells().get(0).getResponse().getVariableName());
        assertEquals("BAR", tableQuestion.getResponseCells().get(1).getResponse().getVariableName());
    }

    @Test
    void dynamicTableQuestionCase() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DynamicTableQuestion dynamicTableQuestion = new DynamicTableQuestion();
        dynamicTableQuestion.setResponseCells(List.of(new BooleanCell(), new BooleanCell()));
        dynamicTableQuestion.setVariableNames(List.of("FOO", "BAR"));
        enoQuestionnaire.getMultipleResponseQuestions().add(dynamicTableQuestion);
        // When
        DDIInsertResponseInTableCells processing = new DDIInsertResponseInTableCells();
        processing.apply(enoQuestionnaire);
        // Then
        dynamicTableQuestion.getResponseCells().forEach(tableCell -> assertNotNull(tableCell.getResponse()));
        assertEquals("FOO", dynamicTableQuestion.getResponseCells().get(0).getResponse().getVariableName());
        assertEquals("BAR", dynamicTableQuestion.getResponseCells().get(1).getResponse().getVariableName());
    }

    @Test
    void complexMultipleChoiceQuestionCase() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        ComplexMultipleChoiceQuestion complexMultipleChoiceQuestion = new ComplexMultipleChoiceQuestion();
        complexMultipleChoiceQuestion.setResponseCells(List.of(new BooleanCell(), new BooleanCell()));
        complexMultipleChoiceQuestion.setVariableNames(List.of("FOO", "BAR"));
        enoQuestionnaire.getMultipleResponseQuestions().add(complexMultipleChoiceQuestion);
        // When
        DDIInsertResponseInTableCells processing = new DDIInsertResponseInTableCells();
        processing.apply(enoQuestionnaire);
        // Then
        complexMultipleChoiceQuestion.getResponseCells().forEach(tableCell -> assertNotNull(tableCell.getResponse()));
        assertEquals("FOO", complexMultipleChoiceQuestion.getResponseCells().get(0).getResponse().getVariableName());
        assertEquals("BAR", complexMultipleChoiceQuestion.getResponseCells().get(1).getResponse().getVariableName());
    }

}
