package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.model.question.table.CellLabel;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test class to test the DDI mapping of non-collected cells in tables.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TableWithNoDataCellTest {

    private EnoQuestionnaire enoQuestionnaire;

    @BeforeAll
    void mapDDI() throws DDIParsingException {
        enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-no-data-cell.xml")),
                enoQuestionnaire);
    }

    @Test
    void tableBasedOnCodeList() {
        TableQuestion tableQuestion = assertInstanceOf(TableQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().getFirst());
        //
        assertEquals(4, tableQuestion.getResponseCells().size());
        //
        assertEquals(2, tableQuestion.getNoDataCells().size());
        assertEquals(2, tableQuestion.getNoDataCells().get(0).getRowNumber());
        assertEquals(2, tableQuestion.getNoDataCells().get(0).getColumnNumber());
        assertEquals(1, tableQuestion.getNoDataCells().get(1).getRowNumber());
        assertEquals(3, tableQuestion.getNoDataCells().get(1).getColumnNumber());
        //
        assertEquals(2, tableQuestion.getCellLabels().size());
        CellLabel cellLabelStatic = tableQuestion.getCellLabels().get(0);
        CellLabel cellLabelDynamic = tableQuestion.getCellLabels().get(1);
        //
        assertEquals("20", cellLabelStatic.getValue());
        assertEquals(2, cellLabelStatic.getRowNumber());
        assertEquals(2, cellLabelStatic.getColumnNumber());
        //
        assertNotNull(cellLabelDynamic.getValue());
        assertEquals(1, cellLabelDynamic.getRowNumber());
        assertEquals(3, cellLabelDynamic.getColumnNumber());
    }

    @Test
    void tableWithFixedSize() {
        DynamicTableQuestion tableQuestion = assertInstanceOf(DynamicTableQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().get(1));
        //
        assertEquals(2, tableQuestion.getResponseCells().size());
        //
        assertEquals(2, tableQuestion.getNoDataCells().size());
        assertEquals(1, tableQuestion.getNoDataCells().get(0).getRowNumber());
        assertEquals(3, tableQuestion.getNoDataCells().get(0).getColumnNumber());
        assertEquals(1, tableQuestion.getNoDataCells().get(1).getRowNumber());
        assertEquals(4, tableQuestion.getNoDataCells().get(1).getColumnNumber());
        //
        assertEquals(2, tableQuestion.getCellLabels().size());
        CellLabel cellLabelStatic = tableQuestion.getCellLabels().get(0);
        CellLabel cellLabelDynamic = tableQuestion.getCellLabels().get(1);
        //
        assertEquals("\"Foo\"", cellLabelStatic.getValue());
        assertEquals(1, cellLabelStatic.getRowNumber());
        assertEquals(3, cellLabelStatic.getColumnNumber());
        //
        assertNotNull(cellLabelDynamic.getValue());
        assertEquals(1, cellLabelDynamic.getRowNumber());
        assertEquals(4, cellLabelDynamic.getColumnNumber());
    }

    @Test
    void tableWithDynamicSize() {
        DynamicTableQuestion tableQuestion = assertInstanceOf(DynamicTableQuestion.class,
                enoQuestionnaire.getMultipleResponseQuestions().get(2));
        //
        assertEquals(2, tableQuestion.getResponseCells().size());
        //
        assertEquals(2, tableQuestion.getNoDataCells().size());
        assertEquals(1, tableQuestion.getNoDataCells().get(0).getRowNumber());
        assertEquals(3, tableQuestion.getNoDataCells().get(0).getColumnNumber());
        assertEquals(1, tableQuestion.getNoDataCells().get(1).getRowNumber());
        assertEquals(4, tableQuestion.getNoDataCells().get(1).getColumnNumber());
        //
        assertEquals(2, tableQuestion.getCellLabels().size());
        CellLabel cellLabelStatic = tableQuestion.getCellLabels().get(0);
        CellLabel cellLabelDynamic = tableQuestion.getCellLabels().get(1);
        //
        assertEquals("\"Bar\"", cellLabelStatic.getValue());
        assertEquals(1, cellLabelStatic.getRowNumber());
        assertEquals(3, cellLabelStatic.getColumnNumber());
        //
        assertNotNull(cellLabelDynamic.getValue());
        assertEquals(1, cellLabelDynamic.getRowNumber());
        assertEquals(4, cellLabelDynamic.getColumnNumber());
    }

}
