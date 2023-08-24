package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.*;

/** Processing on the Eno-model calculated expressions concerning calculated variables.
 * When a calculated expression has a reference to a calculated variable, we want to retrieve the variables references
 * on which this variable depends.
 * */
public class EnoResolveBindingReferences implements ProcessingStep<EnoQuestionnaire> {

    private final Map<String, Variable> variableMap = new HashMap<>();

    /** Resolves the binding references of all CalculatedVariable and ComponentFilter objects defined in
     * the questionnaire. */
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getVariables().forEach(variable -> variableMap.put(variable.getName(), variable));
        // First step: resolve references of calculated expression of calculated variables
        enoQuestionnaire.getVariables().stream()
                .filter(variable -> Variable.CollectionType.CALCULATED.equals(variable.getCollectionType()))
                .map(CalculatedVariable.class::cast)
                .forEach(this::resolveDependencies);
        // Second step: update references of other component that have a calculated expression
        // Component filters
        updateComponentFilterReferences(enoQuestionnaire.getSequences());
        updateComponentFilterReferences(enoQuestionnaire.getSubsequences());
        updateComponentFilterReferences(enoQuestionnaire.getSingleResponseQuestions());
        updateComponentFilterReferences(enoQuestionnaire.getMultipleResponseQuestions());
        // Loops
        enoQuestionnaire.getLoops().stream()
                .filter(StandaloneLoop.class::isInstance)
                .map(StandaloneLoop.class::cast)
                .forEach(this::updateStandaloneLoopReferences);
    }

    /**
     * Resolves the binding dependencies of the calculated variable given.
     * @param calculatedVariable A calculated variable.
     */
    private void resolveDependencies(CalculatedVariable calculatedVariable) {
        CalculatedExpression expression = calculatedVariable.getExpression();
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
     * Dives recursively in binding references to resolve the binding references of the expression given,
     * only adding collected or external variable references (not the intermediate calculated).
     * @param bindingReferences References to be added in the expression binding references.
     * @param expression Expression being updated.
     */
    private void insertReferences(Set<BindingReference> bindingReferences, CalculatedExpression expression) {
        for (BindingReference bindingReference : bindingReferences) {
            Variable variable = variableMap.get(bindingReference.getVariableName());
            if (Variable.CollectionType.CALCULATED.equals(variable.getCollectionType())) {
                insertReferences(((CalculatedVariable) variable).getExpression().getBindingReferences(), expression);
                return;
            }
            expression.getBindingReferences().add(bindingReference);
        }
    }

    /** Update the binding references of the filter in each Eno-component given. */
    private void updateComponentFilterReferences(List<? extends EnoComponent> enoComponents) {
        enoComponents.stream()
                .map(EnoComponent::getComponentFilter)
                .map(ComponentFilter::getBindingReferences)
                .forEach(this::updateBindingReferences);
    }

    /** Update the binding references of the "min" and "max" expressions of the given loop. */
    private void updateStandaloneLoopReferences(StandaloneLoop standaloneLoop) {
        updateBindingReferences(standaloneLoop.getMinIteration().getBindingReferences());
        updateBindingReferences(standaloneLoop.getMaxIteration().getBindingReferences());
    }

    /**
     * If the binding references contains calculated variables,
     * this method adds the references contained in these calculated variables.
     * @param bindingReferences Binding references to be updated.
     */
    private void updateBindingReferences(Set<BindingReference> bindingReferences) {
        // shallow copy
        List<BindingReference> initialBindingReferences = new ArrayList<>(bindingReferences);
        //
        for (BindingReference bindingReference : initialBindingReferences) {
            Variable variable = variableMap.get(bindingReference.getVariableName());
            if (Variable.CollectionType.CALCULATED.equals(variable.getCollectionType()))
                bindingReferences.addAll(
                        ((CalculatedVariable) variable).getExpression().getBindingReferences());
        }
    }

}
