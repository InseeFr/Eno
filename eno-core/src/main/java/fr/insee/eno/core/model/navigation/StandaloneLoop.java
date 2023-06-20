package fr.insee.eno.core.model.navigation;

import datacollection33.LoopType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import lombok.Getter;
import lombok.Setter;

/**
 * Standalone loop, in opposition to "linked" loop.
 * Loop defined with a minimum value and a maximum value (that are calculated expressions).
 * */
@Getter
@Setter
public class StandaloneLoop extends Loop {

    /** Minimum number of iterations allowed.
     * In Pogues, this field is excluded if the "Based on" field is specified.
     * The value is a VTL expression. */
    @DDI(contextType = LoopType.class,
            field = "getInitialValue().getCommandArray(0)")
    private CalculatedExpression minIteration;

    /** Maximum number of iterations allowed.
     * See 'minIteration' for details. */
    @DDI(contextType = LoopType.class,
            field = "getLoopWhile().getCommandArray(0)")
    private CalculatedExpression maxIteration;

}
