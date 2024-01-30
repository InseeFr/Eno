package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.ProcessingStep;
import lombok.extern.slf4j.Slf4j;

/**
 * For some reason, an "Insee" prefix is added in the questionnaire identifier by the current Pogues to DDI
 * transformation. This processing removes this prefix.
 */
@Slf4j
public class DDICleanUpQuestionnaireId implements ProcessingStep<EnoQuestionnaire> {

    private static final String INSEE_PREFIX = "INSEE-";

    /**
     * Removes the "Insee" prefix in the given questionnaire's id.
     * @param enoQuestionnaire A Eno questionnaire.
     */
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        if (enoQuestionnaire.getId() == null) // Shouldn't happen but you never know...
            throw new MappingException("Questionnaire as a null identifier.");
        enoQuestionnaire.setId(enoQuestionnaire.getId().replace(INSEE_PREFIX, ""));
    }

}
