package fr.insee.eno.core.model.variable;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoObjectWithExpression;
import fr.insee.lunatic.model.flat.IVariableType;
import logicalproduct33.VariableType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Variable extends EnoObject implements EnoObjectWithExpression {

    /* TODO: separate object for calculated variable? probably (implies a selection on variable list in DDI,
        but it is worth the effort since it would make more straightforward SpEL fields). */

    @DDI(contextType = VariableType.class, field = "getIDArray(0).getStringValue()")
    private String id;

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

    /** Variable name. */
    @DDI(contextType = VariableType.class,
            field = "getVariableNameArray(0).getStringArray(0).getStringValue()")
    @Lunatic(contextType = IVariableType.class, field = "setName(#param)")
    private String name;

    /** Reference to the question in which the variable is collected.
     * This property has been removed in Lunatic variables. */
    @DDI(contextType = VariableType.class,
            field = "!#this.getQuestionReferenceList().isEmpty() ? " +
                    "getQuestionReferenceArray(0).getIDArray(0).getStringValue() : null")
    //@Lunatic(contextType = IVariableType.class, field = "setComponentRef(#param)")
    String questionReference;

    /** Expression to evaluate the variable if it is a calculated variable. */
    @DDI(contextType = VariableType.class,
            field = "getVariableRepresentation().getProcessingInstructionReference() != null ? " +
                    "#index.get(#this.getVariableRepresentation().getProcessingInstructionReference().getIDArray(0).getStringValue())" +
                    ".getCommandCodeArray(0).getCommandArray(0) : null")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.VariableType.class, field = "setExpression(#param)")
    CalculatedExpression expression;

    /** Measurement unit (in case of some numeric variables). */
    @DDI(contextType = VariableType.class,
            field = "getVariableRepresentation().getValueRepresentation()?.getMeasurementUnit()?.getStringValue()")
    String unit;

    /** Variable type (among 'COLLECTED', 'CALCULATED' or 'EXTERNAL'). */
    @DDI(contextType = VariableType.class,
            field = "!#this.getQuestionReferenceList().isEmpty() ? 'COLLECTED' : 'CALCULATED'")
    @Lunatic(contextType = IVariableType.class,
            field = "setVariableType(T(fr.insee.lunatic.model.flat.VariableTypeEnum).valueOf(#param))")
    String collected; // TODO: an enum (COLLECTED, CALCULATED, EXTERNAL) here would be appropriate (or separate classes?)

}
