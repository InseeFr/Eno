package fr.insee.eno.core;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.lunatic.model.flat.Questionnaire;

import java.io.InputStream;

public class DDIToLunatic implements InToOut<Questionnaire> {

    private DDIInstanceDocument ddiQuestionnaire;

    private DDIToLunatic(DDIInstanceDocument ddiQuestionnaire) {
        this.ddiQuestionnaire = ddiQuestionnaire;
    }

    public static DDIToLunatic fromInputStream(InputStream ddiInputStream) throws DDIParsingException {
        return new DDIToLunatic(DDIDeserializer.deserialize(ddiInputStream));
    }

    public static DDIToLunatic fromObject(DDIInstanceDocument ddiInstanceDocument) {
        return new DDIToLunatic(ddiInstanceDocument);
    }

    /**
     * Transform given DDI input stream into a Lunatic questionnaire object using parameters given.
     * @param ddiInputStream Input stream of a DDI document.
     * @param enoParameters Eno parameters object.
     * @return Lunatic questionnaire object.
     * @throws DDIParsingException if the input stream given cannot be parsed to a DDI object.
     * @deprecated use other transform method.
     */
    @Deprecated(since = "3.33.0")
    public Questionnaire transform(InputStream ddiInputStream, EnoParameters enoParameters)
            throws DDIParsingException {
        ddiQuestionnaire = DDIDeserializer.deserialize(ddiInputStream);
        return transform(enoParameters);
    }

    public Questionnaire transform(EnoParameters enoParameters) {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.fromObject(ddiQuestionnaire).transform(enoParameters);
        //
        return new EnoToLunatic().transform(enoQuestionnaire, enoParameters);
    }
}
