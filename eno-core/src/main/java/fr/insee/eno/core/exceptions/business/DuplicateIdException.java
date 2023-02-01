package fr.insee.eno.core.exceptions.business;

/** Exception to be raised at runtime if an entry document contains duplicate ids. */
public class DuplicateIdException extends RuntimeException {

    public DuplicateIdException(String message) {
        super(message);
    }

}
