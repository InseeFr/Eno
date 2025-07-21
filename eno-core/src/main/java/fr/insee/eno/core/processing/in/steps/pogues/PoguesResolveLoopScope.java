package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.ProcessingStep;

public class PoguesResolveLoopScope implements ProcessingStep<EnoQuestionnaire> {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        throw new UnsupportedOperationException("Loop scope is not implemented for Pogues");
    }

}
