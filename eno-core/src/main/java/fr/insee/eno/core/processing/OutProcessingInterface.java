package fr.insee.eno.core.processing;

/**
 * Processing interface for processing classes corresponding to output formats.
 * These classes are designed to implement business oriented or technical processing that are specific to the
 * output format. */
public interface OutProcessingInterface<T> {

    /**
     * Apply the processing on the out object.
     * @param outObject Out object to be processed.
     */
    void apply(T outObject);

}
