package fr.insee.eno.core.processing.common;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
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
                .thenIf(parameters.isIdentificationQuestion(), new EnoAddIdentificationSection())
                .then(new EnoModeSelection(parameters.getSelectedModes(), enoCatalog))
                .thenIf(parameters.isSequenceNumbering(), new EnoAddNumberingInSequences(
                        parameters.getModeParameter()));
        // This step will be re-used after for question numbering reasons
        EnoAddPrefixInQuestionLabels prefixingStep = new EnoAddPrefixInQuestionLabels(
                parameters.isArrowCharInQuestions(), parameters.getQuestionNumberingMode(),
                parameters.getModeParameter());
        processingPipeline.then(prefixingStep)
                .thenIf(parameters.isResponseTimeQuestion(), new EnoAddResponseTimeSection(prefixingStep))
                .thenIf(parameters.isCommentSection(), new EnoAddCommentSection(prefixingStep))
                .then(new EnoInsertComponentFilters())
                .then(new EnoResolveBindingReferences());

        // Tooltip processing that is common to DDI and Pogues, but only concerns Lunatic
        if (Format.LUNATIC.equals(parameters.getOutFormat()))
            new EnoCleanTooltips(enoCatalog).apply(enoQuestionnaire);
    }

}
