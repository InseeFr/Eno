package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoObjectWithExpression;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class DDIResolveVariableReferencesInExpressions implements ProcessingStep<EnoQuestionnaire> {

    /**
     * In DDI, VTL expressions contain variables references instead of their name.
     * This method replaces the references with the names in calculated variables, filters and controls.
     */
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        // Calculated variables
        enoQuestionnaire.getVariables().stream()
                .filter(variable -> Variable.CollectionType.CALCULATED.equals(variable.getCollectionType()))
                .map(CalculatedVariable.class::cast)
                .forEach(this::resolveExpression);
        // Controls
        enoQuestionnaire.getControls().forEach(this::resolveExpression);
        // Filters
        enoQuestionnaire.getFilters().forEach(this::resolveExpression);
        // Loops
        enoQuestionnaire.getLoops().stream()
                .filter(StandaloneLoop.class::isInstance)
                .map(StandaloneLoop.class::cast)
                .forEach(this::resolveExpression);
        // Dynamic tables with size expression
        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(DynamicTableQuestion.class::isInstance).map(DynamicTableQuestion.class::cast)
                .map(DynamicTableQuestion::getMaxSizeExpression)
                .filter(Objects::nonNull)
                .forEach(this::resolveExpression);
    }

    /**
     * Replace variable reference by variable name in given object's expression.
     */
    private void resolveExpression(EnoObjectWithExpression enoObject) {
        CalculatedExpression expression = enoObject.getExpression();
        resolveExpression(expression);
    }

    private void resolveExpression(StandaloneLoop standaloneLoop) {
        resolveExpression(standaloneLoop.getMinIteration());
        resolveExpression(standaloneLoop.getMaxIteration());
    }

    private void resolveExpression(CalculatedExpression expression) {
        String value = expression.getValue();
        List<BindingReference> orderedBindingReferences = orderById(expression.getBindingReferences());
        // Iterate on the reverse order, so that if some references overlap, the longer one is replaced first
        for (int i = orderedBindingReferences.size() - 1; i >= 0; i --) {
            BindingReference bindingReference = orderedBindingReferences.get(i);
            value = value.replace(bindingReference.getId(), bindingReference.getVariableName());
        }
        expression.setValue(value);
    }

    /**
     * Binding reference identifiers can overlap, so the replacement of references by the variable name must be done
     * carefully in a rather precise order. This method returns a list containing given binding references, ordered by
     * id.
     * @param bindingReferences A set of binding references.
     * @return A list of the binding references, ordered by id.
     */
    private static List<BindingReference> orderById(List<BindingReference> bindingReferences) {
        List<BindingReference> res = new ArrayList<>(bindingReferences);
        res.sort(Comparator.comparing(BindingReference::getId));
        return res;
    }

    // Note: orderByIdLength would have worked also

}
