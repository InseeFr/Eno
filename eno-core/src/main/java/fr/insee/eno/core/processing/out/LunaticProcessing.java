package fr.insee.eno.core.processing.out;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.LunaticParameters;
import fr.insee.eno.core.processing.ProcessingPipeline;
import fr.insee.eno.core.processing.out.steps.lunatic.*;
import fr.insee.eno.core.processing.out.steps.lunatic.pagination.LunaticAddPageNumbers;
import fr.insee.eno.core.processing.out.steps.lunatic.resizing.LunaticAddResizing;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.Questionnaire;

public class LunaticProcessing {

    private final EnoParameters enoParameters;
    private final LunaticParameters lunaticParameters;

    public LunaticProcessing(EnoParameters enoParameters) {
        this.enoParameters = enoParameters;
        this.lunaticParameters = enoParameters.getLunaticParameters();
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
        //
        ProcessingPipeline<Questionnaire> processingPipeline = new ProcessingPipeline<>();
        processingPipeline.start(lunaticQuestionnaire)
                .then(new LunaticAddGeneratingDate())
                .then(new LunaticSortComponents(enoQuestionnaire))
                .then(new LunaticLoopResolution(enoQuestionnaire))
                .then(new LunaticTableProcessing(enoQuestionnaire))
                .then(new LunaticInsertUniqueChoiceDetails(enoQuestionnaire))
                .then(new LunaticInsertCodeFilters(enoQuestionnaire))
                .then(new LunaticEditLabelTypes()) // this step should be temporary
                .then(new LunaticSuggestersConfiguration(enoQuestionnaire))
                .then(new LunaticVariablesDimension(enoQuestionnaire))
                .then(new LunaticSuggesterOptionResponses())
                .thenIf(lunaticParameters.isMissingVariables(),
                        new LunaticAddMissingVariables(enoCatalog, lunaticParameters.isMissingVariables()))
                .then(new LunaticAddResizing(enoQuestionnaire))
                .then(new LunaticAddPageNumbers(lunaticParameters.getLunaticPaginationMode()))
                .then(new LunaticResponseTimeQuestionPagination())
                .then(new LunaticAddCleaningVariables(enoQuestionnaire))
                .thenIf(lunaticParameters.isControls(), new LunaticAddControlFormat())
                .then(new LunaticReverseConsistencyControlLabel())
                .then(new LunaticFinalizePairwise(enoQuestionnaire))
                .thenIf(lunaticParameters.isFilterResult(), new LunaticFilterResult(enoIndex))
                .then(new LunaticShapeFrom())
                .thenIf(enoParameters.getModeParameter().equals(EnoParameters.ModeParameter.CAWI), new LunaticSequenceDescription())
                .then(new LunaticInputNumberDescription(enoParameters.getLanguage()))
                .then(new LunaticDurationDescription())
                .thenIf(lunaticParameters.isQuestionWrapping(), new LunaticQuestionComponent())
                .then(new LunaticRoundaboutLoops(enoQuestionnaire));
    }

}
