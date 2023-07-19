package fr.insee.eno.core.converter;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.exceptions.business.UnauthorizedHeaderException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.TableCell;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.model.question.table.*;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Class that holds the conversion logic between model tables and Lunatic tables. */
@Slf4j
public class LunaticTableConverter {

    public static final String LUNATIC_BOOLEAN_COMPONENT = "CheckboxBoolean";
    public static final String LUNATIC_SMALL_TEXT_COMPONENT = "Input";
    public static final String LUNATIC_LARGE_TEXT_COMPONENT = "Textarea";
    public static final String LUNATIC_NUMERIC_COMPONENT = "InputNumber";
    public static final String LUNATIC_DATE_COMPONENT = "Datepicker";
    public static final String LUNATIC_UCQ_CHECKBOX_COMPONENT = "CheckboxOne";
    public static final String LUNATIC_UCQ_RADIO_COMPONENT = "Radio";
    public static final String LUNATIC_UCQ_DROPDOWN_COMPONENT = "Dropdown";

    private LunaticTableConverter() {
        throw new IllegalArgumentException("Utility class");
    }

    public static Table convertEnoTable(TableQuestion enoTable) {
        //
        Table lunaticTable = new Table();

        // Compute sizes in header and left column
        // For the header, this is done to ensure that it is not a nested code list
        enoTable.getHeader().computeSizes();
        if (enoTable.getHeader().getMaxDepth() > 0)
            throw new UnauthorizedHeaderException(enoTable);
        enoTable.getLeftColumn().computeSizes();

        // Top left empty cell
        HeaderType topLeftCell = new HeaderType();
        LabelType topLeftLabel = new LabelType();
        topLeftLabel.setValue("");
        topLeftLabel.setType(Constant.LUNATIC_LABEL_VTL_MD);
        topLeftCell.setLabel(topLeftLabel);

        if (enoTable.getLeftColumn().getMaxDepth() > 0) {
            int leftColumnHSize = enoTable.getLeftColumn().getMaxDepth() + 1;
            topLeftCell.setColspan(BigInteger.valueOf(leftColumnHSize));
        }
        lunaticTable.getHeader().add(topLeftCell);

        // Header
        lunaticTable.getHeader().addAll(convertEnoHeaders(enoTable.getHeader()));

        // Left column
        List<BodyLine> bodyLines = flattenCodeList(enoTable.getLeftColumn());
        lunaticTable.getBodyLines().addAll(bodyLines);

        // Body
        // Make sure that the lines have enough capacity
        int headerSize = enoTable.getHeader().size();
        lunaticTable.getBodyLines().forEach(bodyLine ->
                bodyLine.getBodyCells().addAll(Collections.nCopies(headerSize, null))); // https://stackoverflow.com/a/27935203/13425151
        // In what follows, it is not assumed that table cells are ordered in a certain way in the eno model
        // Each cell is inserted in the right place using its row number & column number
        int firstContentLine = 0; // Fixed at 0 since nested code lists are not allowed in header
        int firstContentColumn = enoTable.getLeftColumn().getMaxDepth();
        for (int k=0; k<enoTable.getTableCells().size(); k++) {
            TableCell enoCell = enoTable.getTableCells().get(k);
            String variableName = enoTable.getVariableNames().get(k);
            BodyCell lunaticCell = convertEnoCell(enoCell, variableName);
            lunaticTable.getBodyLines().get(enoCell.getRowNumber() + firstContentLine - 1)
                    .getBodyCells().add(enoCell.getColumnNumber() + firstContentColumn, lunaticCell);
        }

        //
        return lunaticTable;
    }

    // We could do something neater here maybe
    public static List<BodyLine> flattenCodeList(CodeList codeList) {
        List<BodyLine> lunaticLines = new ArrayList<>();
        for (CodeItem codeItem : codeList.getCodeItems()) {
            lunaticLines.add(new BodyLine());
            flattenCodeItem(codeItem, lunaticLines);
            lunaticLines.remove(lunaticLines.size()-1);
        }
        return lunaticLines;
    }
    private static void flattenCodeItem(CodeItem codeItem, List<BodyLine> lunaticLines) {
        // Map code item on lunatic cell
        BodyCell lunaticCell = new BodyCell();
        new LunaticMapper().mapEnoObject(codeItem, lunaticCell);
        // Add lunatic cell in flat list
        lunaticLines.get(lunaticLines.size()-1).getBodyCells().add(lunaticCell);
        //
        if (codeItem.size() == 0) {
            lunaticLines.add(new BodyLine());
        }
        else {
            for (CodeItem codeItem1 : codeItem.getCodeItems()) {
                flattenCodeItem(codeItem1, lunaticLines);
            }
        }
    }

    /** Uses eno cell and variable name give + annotations on the TableCell classes
     * to return a fulfilled BodyCell object. */
    public static BodyCell convertEnoCell(TableCell enoCell, String variableName) {
        //
        BodyCell bodyCell = new BodyCell();
        //
        bodyCell.setResponse(new ResponseType());
        bodyCell.getResponse().setName(variableName);
        bodyCell.setId(enoCell.getId());

        if (enoCell instanceof BooleanCell) {
            bodyCell.setComponentType(LUNATIC_BOOLEAN_COMPONENT);
        }
        else if (enoCell instanceof TextCell textCell) {
            if (textCell.getMaxLength().intValue() < Constant.LUNATIC_SMALL_TEXT_LIMIT)
                bodyCell.setComponentType(LUNATIC_SMALL_TEXT_COMPONENT);
            else
                bodyCell.setComponentType(LUNATIC_LARGE_TEXT_COMPONENT);
        }
        else if (enoCell instanceof NumericCell) {
            bodyCell.setComponentType(LUNATIC_NUMERIC_COMPONENT);
        }
        else if (enoCell instanceof DateCell) {
            bodyCell.setComponentType(LUNATIC_DATE_COMPONENT);
        }
        else if (enoCell instanceof UniqueChoiceCell uniqueChoiceCell) {
            switch (uniqueChoiceCell.getDisplayFormat()) {
                case RADIO -> bodyCell.setComponentType(LUNATIC_UCQ_RADIO_COMPONENT);
                case CHECKBOX -> bodyCell.setComponentType(LUNATIC_UCQ_CHECKBOX_COMPONENT);
                case DROPDOWN -> bodyCell.setComponentType(LUNATIC_UCQ_DROPDOWN_COMPONENT);
            }
        }
        //
        new LunaticMapper().mapEnoObject(enoCell, bodyCell);
        //
        return bodyCell;
    }

    public static List<HeaderType> convertEnoHeaders(CodeList enoHeaders) {
        List<HeaderType> headers = new ArrayList<>();
        enoHeaders.getCodeItems().forEach(codeItem -> {
            HeaderType headerCell = new HeaderType();
            LabelType headerLabel = new LabelType();
            headerLabel.setValue(codeItem.getLabel().getValue());
            headerLabel.setType(Constant.LUNATIC_LABEL_VTL_MD);
            headerCell.setLabel(headerLabel);
            headers.add(headerCell);
        });
        return headers;
    }
}
