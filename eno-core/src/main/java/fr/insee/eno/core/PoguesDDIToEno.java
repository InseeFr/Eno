package fr.insee.eno.core;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.common.EnoProcessing;
import fr.insee.eno.core.processing.in.DDIInProcessing;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.pogues.model.Questionnaire;

import java.io.InputStream;

/**
 * Temporary transformation that uses two inputs: the Pogues questionnaire and the DDI.
 * Some properties that does not belong in the DDI are read in the Pogues questionnaire.
 * Then the DDI is read and the DDI processing steps are applied (as a normal DDI to Eno transformation).
 */
public class PoguesDDIToEno implements InToEno {

    private final Questionnaire poguesQuestionnaire;
    private final DDIInstanceDocument ddiQuestionnaire;

    private PoguesDDIToEno(Questionnaire poguesQuestionnaire, DDIInstanceDocument ddiQuestionnaire) {
        this.poguesQuestionnaire = poguesQuestionnaire;
        this.ddiQuestionnaire = ddiQuestionnaire;
    }

    public static PoguesDDIToEno fromInputStreams(InputStream poguesInputStream, InputStream ddiInputStream) throws ParsingException {
        return new PoguesDDIToEno(
                PoguesDeserializer.deserialize(poguesInputStream),
                DDIDeserializer.deserialize(ddiInputStream));
    }

    public static PoguesDDIToEno fromObjects(Questionnaire poguesQuestionnaire, DDIInstanceDocument ddiQuestionnaire) {
        return new PoguesDDIToEno(poguesQuestionnaire, ddiQuestionnaire);
    }

    /**
     * @deprecated For this class, the old transform method is not implemented.
     */
    @Override
    @Deprecated(since = "3.33.0", forRemoval = true)
    public EnoQuestionnaire transform(InputStream inputStream, EnoParameters enoParameters) throws ParsingException {
        throw new UnsupportedOperationException("Use the other transform method.");
    }

    @Override
    public EnoQuestionnaire transform(EnoParameters enoParameters) {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        new DDIMapper().mapDDI(ddiQuestionnaire, enoQuestionnaire);
        new DDIInProcessing().applyProcessing(enoQuestionnaire);
        new EnoProcessing(enoParameters).applyProcessing(enoQuestionnaire);
        return enoQuestionnaire;
    }

}
