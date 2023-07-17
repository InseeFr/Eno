package fr.insee.eno.core.model.variable;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObjectWithExpression;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.lunatic.model.flat.IVariableType;
import logicalproduct33.VariableType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculatedVariable extends Variable implements EnoObjectWithExpression {

    @Lunatic(contextType = IVariableType.class,
            field = "setVariableType(T(fr.insee.eno.core.model.variable.Variable).lunaticCollectionType(#param))")
    private CollectionType collectionType = CollectionType.CALCULATED;

    /** In DDI, when a variable is used in a calculated expression, it is referred to through a reference
     * (not by its id). This reference is the 'SourceParameterReference' id in the variable definition.
     * Collected variables directly have a source parameter reference.
     * Calculated variables have it in the 'Binding' in their 'VariableRepresentation'. */
    @DDI(contextType = VariableType.class,
            field = "getSourceParameterReference() != null ? " +
                    "getSourceParameterReference().getIDArray(0).getStringValue() : " +
                    "getVariableRepresentation()?.getProcessingInstructionReference()?.getBindingArray(0)" +
                    "?.getSourceParameterReference()?.getIDArray(0)?.getStringValue()")
    private String reference; // TODO: is it possible to have none of both cases? (see pairwise DDI with variable 'l0v32sjd': mistake or actual case?)

    /** Expression to evaluate the variable if it is a calculated variable. */
    @DDI(contextType = VariableType.class,
            field = "getVariableRepresentation().getProcessingInstructionReference() != null ? " +
                    "#index.get(#this.getVariableRepresentation().getProcessingInstructionReference().getIDArray(0).getStringValue())" +
                    ".getCommandCodeArray(0).getCommandArray(0) : null")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.VariableType.class, field = "setExpression(#param)")
    private CalculatedExpression expression;


}
