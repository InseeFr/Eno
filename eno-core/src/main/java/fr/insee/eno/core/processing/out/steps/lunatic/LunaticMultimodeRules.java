package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.Questionnaire;

public class LunaticMultimodeRules implements ProcessingStep<Questionnaire> {

    private static final String IS_MOVED_RULE = "IS_MOVED";

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        return;
    }

}
