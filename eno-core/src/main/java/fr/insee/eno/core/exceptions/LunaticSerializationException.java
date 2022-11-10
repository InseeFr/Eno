package fr.insee.eno.core.exceptions;

public class LunaticSerializationException extends Exception {

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
