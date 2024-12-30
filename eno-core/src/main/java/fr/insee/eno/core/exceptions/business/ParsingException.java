package fr.insee.eno.core.exceptions.business;

public abstract class ParsingException extends Exception {

    ParsingException(String message) {
        super(message);
    }

    ParsingException(String message, Exception e) {
        super(message, e);
    }

    ParsingException(Exception e) {
        super(e);
    }
}
