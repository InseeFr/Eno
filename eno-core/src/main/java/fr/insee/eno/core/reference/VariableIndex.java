package fr.insee.eno.core.reference;

public interface VariableIndex {
    Object get(String id);
    /**
     * Utility method to test if the index contains a variable whose name is the given variable name.
     * @param variableName A variable name.
     * @return True if the given variable name corresponds to a variable in the index.
     */
    boolean containsVariable(String variableName);
}
