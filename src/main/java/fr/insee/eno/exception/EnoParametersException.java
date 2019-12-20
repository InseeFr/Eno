package fr.insee.eno.exception;

/**
 * EnoParametersException which is thrown when a error was occured during validation of the parameters.
 */
public class EnoParametersException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EnoParametersException(String message) {
        super(message);
    }
}