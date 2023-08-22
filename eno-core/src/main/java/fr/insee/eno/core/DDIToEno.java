package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.common.EnoProcessing;
import fr.insee.eno.core.processing.in.DDIInProcessing;
import instance33.DDIInstanceDocument;

import java.io.InputStream;

public class DDIToEno {

    private DDIToEno() {}

    /**
     * Transform given DDI input stream into a Eno questionnaire object using parameters given.
     * @param ddiInputStream Input stream of a DDI document.
     * @param enoParameters Eno parameters object.
     * @return Lunatic questionnaire object.
     * @throws DDIParsingException if the input stream given cannot be parsed to a DDI object.
     */
    public static EnoQuestionnaire transform(InputStream ddiInputStream, EnoParameters enoParameters)
            throws DDIParsingException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(ddiInputStream);
        //
        DDIMapper ddiMapper = new DDIMapper();
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        ddiMapper.mapDDI(ddiInstanceDocument, enoQuestionnaire);
        //
        DDIInProcessing ddiInProcessing = new DDIInProcessing();
        ddiInProcessing.applyProcessing(enoQuestionnaire);
        //
        EnoProcessing enoProcessing = new EnoProcessing(enoParameters);
        enoProcessing.applyProcessing(enoQuestionnaire);
        //
        return enoQuestionnaire;
    }

}
