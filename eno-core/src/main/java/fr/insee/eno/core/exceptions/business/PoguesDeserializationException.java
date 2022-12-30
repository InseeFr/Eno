package fr.insee.eno.core.exceptions.business;

public class PoguesDeserializationException extends Exception {

    public PoguesDeserializationException(String message, Exception exception) {
        super(message, exception);
    }

}
