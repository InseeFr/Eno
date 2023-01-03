package fr.insee.eno.core.exceptions.technical;

public class MethodBindingException extends RuntimeException {

    public MethodBindingException(String message, Exception exception) {
        super(message, exception);
    }

}
