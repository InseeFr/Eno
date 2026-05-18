package fr.insee.eno.core.utils.lunatic;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static fr.insee.lunatic.model.flat.ComponentTypeEnum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticTablesHelperTest {

    @Test
    void findTableCellsTest() {
        Table table = new Table();
        table.getBodyLines().add(new BodyLine());
        table.getBodyLines().getLast().getBodyCells().addAll(bodyCellsOfType(INPUT, INPUT_NUMBER));
        table.getBodyLines().add(new BodyLine());
        table.getBodyLines().getLast().getBodyCells().addAll(bodyCellsOfType(DROPDOWN, INPUT));
        assertEquals(2, LunaticTablesHelper.findCellsOfType(INPUT, table).count());
    }

    @Test
    void findDynamicTableCellsTest() {
        RosterForLoop dynamicTable = new RosterForLoop();
        dynamicTable.getComponents().addAll(bodyCellsOfType(SUGGESTER, INPUT_NUMBER, SUGGESTER));
        assertEquals(2, LunaticTablesHelper.findCellsOfType(SUGGESTER, dynamicTable).count());
    }

    private static List<BodyCell> bodyCellsOfType(ComponentTypeEnum... types) {
        return Arrays.stream(types).map(type -> {
            BodyCell bodyCell = new BodyCell();
            bodyCell.setComponentType(type);
            return bodyCell;
        }).toList();
    }

    @Test
    void findAllCellsTest() {
        Table table = new Table();
        table.getBodyLines().add(new BodyLine());
        table.getBodyLines().getLast().getBodyCells().addAll(bodyCellsOfType(INPUT, INPUT_NUMBER));
        table.getBodyLines().add(new BodyLine());
        table.getBodyLines().getLast().getBodyCells().addAll(bodyCellsOfType(DROPDOWN, INPUT));
        assertEquals(4, LunaticTablesHelper.getAllCells(table).count());
    }

}
