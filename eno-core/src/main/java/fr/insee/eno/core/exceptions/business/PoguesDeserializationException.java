package fr.insee.eno.core.exceptions.business;

public class PoguesDeserializationException extends ParsingException {

    public PoguesDeserializationException(String message, Exception exception) {
        super(message, exception);
    }

}
