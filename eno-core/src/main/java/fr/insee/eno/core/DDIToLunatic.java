package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.Questionnaire;

import java.io.InputStream;

public class DDIToLunatic implements InToOut<Questionnaire> {

    /**
     * Transform given DDI input stream into a Lunatic questionnaire object using parameters given.
     * @param ddiInputStream Input stream of a DDI document.
     * @param enoParameters Eno parameters object.
     * @return Lunatic questionnaire object.
     * @throws DDIParsingException if the input stream given cannot be parsed to a DDI object.
     */
    public Questionnaire transform(InputStream ddiInputStream, EnoParameters enoParameters)
            throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = new DDIToEno().transform(ddiInputStream, enoParameters);
        //
        return new EnoToLunatic().transform(enoQuestionnaire, enoParameters);
    }
}
