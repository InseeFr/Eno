package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.common.EnoProcessing;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.pogues.model.Questionnaire;

import java.io.InputStream;

public class PoguesToEno implements InToEno {

    /**
     * Transform given Pogues input stream into a Eno questionnaire object using parameters given.
     * @param poguesInputStream Input stream of a Pogues json questionnaire.
     * @param enoParameters Eno parameters object.
     * @return Lunatic questionnaire object.
     * @throws PoguesDeserializationException if the input stream given cannot be parsed to a Pogues questionnaire.
     */
    public EnoQuestionnaire transform(InputStream poguesInputStream, EnoParameters enoParameters)
            throws PoguesDeserializationException {
        //
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(poguesInputStream);
        //
        PoguesMapper poguesMapper = new PoguesMapper();
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        EnoProcessing enoProcessing = new EnoProcessing(enoParameters);
        enoProcessing.applyProcessing(enoQuestionnaire);
        //
        return enoQuestionnaire;
    }

}
