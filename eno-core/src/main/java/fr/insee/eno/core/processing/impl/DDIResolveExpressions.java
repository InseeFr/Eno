package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.*;
import fr.insee.eno.core.processing.InProcessingInterface;

public class DDIResolveExpressions implements InProcessingInterface {

    /**
     * In DDI, VTL expressions contain variables references instead of their name.
     * This method replaces the references with the names in calculated variables, filters and controls.
     */
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        // Calculated variables
        enoQuestionnaire.getVariables().stream()
                .filter(variable -> variable.getCollected().equals("CALCULATED")) //TODO: no filter required here when separate list for calculated variables will be implemented
                .forEach(this::resolveExpression);
        // Controls
        enoQuestionnaire.getControls().forEach(this::resolveExpression);
        // Filters
        enoQuestionnaire.getFilters().forEach(this::resolveExpression);
    }

    /**
     * Replace variable reference by variable name in given object's expression.
     */
    private void resolveExpression(EnoObjectWithExpression enoObject) {
        String expression = enoObject.getExpression();
        for (BindingReference bindingReference : enoObject.getBindingReferences()) {
            expression = expression.replace(bindingReference.getId(), bindingReference.getVariableName());
        }
        enoObject.setExpression(expression);
    }

}
