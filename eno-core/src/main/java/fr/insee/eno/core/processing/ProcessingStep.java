package fr.insee.eno.core.processing;

public interface ProcessingStep<T> {

    void apply(T processedObject);

}
