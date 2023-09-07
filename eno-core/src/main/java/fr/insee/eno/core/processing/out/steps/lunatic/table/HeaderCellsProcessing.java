package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.exceptions.business.UnauthorizedHeaderException;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.EnoTable;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.lunatic.model.flat.HeaderType;
import fr.insee.lunatic.model.flat.LabelType;
import lombok.Getter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
public class HeaderCellsProcessing {

    private HeaderCellsProcessing() {}

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
        topLeftLabel.setType(Constant.LUNATIC_LABEL_VTL_MD);
        topLeftCell.setLabel(topLeftLabel);
        topLeftCell.setColspan(BigInteger.valueOf(leftColumnColspan));
        lunaticHeader.add(topLeftCell);
    }

    private static void addOtherCells(CodeList headerCodeList, List<HeaderType> lunaticHeader) {
        headerCodeList.getCodeItems().forEach(codeItem -> {
            HeaderType headerCell = new HeaderType();
            LabelType headerLabel = new LabelType();
            headerLabel.setValue(codeItem.getLabel().getValue());
            headerLabel.setType(Constant.LUNATIC_LABEL_VTL_MD);
            headerCell.setLabel(headerLabel);
            lunaticHeader.add(headerCell);
        });
    }

}
