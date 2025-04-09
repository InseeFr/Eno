package fr.insee.eno.core.reference;

import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.VariableType;

import java.util.HashMap;
import java.util.Map;

public class PoguesIndex implements VariableIndex {

    private final Map<String, Object> index = new HashMap<>();

    public Object get(String id) {
        return index.get(id);
    }

    public void indexVariables(Questionnaire poguesQuestionnaire) {
        poguesQuestionnaire.getVariables().getVariable()
                .forEach(poguesVariable -> index.put(poguesVariable.getId(), poguesVariable));
    }

    /**
     * Utility method to test if the index contains a variable whose name is the given variable name.
     * @param variableName A variable name.
     * @return True if the given variable name corresponds to a variable in the index.
     */
    public boolean containsVariable(String variableName) {
        return index.values().stream()
                .filter(VariableType.class::isInstance).map(VariableType.class::cast)
                .map(VariableType::getName).anyMatch(variableName::equals);
    }

}
