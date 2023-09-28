package fr.insee.eno.core.processing;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessingPipeline<T> {

    T processedObject;

    public ProcessingPipeline<T> start(T processedObject) {
        if (this.processedObject != null)
            throw new IllegalStateException("Start method has already been called.");
        log.debug("Start processing on " + processedObject);
        this.processedObject = processedObject;
        return this;
    }

    public ProcessingPipeline<T> then(ProcessingStep<T> processingStep) {
        if (this.processedObject == null)
            throw new IllegalStateException("Start method has not been called");
        processingStep.apply(processedObject);
        log.debug("Processing '{}' applied on {}", this.getClass().getSimpleName(), processedObject);
        return this;
    }

    public ProcessingPipeline<T> thenIf(boolean applyCondition, ProcessingStep<T> processingStep) {
        if (applyCondition)
            return then(processingStep);
        log.debug("Processing '{}' skipped on {}", this.getClass().getSimpleName(), processedObject);
        return this;
    }

}
