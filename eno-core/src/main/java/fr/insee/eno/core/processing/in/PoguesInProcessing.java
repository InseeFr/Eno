package fr.insee.eno.core.processing.in;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.ProcessingPipeline;
import fr.insee.eno.core.processing.in.steps.ddi.DDIInsertCodeLists;
import fr.insee.eno.core.processing.in.steps.ddi.DDIInsertMultipleChoiceLabels;
import fr.insee.eno.core.processing.in.steps.pogues.PoguesCodeResponseDetails;
import fr.insee.eno.core.processing.in.steps.pogues.PoguesNestedCodeLists;

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
                .then(new PoguesCodeResponseDetails())
        ;
    }

}
