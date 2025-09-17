package fr.insee.eno.exception;

/**
 * EnoGenerationException which is thrown when a error was occured during an xslt tranformation of Eno.
 */
public class EnoGenerationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EnoGenerationException(String message) {
        super(message);
    }
}