package fr.insee.eno.core.processing.out;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.LunaticParameters;
import fr.insee.eno.core.processing.ProcessingPipeline;
import fr.insee.eno.core.processing.out.steps.lunatic.*;
import fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable.ShapefromAttributeRetrieval;
import fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable.ShapefromAttributeRetrievalFromVariableGroups;
import fr.insee.eno.core.processing.out.steps.lunatic.pagination.LunaticAddPageNumbers;
import fr.insee.eno.core.processing.out.steps.lunatic.resizing.LunaticAddResizing;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.Questionnaire;

public class LunaticProcessing {

    private final LunaticParameters parameters;

    public LunaticProcessing(LunaticParameters parameters) {
        this.parameters = parameters;
    }

    /**
     * Apply both technical and business processing on the given Lunatic questionnaire.
     * The Eno-model questionnaire object is required to accomplish this task.
     * @param lunaticQuestionnaire Lunatic questionnaire to be modified.
     * @param enoQuestionnaire Eno questionnaire that contains some required info.
     */
    public void applyProcessing(Questionnaire lunaticQuestionnaire, EnoQuestionnaire enoQuestionnaire) {
        //
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        assert enoIndex != null;
        EnoCatalog enoCatalog = new EnoCatalog(enoQuestionnaire);
        ShapefromAttributeRetrieval shapefromAttributeRetrieval = new ShapefromAttributeRetrievalFromVariableGroups();
        //
        ProcessingPipeline<Questionnaire> processingPipeline = new ProcessingPipeline<>();
        processingPipeline.start(lunaticQuestionnaire)
                .then(new LunaticAddGeneratingDate())
                .then(new LunaticSortComponents(enoQuestionnaire))
                .then(new LunaticLoopResolution(enoQuestionnaire))
                .then(new LunaticTableProcessing(enoQuestionnaire))
                .then(new LunaticVariablesValues())
                .thenIf(parameters.isMissingVariables(), new LunaticAddMissingVariables(enoCatalog, parameters.isMissingVariables()))
                .then(new LunaticAddResizing(enoQuestionnaire))
                .then(new LunaticAddHierarchy())
                .then(new LunaticAddPageNumbers(parameters.getLunaticPaginationMode()))
                .then(new LunaticAddCleaningVariables())
                .thenIf(parameters.isControls(), new LunaticAddControlFormat())
                .then(new LunaticReverseConsistencyControlLabel())
                .then(new LunaticAddShapeToCalculatedVariables(enoQuestionnaire, shapefromAttributeRetrieval))
                .then(new LunaticFinalizePairwise(enoQuestionnaire))
                .thenIf(parameters.isFilterResult(), new LunaticFilterResult(enoQuestionnaire, shapefromAttributeRetrieval));
    }

}
