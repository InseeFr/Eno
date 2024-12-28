package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.Questionnaire;

import java.io.InputStream;

public class PoguesToLunatic {

    private PoguesToLunatic() {}

    /**
     * Transform given Pogues input stream into a Lunatic questionnaire object using parameters given.
     * @param poguesInputStream Input stream of a Pogues json questionnaire.
     * @param enoParameters Eno parameters object.
     * @return Lunatic questionnaire object.
     * @throws PoguesDeserializationException if the input stream given cannot be parsed to a Pogues questionnaire.
     */
    public Questionnaire transform(InputStream poguesInputStream, EnoParameters enoParameters)
            throws PoguesDeserializationException {
        //
        EnoQuestionnaire enoQuestionnaire = new PoguesToEno().transform(poguesInputStream, enoParameters);
        //
        return new EnoToLunatic().transform(enoQuestionnaire, enoParameters);
    }
}
