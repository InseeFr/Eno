package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.processing.ProcessingStep;

public class PoguesEditUnitLabelType implements ProcessingStep<EnoQuestionnaire> {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(NumericQuestion.class::isInstance)
                .map(NumericQuestion.class::cast)
                .filter(numericQuestion -> numericQuestion.getUnit() != null)
                .forEach(numericQuestion -> numericQuestion.getUnit().setType("VTL"));
    }
}
