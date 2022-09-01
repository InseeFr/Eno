package fr.insee.eno.core.processing;

import fr.insee.eno.core.model.EnoQuestionnaire;

/**
 * Processing interface for core processing classes.
 * "Core" processing classes are implementation of business oriented processing. */
public interface EnoProcessingInterface {

    /**
     * Apply the processing on the model object.
     * @param enoQuestionnaire Eno questionnaire to be processed.
     */
    void apply(EnoQuestionnaire enoQuestionnaire);

}
