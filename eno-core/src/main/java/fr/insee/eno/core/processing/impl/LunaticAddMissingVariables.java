package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.EnoProcessingInterface;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.lunatic.model.flat.Questionnaire;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class LunaticAddMissingVariables implements OutProcessingInterface<Questionnaire> {
    private boolean isMissingVariables;

    public void apply(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.setMissing(isMissingVariables);
        if (isMissingVariables) {
            log.info("Adding 'missing' variables in Lunatic questionnaire.");
            log.warn("(Not implemented)"); //TODO
        }
    }

}
