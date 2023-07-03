package fr.insee.eno.core.exceptions.business;

/** Exception to be thrown when invalid information is detected in a loop object that makes it impossible to be
 * resolved in Lunatic. */
public class LunaticLoopResolutionException extends RuntimeException {

    public LunaticLoopResolutionException(String message) {
        super(message);
    }

}
