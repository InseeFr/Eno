package fr.insee.eno.core.model.variable;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObjectWithExpression;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Context(format = Format.DDI, type = logicalproduct33.VariableType.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.VariableType.class)
public class CalculatedVariable extends Variable implements EnoObjectWithExpression {

    @Getter
    @Lunatic("setVariableType(T(fr.insee.eno.core.model.variable.Variable).lunaticCollectionType(#param))")
    private CollectionType collectionType = CollectionType.CALCULATED;

    /** DDI reference of the variable.
     * For a calculated variable, it is the 'SourceParameterReference' id,
     * that is in the 'Binding' of the 'ProcessingInstructionReference',
     * that is in the 'VariableRepresentation' of the variable object. */
    @Getter @Setter
    @DDI("getVariableRepresentation().getProcessingInstructionReference().getBindingArray(0)" +
            ".getSourceParameterReference().getIDArray(0).getStringValue()")
    private String reference;

    /** Expression to evaluate the variable if it is a calculated variable. */
    @Getter @Setter
    @DDI("#index.get(#this.getVariableRepresentation().getProcessingInstructionReference().getIDArray(0).getStringValue())" +
            ".getCommandCodeArray(0).getCommandArray(0)")
    @Lunatic("setExpression(#param)")
    private CalculatedExpression expression;

    @Lunatic("getBindingDependencies()")
    private List<String> lunaticBindingDependencies;

    public List<String> getLunaticBindingDependencies() {
        if (expression == null)
            return new ArrayList<>();
        return expression.getBindingReferences().stream()
                .map(BindingReference::getVariableName)
                .distinct()
                .toList();
    }

}
