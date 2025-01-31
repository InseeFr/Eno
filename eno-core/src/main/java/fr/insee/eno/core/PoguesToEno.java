package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.common.EnoProcessing;
import fr.insee.eno.core.processing.in.PoguesInProcessing;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.pogues.model.Questionnaire;

import java.io.InputStream;

public class PoguesToEno implements InToEno {

    private Questionnaire poguesQuestionnaire;

    private PoguesToEno(Questionnaire poguesQuestionnaire) {
        this.poguesQuestionnaire = poguesQuestionnaire;
    }

    public static PoguesToEno fromInputStream(InputStream poguesInputStream) throws PoguesDeserializationException {
        return new PoguesToEno(PoguesDeserializer.deserialize(poguesInputStream));
    }

    public static PoguesToEno fromObject(Questionnaire poguesQuestionnaire) {
        return new PoguesToEno(poguesQuestionnaire);
    }

    /**
     * Transform given Pogues input stream into a Eno questionnaire object using parameters given.
     * @param poguesInputStream Input stream of a Pogues json questionnaire.
     * @param enoParameters Eno parameters object.
     * @return Lunatic questionnaire object.
     * @throws PoguesDeserializationException if the input stream given cannot be parsed to a Pogues questionnaire.
     * @deprecated use other transform method.
     */
    @Deprecated(since = "3.33.0")
    public EnoQuestionnaire transform(InputStream poguesInputStream, EnoParameters enoParameters)
            throws PoguesDeserializationException {
        poguesQuestionnaire = PoguesDeserializer.deserialize(poguesInputStream);
        return transform(enoParameters);
    }

    public EnoQuestionnaire transform(EnoParameters enoParameters) {
        //
        PoguesMapper poguesMapper = new PoguesMapper();
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        PoguesInProcessing poguesInProcessing = new PoguesInProcessing();
        poguesInProcessing.applyProcessing(enoQuestionnaire);
        //
        EnoProcessing enoProcessing = new EnoProcessing(enoParameters);
        enoProcessing.applyProcessing(enoQuestionnaire);
        //
        return enoQuestionnaire;
    }

}
