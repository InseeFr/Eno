package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.UnauthorizedHeaderException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.TableCell;
import fr.insee.eno.core.model.question.TableQuestion;
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

    // TODO: type attribute in Lunatic labels
    // (probably not here but in a designed Lunatic processing class)

    public static Table convertEnoTable(TableQuestion enoTable) {
        //
        Table lunaticTable = new Table();

        // Compute sizes in header and left column
        // For the header, this is done to ensure that it is not a nested code list
        enoTable.getHeader().computeSizes();
        if (enoTable.getHeader().getMaxLevel() > 0)
            throw new UnauthorizedHeaderException(enoTable);
        enoTable.getLeftColumn().computeSizes();

        // Top left empty cell
        HeaderType topLeftCell = new HeaderType();
        topLeftCell.setLabel(new LabelType());
        topLeftCell.getLabel().setValue("");
        if (enoTable.getLeftColumn().getMaxLevel() > 0)
            topLeftCell.setColspan(BigInteger.valueOf(enoTable.getLeftColumn().getMaxLevel() + 1));
        lunaticTable.getHeader().add(topLeftCell);

        // Header
        enoTable.getHeader().getCodeItems().forEach(codeItem -> {
            HeaderType headerCell = new HeaderType();
            LunaticMapper lunaticMapper = new LunaticMapper();
            lunaticMapper.mapEnoObject(codeItem, headerCell);
            lunaticTable.getHeader().add(headerCell);
        });

        // Left column
        // Note: Lunatic class names are a bit confusing: BodyType = line, BodyLine = cell
        List<BodyType> bodyTypes = flattenCodeList(enoTable.getLeftColumn());
        lunaticTable.getBody().addAll(bodyTypes);

        // Body
        // Make sure that the lines have enough capacity
        int headerSize = enoTable.getHeader().size();
        lunaticTable.getBody().forEach(bodyType ->
                bodyType.getBodyLine().addAll(Collections.nCopies(headerSize, null))); // https://stackoverflow.com/a/27935203/13425151
        // In what follows, it is not assumed that table cells are ordered in a certain way in the eno model
        // Each cell is inserted in the right place using its row number & column number
        int firstContentLine = 0; // Fixed at 0 since nested code lists are not allowed in header
        int firstContentColumn = enoTable.getLeftColumn().getMaxLevel() + 1;
        for (int k=0; k<enoTable.getTableCells().size(); k++) {
            TableCell enoCell = enoTable.getTableCells().get(k);
            String variableName = enoTable.getVariableNames().get(k);
            BodyLine lunaticCell = convertEnoCell(enoCell, variableName);
            lunaticTable.getBody().get(enoCell.getRowNumber() + firstContentLine - 1)
                    .getBodyLine().add(enoCell.getColumnNumber() + firstContentColumn, lunaticCell);
        }

        //
        return lunaticTable;
    }

    // We could do something neater here maybe
    public static List<BodyType> flattenCodeList(CodeList codeList) {
        List<BodyType> lunaticLines = new ArrayList<>();
        for (CodeList.CodeItem codeItem : codeList.getCodeItems()) {
            lunaticLines.add(new BodyType());
            flattenCodeItem(codeItem, lunaticLines);
            lunaticLines.remove(lunaticLines.size()-1);
        }
        return lunaticLines;
    }
    private static void flattenCodeItem(CodeList.CodeItem codeItem, List<BodyType> lunaticLines) {
        // Map code item on lunatic cell
        BodyLine lunaticCell = new BodyLine();
        new LunaticMapper().mapEnoObject(codeItem, lunaticCell);
        // Add lunatic cell in flat list
        lunaticLines.get(lunaticLines.size()-1).getBodyLine().add(lunaticCell);
        //
        if (codeItem.size() == 0) {
            lunaticLines.add(new BodyType());
        }
        else {
            for (CodeList.CodeItem codeItem1 : codeItem.getCodeItems()) {
                flattenCodeItem(codeItem1, lunaticLines);
            }
        }
    }

    /** Uses eno cell and variable name give + annotations on the TableCell classes
     * to return a fulfilled BodyLine object. */
    private static BodyLine convertEnoCell(TableCell enoCell, String variableName) {
        //
        BodyLine bodyLine = new BodyLine();
        //
        bodyLine.setResponse(new ResponseType());
        bodyLine.getResponse().setName(variableName);
        if (enoCell instanceof TableCell.BooleanCell) {
            bodyLine.setComponentType(LUNATIC_BOOLEAN_COMPONENT);
        }
        else if (enoCell instanceof TableCell.TextCell textCell) {
            if (textCell.getMaxLength().intValue() < LunaticConverter.SMALL_TEXT_LIMIT)
                bodyLine.setComponentType(LUNATIC_SMALL_TEXT_COMPONENT);
            else
                bodyLine.setComponentType(LUNATIC_LARGE_TEXT_COMPONENT);
        }
        else if (enoCell instanceof TableCell.NumericCell numericCell) {
            bodyLine.setComponentType(LUNATIC_NUMERIC_COMPONENT);
        }
        else if (enoCell instanceof TableCell.DateCell dateCell) {
            bodyLine.setComponentType(LUNATIC_DATE_COMPONENT);
        }
        else if (enoCell instanceof TableCell.UniqueChoiceCell uniqueChoiceCell) {
            switch (uniqueChoiceCell.getDisplayFormat()) {
                case RADIO -> bodyLine.setComponentType(LUNATIC_UCQ_RADIO_COMPONENT);
                case CHECKBOX -> bodyLine.setComponentType(LUNATIC_UCQ_CHECKBOX_COMPONENT);
                case DROPDOWN -> bodyLine.setComponentType(LUNATIC_UCQ_DROPDOWN_COMPONENT);
            }
        }
        //
        new LunaticMapper().mapEnoObject(enoCell, bodyLine);
        //
        return bodyLine;
    }

}
