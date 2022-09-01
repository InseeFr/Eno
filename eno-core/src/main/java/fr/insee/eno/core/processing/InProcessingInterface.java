package fr.insee.eno.core.processing;

import fr.insee.eno.core.model.EnoQuestionnaire;

/**
 * Processing interface for processing classes corresponding to input formats.
 * The mapping of some information cannot be completely fulfilled by the 'in' mapping annotations.
 * These classes are designed to implement technical processing on the model object after mapping,
 * so that the following processing can be done independently of the input format. */
public interface InProcessingInterface {

    /**
     * Apply the processing on the model object.
     * @param enoQuestionnaire Eno questionnaire to be processed.
     */
    void apply(EnoQuestionnaire enoQuestionnaire);

}
