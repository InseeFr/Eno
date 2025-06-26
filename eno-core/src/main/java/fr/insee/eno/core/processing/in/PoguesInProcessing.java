package fr.insee.eno.core.processing.in;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.ProcessingPipeline;
import fr.insee.eno.core.processing.in.steps.ddi.DDIInsertCodeLists;
import fr.insee.eno.core.processing.in.steps.ddi.DDIInsertMultipleChoiceLabels;
import fr.insee.eno.core.processing.in.steps.pogues.*;

public class PoguesInProcessing {

    public void applyProcessing(EnoQuestionnaire enoQuestionnaire) {
        ProcessingPipeline<EnoQuestionnaire> processingPipeline = new ProcessingPipeline<>();
        // Note: some steps share the same logic between Pogues and DDI, DDI steps are re-used in that case
        processingPipeline.start(enoQuestionnaire)
                //.then(new PoguesCheckFilterMode())
                //.then(new PoguesCheckExpressionLanguage()) // Disabled while Pogues DDI is used
                .then(new PoguesNestedCodeLists())
                .then(new DDIInsertCodeLists())
                .then(new DDIInsertMultipleChoiceLabels())
                .then(new PoguesEditUnitLabelType())
                .then(new PoguesRoundaboutLoops())
                .then(new PoguesSortLoops()) // this step is required for Pogues+DDI mapping (might be removed when Pogues only)
                //.then(new PoguesResolveLoopScope()) // Disabled since it isn't implemented yet
                //.then(new PoguesCodeResponseDetails()) // Disabled since it isn't implemented yet
        ;
    }

}
