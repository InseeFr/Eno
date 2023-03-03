package fr.insee.eno.core.processing;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.impl.*;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.reference.LunaticCatalog;
import fr.insee.lunatic.model.flat.*;

public class LunaticProcessing {

    private final EnoParameters parameters;

    public LunaticProcessing() {
        this.parameters = new EnoParameters();
    }

    public LunaticProcessing(EnoParameters parameters) {
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
        LunaticCatalog lunaticCatalog = new LunaticCatalog(lunaticQuestionnaire);
        //
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        assert enoIndex != null;
        // TODO: (not a priority) implement something to manage the order of processing classes calls
        new LunaticSortComponents(enoQuestionnaire, lunaticCatalog).apply(lunaticQuestionnaire);
        new LunaticAddGeneratingDate().apply(lunaticQuestionnaire);
        new LunaticAddBindingDependencies(lunaticCatalog, enoIndex).apply(lunaticQuestionnaire);
        new LunaticAddMissingVariables(parameters.isMissingVariables()).apply(lunaticQuestionnaire);
        new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticAddPageNumbers(parameters.getLunaticPaginationMode()).apply(lunaticQuestionnaire);
        new LunaticAddHierarchy().apply(lunaticQuestionnaire);
    }

}
