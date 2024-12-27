package fr.insee.eno.core;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.common.EnoProcessing;
import fr.insee.eno.core.processing.in.DDIInProcessing;
import fr.insee.eno.core.serialize.DDIDeserializer;

import java.io.InputStream;

public class DDIToEno {

    /**
     * Transform given DDI input stream into a Eno questionnaire object using parameters given.
     * @param ddiInputStream Input stream of a DDI document.
     * @param enoParameters Eno parameters object.
     * @return Lunatic questionnaire object.
     * @throws DDIParsingException if the input stream given cannot be parsed to a DDI object.
     */
    public EnoQuestionnaire transform(InputStream ddiInputStream, EnoParameters enoParameters)
            throws DDIParsingException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIDeserializer.deserialize(ddiInputStream);
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
