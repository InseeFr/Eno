package fr.insee.eno.core.model.question;

import datacollection33.GridResponseDomainInMixedType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;

import java.util.ArrayList;
import java.util.List;

public class TableLine extends EnoObject {

    @DDI(contextType = GridResponseDomainInMixedType.class,
            field = "")
    //@Lunatic(contextType = CellsLines.class, field = "getCells()") TODO: changed in Lunatic-Model v2.2.13
    private final List<TableCell> cells = new ArrayList<>();

}
