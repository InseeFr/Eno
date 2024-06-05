package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.EnoTable;
import fr.insee.eno.core.model.question.table.ResponseCell;
import fr.insee.eno.core.model.question.table.TableCell;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.List;

public class DDIInsertResponseInTableCells implements ProcessingStep<EnoQuestionnaire> {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        // Objects that implement the EnoTable interface
        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(EnoTable.class::isInstance)
                .map(EnoTable.class::cast)
                .forEach(this::insertResponsesInCells);
        // Complex multiple choice question objects (that correspond to tables in Lunatic)
        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(ComplexMultipleChoiceQuestion.class::isInstance)
                .map(ComplexMultipleChoiceQuestion.class::cast)
                .forEach(this::insertResponsesInCells);
    }

    private void insertResponsesInCells(EnoTable enoTable) {
        insertResponsesInCells(enoTable.getResponseCells(), enoTable.getVariableNames(), enoTable.getId());
    }

    private void insertResponsesInCells(ComplexMultipleChoiceQuestion enoComplexMCQ) {
        insertResponsesInCells(enoComplexMCQ.getResponseCells(), enoComplexMCQ.getVariableNames(), enoComplexMCQ.getId());
    }

    /**
     * Insert the response name property in table cells, using the ordered list of variable names.
     * @param responseCells List of Eno table cells objects.
     * @param variableNames List of variable names such as its n-th value is the response name of the n-th table cell.
     * @param questionId The identifier of the question object holding the table cells (for logging purposes).
     */
    private void insertResponsesInCells(List<ResponseCell> responseCells, List<String> variableNames, String questionId) {
        int cellsCount = responseCells.size();
        int variableNamesCount = variableNames.size();
        if (cellsCount != variableNamesCount)
            throw new MappingException(String.format(
                    "Table question '%s' mapped from DDI has %s cells and %s response names (the two must be equal).",
                    questionId, cellsCount, variableNamesCount));
        for (int k = 0; k< cellsCount; k++) {
            ResponseCell responseCell = responseCells.get(k);
            String variableName = variableNames.get(k);
            Response response = new Response();
            response.setVariableName(variableName);
            responseCell.setResponse(response);
        }
    }

}
