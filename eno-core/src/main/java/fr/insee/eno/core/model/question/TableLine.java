package fr.insee.eno.core.model.question;

import datacollection33.GridResponseDomainInMixedType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.lunatic.model.flat.CellsLines;

import java.util.ArrayList;
import java.util.List;

public class TableLine extends EnoObject {

    @DDI(contextType = GridResponseDomainInMixedType.class,
            field = "")
    @Lunatic(contextType = CellsLines.class, field = "getCells()")
    private final List<TableCell> cells = new ArrayList<>();

}
