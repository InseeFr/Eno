package fr.insee.eno.core.reference;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.pogues.model.VariableType;

import java.util.HashMap;
import java.util.Map;

public class EnoVariableIndex implements VariableIndex{
    private final Map<String, Variable> index = new HashMap<>();

    public void indexVariables(EnoQuestionnaire enoQuestionnaire){
        enoQuestionnaire.getVariables().forEach(
                variable -> index.put(variable.getId(), variable)
        );
    }

    @Override
    public Object get(String id) {
        return index.get(id);
    }

    @Override
    public boolean containsVariable(String variableName) {
        return index.values().stream()
                .map(Variable::getName)
                .anyMatch(variableName::equals);
    }
}
