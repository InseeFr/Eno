package fr.insee.eno.core.model.navigation;

import datacollection33.LoopType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

/**
 * Standalone loop, in opposition to "linked" loop.
 * Loop defined with a minimum value and a maximum value (that are calculated expressions).
 * */
@Getter
@Setter
@Context(format = Format.DDI, type = LoopType.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.Loop.class)
public class StandaloneLoop extends Loop {

    /** A standalone loop has a button to add occurrences, which has a label. */
    @DDI("getLabelArray(0)")
    @Lunatic("setLabel(#param)")
    Label addButtonLabel;

    /** Minimum number of iterations allowed.
     * In Pogues, this field is excluded if the "Based on" field is specified.
     * The value is a VTL expression. */
    @DDI("getInitialValue().getCommandArray(0)")
    private CalculatedExpression minIteration;

    /** Maximum number of iterations allowed.
     * See 'minIteration' for details. */
    @DDI("getLoopWhile().getCommandArray(0)")
    private CalculatedExpression maxIteration;

}
