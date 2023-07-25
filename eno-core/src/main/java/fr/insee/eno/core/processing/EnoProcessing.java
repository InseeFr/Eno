package fr.insee.eno.core.processing;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
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
        if (parameters.isResponseTimeQuestion())
            new EnoAddResponseTimeSection().apply(enoQuestionnaire);
        if (parameters.isCommentSection())
            new EnoAddCommentSection().apply(enoQuestionnaire);
        new EnoModeSelection(parameters.getSelectedModes(), enoCatalog).apply(enoQuestionnaire);
        if (parameters.isSequenceNumbering())
            new EnoAddNumberingInSequences().apply(enoQuestionnaire);
        new EnoAddNumberingInQuestions(parameters.getQuestionNumberingMode()).apply(enoQuestionnaire);
        if (parameters.isArrowCharInQuestions())
            new EnoAddArrowCharInQuestions(enoCatalog).apply(enoQuestionnaire);
        //
        new EnoInsertComponentFilters().apply(enoQuestionnaire);
        new EnoResolveBindingReferences().apply(enoQuestionnaire);
    }

    private void ddiTechnicalProcessing(EnoQuestionnaire enoQuestionnaire) {
        new DDIMoveUnitInQuestions().apply(enoQuestionnaire);
        new DDIResolveVariableReferencesInExpressions().apply(enoQuestionnaire);
        new DDIInsertDeclarations().apply(enoQuestionnaire);
        new DDIInsertControls().apply(enoQuestionnaire);
        new DDIInsertCodeLists().apply(enoQuestionnaire);
        new DDIResolveVariableReferencesInLabels(enoCatalog).apply(enoQuestionnaire);
        new DDIResolveSequencesStructure().apply(enoQuestionnaire);
        new DDIResolveFiltersScope().apply(enoQuestionnaire);
    }

    private void poguesTechnicalProcessing(EnoQuestionnaire enoQuestionnaire) {
        //
    }

}
