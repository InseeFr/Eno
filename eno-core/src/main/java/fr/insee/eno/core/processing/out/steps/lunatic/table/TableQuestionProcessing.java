package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.model.question.table.NoDataCell;
import fr.insee.eno.core.model.question.table.ResponseCell;
import fr.insee.eno.core.model.question.table.TableCell;
import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.BodyLine;
import fr.insee.lunatic.model.flat.HeaderType;
import fr.insee.lunatic.model.flat.Table;

import java.util.List;

/** Class that holds the conversion logic between model tables and Lunatic tables. */
public class TableQuestionProcessing {

    private TableQuestionProcessing() {
        throw new IllegalArgumentException("Utility class");
    }

    /**
     * Processes header and body lines of lunatic table, using table cell objects from the Eno table.
     * @param lunaticTable Lunatic table (its header and body lines to be filled)
     * @param enoTable Eno table
     */
    public static void process(Table lunaticTable, TableQuestion enoTable) {

        // Left column
        LeftColumnCellsProcessing leftColumnCellsProcessing = LeftColumnCellsProcessing.from(enoTable);
        List<BodyLine> lunaticBody = leftColumnCellsProcessing.getLunaticBody();
        lunaticTable.getBodyLines().addAll(lunaticBody);

        // Header
        int leftColumnColspan = leftColumnCellsProcessing.getLeftColumnColspan();
        List<HeaderType> lunaticHeader = HeaderCellsProcessing.from(enoTable, leftColumnColspan);
        lunaticTable.getHeader().addAll(lunaticHeader);

        // Body cells

        int columnNumber = lunaticHeader.size() - 1; // (not counting the top left cell)
        int rowNumber = lunaticBody.size();

        TableCell[][] enoCellMatrix = createCellsMatrix(rowNumber, columnNumber);
        putResponseCells(enoTable.getResponseCells(), enoCellMatrix);
        putNoDataCells(enoTable.getNoDataCells(), enoCellMatrix);
        for (int i=0; i<enoCellMatrix.length; i++) {
            for (int j=0; j<enoCellMatrix[i].length; j++) {
                TableCell enoCell = enoCellMatrix[i][j];
                BodyCell lunaticCell = convertResponseCell(enoCell);
                lunaticTable.getBodyLines().get(i).getBodyCells().add(lunaticCell);
            }
        }

    }

    /** Uses eno cell and variable name give + annotations on the TableCell classes
     * to return a fulfilled BodyCell object. */
    public static BodyCell convertResponseCell(TableCell enoCell) {
        BodyCell bodyCell = new BodyCell();
        new LunaticMapper().mapEnoObject((EnoObject) enoCell, bodyCell);
        return bodyCell;
    }

    private static void putResponseCells(List<ResponseCell> flatCellsList, TableCell[][] matrix) {
        flatCellsList.forEach(tableCell -> putCellInMatrix(matrix, tableCell));
    }

    private static void putNoDataCells(List<NoDataCell> flatCellsList, TableCell[][] matrix) {
        flatCellsList.forEach(tableCell -> putCellInMatrix(matrix, tableCell));
    }

    private static void putCellInMatrix(TableCell[][] matrix, TableCell tableCell) {
        int rowIndex = tableCell.getRowNumber() - 1;
        int columnIndex = tableCell.getColumnNumber() - 1;
        matrix[rowIndex][columnIndex] = tableCell;
    }

    private static TableCell[][] createCellsMatrix(int rowNumber, int columnNumber) {
        return new TableCell[rowNumber][columnNumber];
    }

}
