package fr.insee.eno.core.model.navigation;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ConditionFilterType;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Class that is very similar to the calculatedExpression class, designed to map the "conditionFilter"
 * property in Lunatic components.
 * The class contain methods to concatenate expressions that come from filters. */
@Context(format = Format.POGUES, type = String.class)
@Context(format = Format.LUNATIC, type = ConditionFilterType.class)
public class ComponentFilter extends EnoObject {

    public static final String DEFAULT_FILTER_VALUE = "true";

    /** Expression initialized with the default value. */
    @Getter @Setter
    @Pogues("T(fr.insee.eno.core.model.calculated.CalculatedExpression)" +
            ".removeSurroundingDollarSigns(#this)")
    @Lunatic("setValue(#param)")
    private String value = DEFAULT_FILTER_VALUE;

    /** Private attribute used to replace the default value without doing strings comparison. */
    private boolean isValueAtDefault = true;

    /** For now, Lunatic type in label objects does not come from metadata, but is hardcoded here in Eno.
     * See labels documentation. */
    @Getter @Setter
    @Lunatic("setType(T(fr.insee.lunatic.model.flat.LabelTypeEnum).fromValue(#param))")
    String type = LabelTypeEnum.VTL.value();

    /**
     * Reference of variables that are directly mentioned in the expression.
     * See the "resolved" binding references attribute.
     * In DDI, filters are mapped at questionnaire level in "Filter" objects of the Eno model,
     * binding references of this list come from there.
     * */
    @Getter
    @Pogues("T(fr.insee.eno.core.model.calculated.CalculatedExpression)" +
            ".extractBindingReferences(#this, #poguesIndex)")
    private final List<BindingReference> bindingReferences = new ArrayList<>();

    /**
     * Reference of all variables on which the expression is dependent.
     * A processing step uses the "raw" binding references to compute the "resolved" references.
     * @see fr.insee.eno.core.processing.common.steps.EnoResolveBindingReferences
     */
    @Getter
    private final List<BindingReference> resolvedBindingReferences = new ArrayList<>();

    /**
     * Contrary to CalculatedExpression class, Lunatic binding dependencies are mapped in the condition filter object.
     * NOTE: In Lunatic, "conditionFilter" don't have "bindingDependencies" anymore. Thus, this attribute should be
     * removed here in the Eno model. Yet, it is currently used in some processing steps, these have to be refactored.
     * Then we will be able to remove this attribute safely.
     * */
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
        if (filter.isRoundaboutFilter()) // dirty patch cf. comment on the corresponding property
            return;
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

    @Override
    public String toString() {
        return "ComponentFilter{" +
                "value='" + value + '\'' +
                ", isValueAtDefault=" + isValueAtDefault +
                ", type='" + type + '\'' +
                '}';
    }
}
