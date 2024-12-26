package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.exceptions.business.IllegalPoguesElementException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.ProcessingStep;

public class PoguesCheckExpressionLanguage implements ProcessingStep<EnoQuestionnaire> {
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        String expressionLanguage = enoQuestionnaire.getExpressionLanguage();
        if (expressionLanguage == null) // if this property is missing it's alright, might be removed later on anyway
            return;
        if (!"VTL".equals(expressionLanguage))
            throw new IllegalPoguesElementException("'" + expressionLanguage + "' expression language is not allowed.");
    }
}
