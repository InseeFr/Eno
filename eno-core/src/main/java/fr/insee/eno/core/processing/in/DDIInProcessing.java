package fr.insee.eno.core.processing.in;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.ProcessingPipeline;
import fr.insee.eno.core.processing.in.steps.ddi.*;
import fr.insee.eno.core.reference.EnoCatalog;

public class DDIInProcessing {

    public void applyProcessing(EnoQuestionnaire enoQuestionnaire) {
        //
        EnoCatalog enoCatalog = new EnoCatalog(enoQuestionnaire);
        //
        ProcessingPipeline<EnoQuestionnaire> processingPipeline = new ProcessingPipeline<>();
        processingPipeline.start(enoQuestionnaire)
                .then(new DDIMoveUnitInQuestions())
                .then(new DDIResolveVariableReferencesInExpressions())
                .then(new DDIInsertDeclarations())
                .then(new DDIInsertControls())
                .then(new DDIInsertCodeLists())
                .then(new DDIResolveVariableReferencesInLabels(enoCatalog))
                .then(new DDIResolveSequencesStructure())
                .then(new DDIResolveFiltersScope())
                .then(new DDIResolveLoopsScope());
    }

}
