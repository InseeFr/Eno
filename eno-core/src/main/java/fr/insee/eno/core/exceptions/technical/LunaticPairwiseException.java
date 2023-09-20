package fr.insee.eno.core.exceptions.technical;

/** Exception to be thrown if illegal content if found in a Lunatic pairwise object.
 * This exception exists since current Lunatic pairwise modeling relies on implicit conventions. */
public class LunaticPairwiseException extends RuntimeException {

    public LunaticPairwiseException(String message) {
        super(message);
    }

}
