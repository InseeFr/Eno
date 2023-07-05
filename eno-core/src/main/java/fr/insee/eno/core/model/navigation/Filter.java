package fr.insee.eno.core.model.navigation;

import datacollection33.IfThenElseType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoObjectWithExpression;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.lunatic.model.flat.ConditionFilterType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Filter extends EnoIdentifiableObject implements EnoObjectWithExpression {

    /** In DDI, filter have a "scope" that can contain other filters (nested filters).
     * Parent filter relationships are set during a DDI processing.
     * In Lunatic, each component has a unique filter. Its expression is a logical concatenation of the filter
     * with its parent expressions. */
    private Filter parentFilter; //TODO: nested filters logical concatenation for Lunatic

    /** Default expression is "true". */
    public Filter() {
        stringExpression = "true";
    }

    /** Lunatic filters doesn't have an identifier. */
    @DDI(contextType = IfThenElseType.class, field = "getIDArray(0).getStringValue()")
    private String id;

    //TODO: Asked Lunatic-Model to use LabelType in ConditionFilterType to refactor this.

    /** In DDI, the expression contains variable references instead of variables names.
     * This list contains the references of these variables. */
    @DDI(contextType = IfThenElseType.class, field = "getIfCondition().getCommandArray(0).getInParameterList()")
    private List<BindingReference> bindingReferences = new ArrayList<>();

    /** Command that determines if the filter is applied or not. */
    @DDI(contextType = IfThenElseType.class, field = "getIfCondition().getCommandArray(0).getCommandContent()")
    @Lunatic(contextType = ConditionFilterType.class, field = "setValue(#param)")
    private String stringExpression;

    private CalculatedExpression expression;

    public CalculatedExpression getExpression() {
        if (expression == null) {
            expression = new CalculatedExpression();
            expression.setValue(stringExpression);
            expression.setBindingReferences(bindingReferences);
        }
        return expression;
    }

    /** Same principle as sequence items list in sequence objects. */
    @DDI(contextType = IfThenElseType.class,
            field = "#index.get(#this.getThenConstructReference().getIDArray(0).getStringValue())" +
                    ".getControlConstructReferenceList()")
    private List<ItemReference> filterItems = new ArrayList<>();

}
