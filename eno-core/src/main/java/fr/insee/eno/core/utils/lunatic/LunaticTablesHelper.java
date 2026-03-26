package fr.insee.eno.core.utils.lunatic;

import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.RosterForLoop;
import fr.insee.lunatic.model.flat.Table;

import java.util.stream.Stream;

public class LunaticTablesHelper {

    private LunaticTablesHelper() {}

    public static Stream<BodyCell> getAllCells(Table table) {
        return table.getBodyLines().stream()
                .flatMap(bodyLine -> bodyLine.getBodyCells().stream());
    }

    public static Stream<BodyCell> findCellsOfType(ComponentTypeEnum lunaticType, Table table) {
        return table.getBodyLines().stream()
                .flatMap(bodyLine -> bodyLine.getBodyCells().stream())
                .filter(bodyCell -> lunaticType.equals(bodyCell.getComponentType()));
    }

    public static Stream<BodyCell> findCellsOfType(ComponentTypeEnum lunaticType, RosterForLoop dynamicTable) {
        return dynamicTable.getComponents().stream()
                .filter(bodyCell -> lunaticType.equals(bodyCell.getComponentType()));
    }

}
