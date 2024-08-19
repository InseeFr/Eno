package fr.insee.eno.ws.exception;

/**
 * Generic error to be thrown when an exception occurs during the Pogues to Lunatic transformation.
 */
public class PoguesToLunaticException extends RuntimeException {

    public PoguesToLunaticException(Exception e) {
        super(e);
    }

}
