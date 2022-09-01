package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.EnoProcessingInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class EnoAddMissingVariables implements EnoProcessingInterface {

    private boolean isMissingVariables;

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.setMissingVariables(isMissingVariables);
        if (isMissingVariables) {
            log.info("Adding 'missing' variables in questionnaire.");
            log.warn("(Not implemented)"); //TODO
        }
    }

}
