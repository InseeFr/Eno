package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.Table;

import java.util.ArrayList;
import java.util.List;

public class TableQuestion extends MultipleResponseQuestion {

    /*
    @DDI(contextType = QuestionGridType.class,
            field = "getStructuredMixedGridResponseDomain().getGridResponseDomainInMixedList()")
    @Lunatic(contextType = Table.class, field = "getCells()")
    private final List<TableLine> lines = new ArrayList<>();*
    */

    @Lunatic(contextType = Table.class, field = "setPositioning(#param)")
    private final String positioning = "HORIZONTAL";

}
