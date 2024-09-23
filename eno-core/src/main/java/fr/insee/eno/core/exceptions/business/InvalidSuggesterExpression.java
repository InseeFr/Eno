package fr.insee.eno.core.exceptions.business;

/**
 * Exception to be thrown if the magic VTL expression (that uses a left join) used for suggester option responses
 * is invalid.
 */
public class InvalidSuggesterExpression extends RuntimeException {

    public InvalidSuggesterExpression(String message) {
        super(message);
    }

}
