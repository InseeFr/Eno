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

    /** DDI reference of the variable.
     * For a calculated variable, it is the 'SourceParameterReference' id,
     * that is in the 'Binding' of the 'ProcessingInstructionReference',
     * that is in the 'VariableRepresentation' of the variable object. */
    @DDI(contextType = VariableType.class,
            field = "getVariableRepresentation().getProcessingInstructionReference().getBindingArray(0)" +
                    ".getSourceParameterReference().getIDArray(0).getStringValue()")
    private String reference;

    /** Expression to evaluate the variable if it is a calculated variable. */
    @DDI(contextType = VariableType.class,
            field = "#index.get(#this.getVariableRepresentation().getProcessingInstructionReference().getIDArray(0).getStringValue())" +
                    ".getCommandCodeArray(0).getCommandArray(0)")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.VariableType.class, field = "setExpression(#param)")
    private CalculatedExpression expression;


}
