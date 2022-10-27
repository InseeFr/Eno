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
     * TODO
     * @param lunaticQuestionnaire Lunatic questionnaire to be modified.
     * @param enoQuestionnaire Eno questionnaire that contains some required info.
     */
    public void applyProcessing(Questionnaire lunaticQuestionnaire, EnoQuestionnaire enoQuestionnaire) {
        //
        LunaticCatalog lunaticCatalog = new LunaticCatalog(lunaticQuestionnaire);
        //
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        assert enoIndex != null;
        //
        new LunaticSortComponents(enoQuestionnaire, lunaticCatalog).apply(lunaticQuestionnaire);
        new LunaticAddGeneratingDate().apply(lunaticQuestionnaire);
        new LunaticAddBindingDependencies(lunaticCatalog, enoIndex).apply(lunaticQuestionnaire);
        new LunaticAddPageNumbers(parameters.getLunaticPaginationMode()).apply(lunaticQuestionnaire);
        new LunaticAddHierarchy().apply(lunaticQuestionnaire);
        new LunaticAddMissingVariables(parameters.isMissingVariables()).apply(lunaticQuestionnaire);
    }

}
