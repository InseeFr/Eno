package fr.insee.eno.core.exceptions.business;

public class LunaticSerializationException extends RuntimeException {

    public LunaticSerializationException(String message) {
        super(message);
    }

    public LunaticSerializationException(String message, Exception e) {
        super(message, e);
    }

    public LunaticSerializationException(Exception e) {
        super(e);
    }

}
