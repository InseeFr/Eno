package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.ProcessingStep;

public class EnoAddIdentificationSection implements ProcessingStep<EnoQuestionnaire> {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        throw new UnsupportedOperationException("The addition of the 'identification' section is not supported yet.");
    }

}
