package fr.insee.eno.core.exceptions.business;

/** Exception to be thrown when invalid information is detected in a loop object that makes it impossible to be
 * resolved in Lunatic. */
public class LunaticLoopException extends RuntimeException {

    public LunaticLoopException(String message) {
        super(message);
    }

}
