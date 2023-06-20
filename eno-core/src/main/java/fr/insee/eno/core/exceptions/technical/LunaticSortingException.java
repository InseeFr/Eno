package fr.insee.eno.core.exceptions.technical;

/**
 * Exception to be thrown if an error occurs during Lunatic sorting components processing.
 */
public class LunaticSortingException extends RuntimeException {

    public LunaticSortingException(String message) {
        super(message);
    }

}
