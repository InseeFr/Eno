package fr.insee.eno.core.processing.common;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.ProcessingPipeline;
import fr.insee.eno.core.processing.common.steps.*;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.eno.core.reference.EnoIndex;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnoProcessing {

    private final EnoParameters parameters;

    public EnoProcessing(EnoParameters parameters) {
        this.parameters = parameters;
    }

    public void applyProcessing(EnoQuestionnaire enoQuestionnaire) {
        //
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        assert enoIndex != null;
        EnoCatalog enoCatalog = new EnoCatalog(enoQuestionnaire);
        //
        ProcessingPipeline<EnoQuestionnaire> processingPipeline = new ProcessingPipeline<>();
        processingPipeline.start(enoQuestionnaire)
                .thenIf(parameters.isResponseTimeQuestion(), new EnoAddResponseTimeSection())
                .thenIf(parameters.isCommentSection(), new EnoAddCommentSection())
                .then(new EnoModeSelection(parameters.getSelectedModes(), enoCatalog))
                .thenIf(parameters.isSequenceNumbering(), new EnoAddNumberingInSequences())
                .then(new EnoAddNumberingInQuestions(parameters.getQuestionNumberingMode()))
                .thenIf(parameters.isArrowCharInQuestions(), new EnoAddArrowCharInQuestions(enoCatalog))
                .then(new EnoInsertComponentFilters())
                .then(new EnoResolveBindingReferences());
    }

}
