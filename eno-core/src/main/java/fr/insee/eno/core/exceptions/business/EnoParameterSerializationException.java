package fr.insee.eno.core.exceptions.business;

public class EnoParameterSerializationException extends Exception {

    public EnoParameterSerializationException(String message) {
        super(message);
    }

    public EnoParameterSerializationException(String message, Exception e) {
        super(message, e);
    }

    public EnoParameterSerializationException(Exception e) {
        super(e);
    }

}
