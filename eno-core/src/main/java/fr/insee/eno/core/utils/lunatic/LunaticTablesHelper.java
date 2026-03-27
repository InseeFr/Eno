package fr.insee.eno.core.utils.lunatic;

import fr.insee.lunatic.model.flat.*;

import java.util.stream.Stream;

public class LunaticTablesHelper {

    private LunaticTablesHelper() {}

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
