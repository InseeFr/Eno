package fr.insee.eno.core.processing.in;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.ProcessingPipeline;
import fr.insee.eno.core.processing.in.steps.ddi.*;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.eno.core.reference.EnoIndex;

public class DDIInProcessing {

    public void applyProcessing(EnoQuestionnaire enoQuestionnaire) {
        //
        ProcessingPipeline<EnoQuestionnaire> processingPipeline = new ProcessingPipeline<>();
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        processingPipeline.start(enoQuestionnaire)
                .then(new DDICleanUpQuestionnaireId())
                .then(new DDIMarkRowControls())
                .then(new DDIMarkRoundaboutFilters())
                .then(new DDIMoveUnitInQuestions(enoIndex))
                .then(new DDIInsertResponseInTableCells())
                .then(new DDIInsertDetailResponses())
                .then(new DDIInsertMultipleChoiceLabels())
                .then(new DDIResolveVariableReferencesInExpressions())
                .then(new DDIInsertDeclarations(enoIndex))
                .then(new DDIInsertControls())
                .then(new DDIManagePairwiseId(enoIndex))
                .then(new DDIDeserializeSuggesterConfiguration())
                .then(new DDIInsertNoDataCellLabels())
                .then(new DDIInsertCodeLists());
        //
        EnoCatalog enoCatalog = new EnoCatalog(enoQuestionnaire);
        //
        processingPipeline.then(new DDIResolveVariableReferencesInLabels(enoCatalog))
                .then(new DDIResolveSequencesStructure())
                .then(new DDIResolveFiltersScope())
                .then(new DDIResolveLoopsScope());
    }

}
