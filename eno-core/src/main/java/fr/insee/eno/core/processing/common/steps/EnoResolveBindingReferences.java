package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Processing on the Eno-model calculated expressions concerning calculated variables.
 * When a calculated expression has a reference to a calculated variable, we want to retrieve the variables references
 * on which this variable depends.
 * */
public class EnoResolveBindingReferences implements ProcessingStep<EnoQuestionnaire> {

    private final Map<String, Variable> variableMap = new HashMap<>();

    /**
     * Searches the variable in the variables map using the binding reference given.
     * Throws an exception if the variable is not found.
     * @param bindingReference binding reference.
     * @param calculatedVariable the variable whose dependencies are being resolved, passed for logging purposes.
     * @return The variable which has the name defined in the binding reference.
     */
    private Variable findVariable(BindingReference bindingReference, CalculatedVariable calculatedVariable) {
        Variable variable = variableMap.get(bindingReference.getVariableName());
        if (variable == null)
            throw new MappingException(String.format(
                    "Unable to retrieve the variable '%s' when resolving dependencies of calculated variable '%s'.",
                    bindingReference.getVariableName(), calculatedVariable.getName()));
        return variable;
    }

    /**
     * Searches the variable in the variables map using the binding reference given.
     * Throws an exception if the variable is not found.
     * @param bindingReference binding reference.
     * @return The variable which has the name defined in the binding reference.
     */
    private Variable findVariable(BindingReference bindingReference) {
        Variable variable = variableMap.get(bindingReference.getVariableName());
        if (variable == null)
            throw new MappingException(String.format(
                    "Unable to retrieve the variable '%s'.", bindingReference.getVariableName()));
        return variable;
    }

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
            Variable variable = findVariable(bindingReference, calculatedVariable);
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
    private void insertReferences(List<BindingReference> bindingReferences, CalculatedExpression expression) {
        for (BindingReference bindingReference : bindingReferences) {
            Variable variable = findVariable(bindingReference);
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
                .forEach(componentFilter -> {
                    List<BindingReference> references = resolveBindingReferences(componentFilter.getBindingReferences());
                    componentFilter.getResolvedBindingReferences().addAll(references);
                } );
    }

    /** Update the binding references of the "min" and "max" expressions of the given loop. */
    private void updateStandaloneLoopReferences(StandaloneLoop standaloneLoop) {
        List<BindingReference> references = null;
        if (standaloneLoop.getMinIteration() != null) {
            references = resolveBindingReferences(standaloneLoop.getMinIteration().getBindingReferences());
            standaloneLoop.getMinIteration().setBindingReferences(references);
        }

        if (standaloneLoop.getMaxIteration() != null) {
            references = resolveBindingReferences(standaloneLoop.getMaxIteration().getBindingReferences());
            standaloneLoop.getMaxIteration().setBindingReferences(references);
        }
    }

    /**
     * If the binding references contains calculated variables,
     * this method adds the references contained in these calculated variables.
     * @param bindingReferences Binding references to be updated.
     */
    private List<BindingReference> resolveBindingReferences(List<BindingReference> bindingReferences) {
        // shallow copy
        List<BindingReference> resolvedBindingReferences = new ArrayList<>(bindingReferences);
        //
        for (BindingReference bindingReference : bindingReferences) {
            Variable variable = findVariable(bindingReference);
            if (Variable.CollectionType.CALCULATED.equals(variable.getCollectionType()))
                resolvedBindingReferences.addAll(
                        ((CalculatedVariable) variable).getExpression().getBindingReferences());
        }
        return resolvedBindingReferences;
    }
}
