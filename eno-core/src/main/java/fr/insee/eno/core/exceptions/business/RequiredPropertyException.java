package fr.insee.eno.core.exceptions.business;

public class RequiredPropertyException extends RuntimeException {

    public  RequiredPropertyException(String message) {
        super(message);
    }

}
