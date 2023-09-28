package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.lunatic.model.flat.BodyLine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LeftColumnCellsProcessingTest {

    private static List<BodyLine> lunaticBody;
    @BeforeAll
    static void processComplexCodeList() {
        // Given
        TableQuestion enoTable = new TableQuestion();
        enoTable.setLeftColumn(ComputeCodeListSizesTest.createComplexNestedCodeList());
        // When
        lunaticBody = LeftColumnCellsProcessing.from(enoTable).getLunaticBody();
        // Then
        // -> tests
    }

    @Test
    void resultShouldHaveExpectedCells() {
        //
        assertEquals(1, lunaticBody.get(0).getBodyCells().size());
        assertEquals("1", lunaticBody.get(0).getBodyCells().get(0).getLabel().getValue());
        assertEquals(2, lunaticBody.get(1).getBodyCells().size());
        assertEquals("2", lunaticBody.get(1).getBodyCells().get(0).getLabel().getValue());
        assertEquals("21", lunaticBody.get(1).getBodyCells().get(1).getLabel().getValue());
        assertEquals(2, lunaticBody.get(2).getBodyCells().size());
        assertEquals("22", lunaticBody.get(2).getBodyCells().get(0).getLabel().getValue());
        assertEquals("221", lunaticBody.get(2).getBodyCells().get(1).getLabel().getValue());
        assertEquals(1, lunaticBody.get(3).getBodyCells().size());
        assertEquals("222", lunaticBody.get(3).getBodyCells().get(0).getLabel().getValue());
        assertEquals(2, lunaticBody.get(4).getBodyCells().size());
        assertEquals("223", lunaticBody.get(4).getBodyCells().get(0).getLabel().getValue());
        assertEquals("2231", lunaticBody.get(4).getBodyCells().get(1).getLabel().getValue());
        //...
        assertEquals(9, lunaticBody.size());
    }

    @Test
    void cellsShouldHaveExpectedRowspanAndColspan() {
        //
        assertNull(lunaticBody.get(0).getBodyCells().get(0).getRowspan());
        assertEquals(BigInteger.valueOf(4), lunaticBody.get(0).getBodyCells().get(0).getColspan());
        //
        assertEquals(BigInteger.valueOf(7), lunaticBody.get(1).getBodyCells().get(0).getRowspan());
        assertNull(lunaticBody.get(1).getBodyCells().get(0).getColspan());
        assertNull(lunaticBody.get(1).getBodyCells().get(1).getRowspan());
        assertEquals(BigInteger.valueOf(3), lunaticBody.get(1).getBodyCells().get(1).getColspan());
        //
        assertEquals(BigInteger.valueOf(4), lunaticBody.get(2).getBodyCells().get(0).getRowspan());
        assertNull(lunaticBody.get(2).getBodyCells().get(0).getColspan());
        assertNull(lunaticBody.get(2).getBodyCells().get(1).getRowspan());
        assertEquals(BigInteger.valueOf(2), lunaticBody.get(2).getBodyCells().get(1).getColspan());
        //...
        assertNull(lunaticBody.get(8).getBodyCells().get(0).getRowspan());
        assertEquals(BigInteger.valueOf(4), lunaticBody.get(8).getBodyCells().get(0).getColspan());
    }

}
