package fr.insee.eno.exception;
public class EnoGenerationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EnoGenerationException(String message) {
        super(message);
    }
}