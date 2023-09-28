package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.model.question.table.BooleanCell;
import fr.insee.eno.core.model.question.table.NumericCell;
import fr.insee.eno.core.model.question.table.TextCell;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Table;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TableQuestionProcessingTest {

    private static CodeItem createCodeItem(String codeValue, String labelValue) {
        CodeItem codeItem = new CodeItem();
        codeItem.setLabel(new Label());
        codeItem.getLabel().setValue(labelValue);
        codeItem.setValue(codeValue);
        return codeItem;
    }

    @Test
    void processLunaticTableAgainstEnoTable() {
        // Given
        TableQuestion enoTable = new TableQuestion();
        //
        CodeList headerCodeList = new CodeList();
        CodeItem header1 = createCodeItem("1", "h1");
        CodeItem header2 = createCodeItem("2", "h2");
        CodeItem header3 = createCodeItem("3", "h3");
        headerCodeList.getCodeItems().add(header1);
        headerCodeList.getCodeItems().add(header2);
        headerCodeList.getCodeItems().add(header3);
        enoTable.setHeader(headerCodeList);
        //
        CodeList leftColumnCodeList = new CodeList();
        CodeItem line1 = createCodeItem("1", "l1");
        CodeItem line2 = createCodeItem("2", "l2");
        leftColumnCodeList.getCodeItems().add(line1);
        leftColumnCodeList.getCodeItems().add(line2);
        enoTable.setLeftColumn(leftColumnCodeList);
        //
        BooleanCell cell11 = new BooleanCell();
        cell11.setRowNumber(1);
        cell11.setColumnNumber(1);
        BooleanCell cell21 = new BooleanCell();
        cell21.setRowNumber(2);
        cell21.setColumnNumber(1);
        NumericCell cell12 = new NumericCell();
        cell12.setRowNumber(1);
        cell12.setColumnNumber(2);
        NumericCell cell22 = new NumericCell();
        cell22.setRowNumber(2);
        cell22.setColumnNumber(2);
        TextCell cell13 = new TextCell();
        cell13.setLengthType(TextQuestion.LengthType.SHORT);
        cell13.setRowNumber(1);
        cell13.setColumnNumber(3);
        TextCell cell23 = new TextCell();
        cell23.setLengthType(TextQuestion.LengthType.SHORT);
        cell23.setRowNumber(2);
        cell23.setColumnNumber(3);
        enoTable.getTableCells().add(cell11);
        enoTable.getTableCells().add(cell21);
        enoTable.getTableCells().add(cell12);
        enoTable.getTableCells().add(cell22);
        enoTable.getTableCells().add(cell13);
        enoTable.getTableCells().add(cell23);
        //
        enoTable.setVariableNames(List.of("BOOLEAN_1", "BOOLEAN_2", "NUMERIC_1", "NUMERIC_2", "TEXT_1", "TEXT_2"));
        //
        Table lunaticTable = new Table();

        // When
        TableQuestionProcessing.process(lunaticTable, enoTable);

        // Then
        assertEquals(4, lunaticTable.getHeader().size()); // top left cell + header
        assertEquals(2, lunaticTable.getBodyLines().size());
        lunaticTable.getBodyLines().forEach(bodyLine ->
                assertEquals(4, bodyLine.getBodyCells().size()));
        //
        lunaticTable.getHeader().forEach(headerType -> assertNull(headerType.getValue()));
        assertEquals("", lunaticTable.getHeader().get(0).getLabel().getValue());
        assertEquals("h1", lunaticTable.getHeader().get(1).getLabel().getValue());
        assertEquals("h2", lunaticTable.getHeader().get(2).getLabel().getValue());
        assertEquals("h3", lunaticTable.getHeader().get(3).getLabel().getValue());
        lunaticTable.getHeader().forEach(headerType ->
                assertEquals(Constant.LUNATIC_LABEL_VTL_MD, headerType.getLabel().getType()));
        //
        assertEquals("1", lunaticTable.getBodyLines().get(0).getBodyCells().get(0).getValue());
        assertEquals("2", lunaticTable.getBodyLines().get(1).getBodyCells().get(0).getValue());
        assertEquals("l1", lunaticTable.getBodyLines().get(0).getBodyCells().get(0).getLabel().getValue());
        assertEquals("l2", lunaticTable.getBodyLines().get(1).getBodyCells().get(0).getLabel().getValue());
        assertEquals(Constant.LUNATIC_LABEL_VTL_MD,
                lunaticTable.getBodyLines().get(0).getBodyCells().get(0).getLabel().getType());
        assertEquals(Constant.LUNATIC_LABEL_VTL_MD,
                lunaticTable.getBodyLines().get(1).getBodyCells().get(0).getLabel().getType());
        //
        assertEquals(ComponentTypeEnum.CHECKBOX_BOOLEAN, lunaticTable.getBodyLines().get(0).getBodyCells().get(1).getComponentType());
        assertEquals(ComponentTypeEnum.CHECKBOX_BOOLEAN, lunaticTable.getBodyLines().get(1).getBodyCells().get(1).getComponentType());
        assertEquals(ComponentTypeEnum.INPUT_NUMBER, lunaticTable.getBodyLines().get(0).getBodyCells().get(2).getComponentType());
        assertEquals(ComponentTypeEnum.INPUT_NUMBER, lunaticTable.getBodyLines().get(1).getBodyCells().get(2).getComponentType());
        assertEquals(ComponentTypeEnum.INPUT, lunaticTable.getBodyLines().get(0).getBodyCells().get(3).getComponentType());
        assertEquals(ComponentTypeEnum.INPUT, lunaticTable.getBodyLines().get(1).getBodyCells().get(3).getComponentType());

    }

}
