package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.lunatic.model.flat.Questionnaire;

import java.io.InputStream;

public class PoguesToLunatic implements InToOut<Questionnaire> {

    private fr.insee.pogues.model.Questionnaire poguesQuestionnaire;

    private PoguesToLunatic(fr.insee.pogues.model.Questionnaire poguesQuestionnaire) {
        this.poguesQuestionnaire = poguesQuestionnaire;
    }

    public static PoguesToLunatic fromInputStream(InputStream poguesInputStream) throws PoguesDeserializationException {
        return new PoguesToLunatic(PoguesDeserializer.deserialize(poguesInputStream));
    }

    public static PoguesToLunatic fromObject(fr.insee.pogues.model.Questionnaire poguesQuestionnaire) {
        return new PoguesToLunatic(poguesQuestionnaire);
    }

    /**
     * Transform given Pogues input stream into a Lunatic questionnaire object using parameters given.
     * @param poguesInputStream Input stream of a Pogues json questionnaire.
     * @param enoParameters Eno parameters object.
     * @return Lunatic questionnaire object.
     * @throws PoguesDeserializationException if the input stream given cannot be parsed to a Pogues questionnaire.
     * @deprecated use other transform method.
     */
    @Deprecated(since = "3.33.0")
    public Questionnaire transform(InputStream poguesInputStream, EnoParameters enoParameters)
            throws PoguesDeserializationException {
        poguesQuestionnaire = PoguesDeserializer.deserialize(poguesInputStream);
        return transform(enoParameters);
    }

    public Questionnaire transform(EnoParameters enoParameters) {
        //
        EnoQuestionnaire enoQuestionnaire = PoguesToEno.fromObject(poguesQuestionnaire).transform(enoParameters);
        //
        return new EnoToLunatic().transform(enoQuestionnaire, enoParameters);

    }
}
