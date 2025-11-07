package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.EnoTable;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.model.question.table.CellLabel;
import fr.insee.eno.core.model.question.table.NoDataCell;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.Iterator;

/**
 * Moves
 */
public class DDIInsertNoDataCellLabels implements ProcessingStep<EnoQuestionnaire> {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(question -> question instanceof TableQuestion || question instanceof DynamicTableQuestion)
                .map(EnoTable.class::cast)
                .forEach(this::insertNoDataCellLabels);
    }

    private void insertNoDataCellLabels(EnoTable enoTable) {
        for (Iterator<CellLabel> iterator = enoTable.getCellLabels().iterator(); iterator.hasNext();) {
            CellLabel cellLabel = iterator.next();
            if (! CellLabel.DDI_FIXED_CELL.equals(cellLabel.getDdiCellType()))
                continue;
            NoDataCell noDataCell = findNoDataCell(cellLabel.getRowNumber(), cellLabel.getColumnNumber(), enoTable);
            noDataCell.setCellLabel(cellLabel);
            iterator.remove();
        }
    }

    private static NoDataCell findNoDataCell(int rowNumber, int columnNumber, EnoTable enoTable) {
        for (NoDataCell noDataCell : enoTable.getNoDataCells()) {
            if (noDataCell.getRowNumber() == rowNumber && noDataCell.getColumnNumber() == columnNumber)
                return noDataCell;
        }
        throw new MappingException("Unable to find non-collected cell for cell label defined at " +
                "row " + rowNumber + " and column " + columnNumber + ".");
    }

}
