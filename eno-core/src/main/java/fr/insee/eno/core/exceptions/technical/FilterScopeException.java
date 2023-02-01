package fr.insee.eno.core.exceptions.technical;

/**
 * A filter object has a scope which is a list of references. These references cannot refer to any type of object.
 * This exception is meant to be raised at runtime if a referenced object is of an unexpected type.
 * */
public class FilterScopeException extends RuntimeException {

    public FilterScopeException(String message) {
        super(message);
    }

}
