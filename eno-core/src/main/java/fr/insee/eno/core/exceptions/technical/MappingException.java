package fr.insee.eno.core.exceptions.technical;

public class MappingException extends RuntimeException {

    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Exception exception) {
        super(message, exception);
    }

}
