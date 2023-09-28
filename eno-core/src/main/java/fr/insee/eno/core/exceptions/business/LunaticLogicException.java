package fr.insee.eno.core.exceptions.business;

/** Exception to be thrown if a blocking logical inconsistency is detected in the Lunatic questionnaire. */
public class LunaticLogicException extends RuntimeException {

    public LunaticLogicException(String message) {
        super(message);
    }

}
