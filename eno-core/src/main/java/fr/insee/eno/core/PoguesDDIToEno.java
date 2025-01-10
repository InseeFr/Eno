package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.common.EnoProcessing;
import fr.insee.eno.core.processing.in.DDIInProcessing;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;

import java.io.InputStream;

/**
 * Temporary transformation that uses two inputs: the Pogues questionnaire and the DDI.
 * Some properties that does not belong in the DDI are read in the Pogues questionnaire.
 * Then the DDI is read and the DDI processing steps are applied (as a normal DDI to Eno transformation).
 */
public class PoguesDDIToEno {

    public EnoQuestionnaire transform(InputStream poguesInputStream, InputStream ddiInputStream,
                                      EnoParameters enoParameters) throws ParsingException {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(PoguesDeserializer.deserialize(poguesInputStream), enoQuestionnaire);
        new DDIMapper().mapDDI(DDIDeserializer.deserialize(ddiInputStream), enoQuestionnaire);
        new DDIInProcessing().applyProcessing(enoQuestionnaire);
        new EnoProcessing(enoParameters).applyProcessing(enoQuestionnaire);
        return enoQuestionnaire;
    }

}
