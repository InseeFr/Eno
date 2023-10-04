package fr.insee.eno.treatments.exceptions;

/**
 * Exception thrown when errors occurred on json deserialization of specific treatments
 */
public class SpecificTreatmentsDeserializationException extends RuntimeException {
    public SpecificTreatmentsDeserializationException(String message) {
        super(message);
    }
}
