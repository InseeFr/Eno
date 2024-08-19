package fr.insee.eno.core.mappers;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.Format;
import fr.insee.pogues.model.Questionnaire;

public class PoguesMapper extends Mapper {

    public PoguesMapper() {
        this.format = Format.POGUES;
    }

    public void mapPoguesQuestionnaire(Questionnaire poguesQuestionnaire, EnoQuestionnaire enoQuestionnaire) {
        throw new UnsupportedOperationException("Pogues mapping is not implemented yet.");
    }

}
