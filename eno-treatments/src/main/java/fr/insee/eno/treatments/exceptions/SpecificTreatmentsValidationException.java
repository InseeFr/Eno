package fr.insee.eno.treatments.exceptions;

/**
 * Exception thrown when errors occurred on json validation of specific treatments
 */
public class SpecificTreatmentsValidationException extends RuntimeException {

    public SpecificTreatmentsValidationException(String message) {
        super(message);
    }
}
