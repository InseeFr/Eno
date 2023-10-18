package fr.insee.eno.core;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.out.LunaticProcessing;
import fr.insee.lunatic.model.flat.Questionnaire;

public class EnoToLunatic {

    private EnoToLunatic() {}

    /**
     * Transform given Eno questionnaire into a Lunatic questionnaire object using parameters given.
     * @param enoQuestionnaire An Eno Questionnaire object.
     * @param enoParameters Eno parameters object.
     * @return Lunatic questionnaire object.
     */
    public static Questionnaire transform(EnoQuestionnaire enoQuestionnaire, EnoParameters enoParameters) {
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        //
        LunaticProcessing lunaticProcessing = new LunaticProcessing(enoParameters.getLunaticParameters());
        lunaticProcessing.applyProcessing(lunaticQuestionnaire, enoQuestionnaire);
        //
        return lunaticQuestionnaire;
    }

}
