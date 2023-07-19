package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoObjectWithExpression;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.EnoProcessingInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Processing on the Eno-model calculated expressions concerning calculated variables.
 * When a calculated expression has a reference to a calculated variable, we want to retrieve the variables references
 * on which this variable depends.
 * */
public class EnoResolveBindingReferences implements EnoProcessingInterface {

    private final Map<String, Variable> variableMap = new HashMap<>();

    /** Resolves the binding references of all CalculatedVariable and ComponentFilter objects defined in
     * the questionnaire. */
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getVariables().forEach(variable -> variableMap.put(variable.getName(), variable));
        // Calculated variables
        enoQuestionnaire.getVariables().stream()
                .filter(variable -> Variable.CollectionType.CALCULATED.equals(variable.getCollectionType()))
                .map(CalculatedVariable.class::cast)
                .map(CalculatedVariable::getExpression)
                .forEach(this::resolveDependencies);
        // Component filters
        // in sequences, subsequences and questions
        // TODO: probably loops too here
    }

    private void resolveDependencies(CalculatedExpression expression) {
        // shallow copy
        List<BindingReference> initialBindingReferences = new ArrayList<>(expression.getBindingReferences());
        //
        for (BindingReference bindingReference : initialBindingReferences) {
            Variable variable = variableMap.get(bindingReference.getVariableName());
            if (Variable.CollectionType.CALCULATED.equals(variable.getCollectionType())) {
                insertReferences(((CalculatedVariable) variable).getExpression().getBindingReferences(), expression);
            }
        }
    }

    /**
     *
     * @param bindingReferences References to be added in the expression binding references.
     * @param expression Expression being updated.
     */
    private void insertReferences(List<BindingReference> bindingReferences, CalculatedExpression expression) {
        for (BindingReference bindingReference : bindingReferences) {
            expression.getBindingReferences().add(bindingReference); // TODO: Set<> for binding references to not worry about duplicates!!!
            Variable variable = variableMap.get(bindingReference.getVariableName());
            if (Variable.CollectionType.CALCULATED.equals(variable.getCollectionType())) {
                insertReferences(((CalculatedVariable) variable).getExpression().getBindingReferences(), expression);
            }
        }
    }

}
