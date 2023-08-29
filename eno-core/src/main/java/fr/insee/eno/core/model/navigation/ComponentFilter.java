package fr.insee.eno.core.model.navigation;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ConditionFilterType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Class that is very similar to the calculatedExpression class, designed to map the "conditionFilter"
 * property in Lunatic components.
 * The class contain methods to concatenate expressions that come from filters. */
@Context(format = Format.LUNATIC, type = ConditionFilterType.class)
public class ComponentFilter extends EnoObject {

    public static final String DEFAULT_FILTER_VALUE = "true";

    /** Expression initialized with the default value. */
    @Getter @Setter
    @Lunatic("setValue(#param)")
    private String value = DEFAULT_FILTER_VALUE;

    /** Private attribute used to replace the default value without doing strings comparison. */
    private boolean isValueAtDefault = true;

    /** For now, Lunatic type in label objects does not come from metadata, but is hardcoded here in Eno.
     * See labels documentation. */
    @Getter @Setter
    @Lunatic("setType(#param)")
    String type = Constant.LUNATIC_LABEL_VTL;

    @Getter
    private final Set<BindingReference> bindingReferences = new HashSet<>();

    @Getter
    private final Set<BindingReference> resolvedBindingReferences = new HashSet<>();

    /** Contrary to CalculatedExpression class, Lunatic binding dependencies are mapped in the condition filter
     * object. */
    @Lunatic("getBindingDependencies()")
    private List<String> lunaticBindingDependencies;

    /** Custom getter that uses the binding references. */
    public List<String> getLunaticBindingDependencies() {
        return resolvedBindingReferences.stream().map(BindingReference::getVariableName).distinct().toList();
    }

    /**
     * Concatenate the expression defined in the given filter with the current expression of the instance,
     * and update binding references.
     * @param filter Questionnaire filter.
     */
    public void addFilter(Filter filter) {
        addFilterExpression(filter);
        addFilterBindingReferences(filter);
    }

    private void addFilterExpression(Filter filter) {
        String expressionToBeAdded = "(" + filter.getExpression().getValue() + ")";
        if (isValueAtDefault) {
            isValueAtDefault = false;
            this.setValue(expressionToBeAdded);
        }
        else
            this.setValue(this.getValue() + " and " + expressionToBeAdded);
    }

    private void addFilterBindingReferences(Filter filter) {
        this.getBindingReferences().addAll(filter.getExpression().getBindingReferences());
    }

}
