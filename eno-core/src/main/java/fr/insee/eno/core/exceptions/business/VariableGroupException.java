package fr.insee.eno.core.exceptions.business;

/**
 * The Pairwise variableGroup must belong to a variableGroup of type iterable object (Loop or dynamicTable),
 * if it does not, an exception must be thrown.
 */
public class VariableGroupException extends ParsingException {

    public VariableGroupException(String message) {
        super(message);
    }
}
