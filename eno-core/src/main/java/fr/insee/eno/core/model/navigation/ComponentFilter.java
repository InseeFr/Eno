package fr.insee.eno.core.model.navigation;

import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoObjectWithExpression;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.lunatic.model.flat.ConditionFilterType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponentFilter extends EnoObject implements EnoObjectWithExpression {

    public static ComponentFilter defaultFilter() {
        ComponentFilter componentFilter = new ComponentFilter();
        componentFilter.setExpression(CalculatedExpression.defaultExpression());
        return componentFilter;
    }

    @Lunatic(contextType = ConditionFilterType.class, field = "setExpression(#param)")
    private CalculatedExpression expression = new CalculatedExpression();

    public void addFilter(Filter filter) {
        addFilterExpression(filter);
        addFilterBindingReferences(filter);
    }

    private void addFilterExpression(Filter filter) {
        String expressionToBeAdded = "(" + filter.getExpression().getValue() + ")";
        if (expression.getValue() == null)
            expression.setValue(expressionToBeAdded);
        else
            expression.setValue(expression.getValue() + " and " + expressionToBeAdded);
    }

    private void addFilterBindingReferences(Filter filter) {
        expression.getBindingReferences().addAll(filter.getExpression().getBindingReferences());
        /* TODO: duplicates might be a problem, so: replace "List" with "Collection" in mappers,
            use Set instead of List for this property here (and maybe in other places),
            and implement equals method in the BindingReference class
         */
    }

}
