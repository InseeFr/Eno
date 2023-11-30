package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.exceptions.business.UnauthorizedHeaderException;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.EnoTable;
import fr.insee.lunatic.model.flat.HeaderType;
import fr.insee.lunatic.model.flat.LabelType;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import lombok.Getter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
public class HeaderCellsProcessing {

    private HeaderCellsProcessing() {}

    /**
     * Use the eno table given to create the header of the corresponding Lunatic table.
     * If the table has a left column, it implies adding a first empty cell, with a 'colspan' that fits
     * this left column.
     * @param enoTable Eno table object.
     * @param leftColumnColspan Lunatic colspan for the first cell. Use 0 if there is no such empty cell.
     * @return Lunatic header for the table.
     */
    public static List<HeaderType> from(EnoTable enoTable, int leftColumnColspan) {
        flatListCheck(enoTable);
        return createHeaderCells(enoTable.getHeader(), leftColumnColspan);
    }

    /**
     * Complex/nested code lists are not allowed in the header of Lunatic tables.
     * This method throws an exception if the header code list of the Eno table given is not flat.
     *  */
    private static void flatListCheck(EnoTable enoTable) {
        CodeList headerCodeList = enoTable.getHeader();
        ComputeCodeListSizes.of(headerCodeList);
        if (headerCodeList.getMaxDepth() > 0)
            throw new UnauthorizedHeaderException(enoTable);
    }

    private static List<HeaderType> createHeaderCells(CodeList headerCodeList, int leftColumnColspan) {
        List<HeaderType> lunaticHeader = new ArrayList<>();
        // Top left empty cell
        addTopLeftCell(leftColumnColspan, lunaticHeader);
        // Proper header cells
        addOtherCells(headerCodeList, lunaticHeader);
        //
        return lunaticHeader;
    }

    private static void addTopLeftCell(int leftColumnColspan, List<HeaderType> lunaticHeader) {
        if (leftColumnColspan == 0)
            return;
        HeaderType topLeftCell = new HeaderType();
        LabelType topLeftLabel = new LabelType();
        topLeftLabel.setValue("");
        topLeftLabel.setType(LabelTypeEnum.VTL_MD);
        topLeftCell.setLabel(topLeftLabel);
        if (leftColumnColspan > 1) // little detail that might change (no colspan if the value is 1)
            topLeftCell.setColspan(BigInteger.valueOf(leftColumnColspan));
        lunaticHeader.add(topLeftCell);
    }

    private static void addOtherCells(CodeList headerCodeList, List<HeaderType> lunaticHeader) {
        headerCodeList.getCodeItems().forEach(codeItem -> {
            HeaderType headerCell = new HeaderType();
            LabelType headerLabel = new LabelType();
            headerLabel.setValue(codeItem.getLabel().getValue());
            headerLabel.setType(LabelTypeEnum.VTL_MD);
            headerCell.setLabel(headerLabel);
            lunaticHeader.add(headerCell);
        });
    }

}
