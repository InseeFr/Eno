package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.exceptions.business.IllegalPoguesElementException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.ProcessingStep;

public class PoguesCheckFilterMode implements ProcessingStep<EnoQuestionnaire> {
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        String filterMode = enoQuestionnaire.getFilterMode();
        if (filterMode == null) // if this property is missing it's alright, might be removed later on anyway
            return;
        if (!"FILTER".equals(filterMode))
            throw new IllegalPoguesElementException("'" + filterMode + "' filter mode is not allowed.");
    }
}
