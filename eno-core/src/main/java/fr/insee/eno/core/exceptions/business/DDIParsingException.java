package fr.insee.eno.core.exceptions.business;

public class DDIParsingException extends Exception {

    public DDIParsingException(String message) {
        super(message);
    }

    public DDIParsingException(String message, Exception e) {
        super(message, e);
    }

    public DDIParsingException(Exception e) {
        super(e);
    }

}
