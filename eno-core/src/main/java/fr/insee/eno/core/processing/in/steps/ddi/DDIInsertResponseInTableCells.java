package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.EnoTable;
import fr.insee.eno.core.model.question.table.TableCell;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.processing.ProcessingStep;

public class DDIInsertResponseInTableCells implements ProcessingStep<EnoQuestionnaire> {


    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(EnoTable.class::isInstance)
                .map(EnoTable.class::cast)
                .forEach(this::insertResponsesInCells);
    }

    private void insertResponsesInCells(EnoTable enoTable) {
        int cellsCount = enoTable.getTableCells().size();
        int variableNamesCount = enoTable.getVariableNames().size();
        if (cellsCount != variableNamesCount)
            throw new MappingException(String.format(
                    "Table question '%s' mapped from DDI has %s cells and %s response names (the two must be equal).",
                    enoTable.getId(), cellsCount, variableNamesCount));
        for (int k = 0; k< cellsCount; k++) {
            TableCell tableCell = enoTable.getTableCells().get(k);
            String variableName = enoTable.getVariableNames().get(k);
            Response response = new Response();
            response.setVariableName(variableName);
            tableCell.setResponse(response);
        }
    }

}
