package fr.insee.eno.core.processing;

import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.impl.*;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.eno.core.reference.EnoIndex;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnoProcessing {

    private final EnoParameters parameters;

    private EnoCatalog enoCatalog;

    /** Constructor with default parameters. */
    public EnoProcessing() {
        this.parameters = new EnoParameters();
    }

    public EnoProcessing(EnoParameters parameters) {
        this.parameters = parameters;
    }

    public void applyProcessing(EnoQuestionnaire enoQuestionnaire, Format inputFormat) {
        //
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        assert enoIndex != null;
        enoCatalog = new EnoCatalog(enoQuestionnaire);
        //
        switch (inputFormat) {
            case DDI -> ddiTechnicalProcessing(enoQuestionnaire);
            case POGUES -> poguesTechnicalProcessing(enoQuestionnaire);
        }
        //
        coreProcessing(enoQuestionnaire);
    }

    private void coreProcessing(EnoQuestionnaire enoQuestionnaire) {
        new EnoAddVersions().apply(enoQuestionnaire);
        if (parameters.isCommentSection())
            new EnoAddCommentSection().apply(enoQuestionnaire);
        new EnoModeSelection(parameters.getSelectedModes(), enoCatalog).apply(enoQuestionnaire);
        if (parameters.isSequenceNumbering())
            new EnoAddNumberingInSequences().apply(enoQuestionnaire);
        new EnoAddNumberingInQuestions(parameters.getQuestionNumberingMode()).apply(enoQuestionnaire);
        if (parameters.isArrowCharInQuestions())
            new EnoAddArrowCharInQuestions(enoCatalog).apply(enoQuestionnaire);
    }

    private void ddiTechnicalProcessing(EnoQuestionnaire enoQuestionnaire) {
        new DDIMoveUnitInQuestions().apply(enoQuestionnaire);
        new DDIResolveDeclarationLabels(enoCatalog).apply(enoQuestionnaire);
        new DDIResolveExpressions().apply(enoQuestionnaire);
        new DDIInsertDeclarations().apply(enoQuestionnaire);
        new DDIInsertControls().apply(enoQuestionnaire);
        new DDIInsertFilters().apply(enoQuestionnaire);
        /* TODO: warning: in all "insert" methods, parent object is not changed.
         *   2 solutions : set it each time (and then write tests!) or remove the parent attribute that is actually not used yet.
         * Solution 2 selected: parent attribute marked as deprecated, to be removed. */
    }

    private void poguesTechnicalProcessing(EnoQuestionnaire enoQuestionnaire) {
        //
    }

}
