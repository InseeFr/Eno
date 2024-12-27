package fr.insee.eno.core.processing.in;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.ProcessingPipeline;
import fr.insee.eno.core.processing.in.steps.pogues.PoguesCheckExpressionLanguage;
import fr.insee.eno.core.processing.in.steps.pogues.PoguesCheckFilterMode;
import fr.insee.eno.core.processing.in.steps.pogues.PoguesNestedCodeLists;

public class PoguesInProcessing {

    public void applyProcessing(EnoQuestionnaire enoQuestionnaire) {
        ProcessingPipeline<EnoQuestionnaire> processingPipeline = new ProcessingPipeline<>();
        processingPipeline.start(enoQuestionnaire)
                .then(new PoguesCheckFilterMode())
                .then(new PoguesCheckExpressionLanguage())
                .then(new PoguesNestedCodeLists());
    }

}
