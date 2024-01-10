package fr.insee.eno.core.exceptions.business;

public class EnoParametersException extends Exception {

    public EnoParametersException(String message) {
        super(message);
    }

    public EnoParametersException(String message, Exception e) {
        super(message, e);
    }

}
