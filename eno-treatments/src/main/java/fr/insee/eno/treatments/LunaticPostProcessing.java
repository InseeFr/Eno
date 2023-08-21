package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Handle list of lunatic out processings
 */
@Slf4j
public class LunaticPostProcessing implements ProcessingStep<Questionnaire> {

    private final List<ProcessingStep<Questionnaire>> postProcessingSteps;

    public LunaticPostProcessing() {
        this.postProcessingSteps = new ArrayList<>();
    }

    public void addPostProcessing(ProcessingStep<Questionnaire> lunaticProcessing) {
        postProcessingSteps.add(lunaticProcessing);
    }

    @Override
    public void apply(Questionnaire questionnaire) {
        for(ProcessingStep<Questionnaire> lunaticProcessing : postProcessingSteps) {
            lunaticProcessing.apply(questionnaire);
        }
    }
}
