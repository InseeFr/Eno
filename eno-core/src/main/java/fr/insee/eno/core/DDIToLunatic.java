package fr.insee.eno.core;

import fr.insee.eno.core.converter.JsonLunaticConverter;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.EnoProcessing;
import fr.insee.eno.core.processing.LunaticProcessing;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.lunatic.model.flat.Questionnaire;
import instance33.DDIInstanceDocument;

import java.io.InputStream;

public class DDIToLunatic {

    private DDIToLunatic() {}

    /**
     * Transform given DDI input stream into a Lunatic questionnaire object using parameters given.
     * @param ddiInputStream Input stream of a DDI document.
     * @param enoParameters Eno parameters object.
     * @return Lunatic questionnaire object.
     * @throws DDIParsingException if the input stream given cannot be parsed to a DDI object.
     */
    public static Questionnaire transform(InputStream ddiInputStream, EnoParameters enoParameters)
            throws DDIParsingException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(ddiInputStream);
        //
        DDIMapper ddiMapper = new DDIMapper();
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        ddiMapper.mapDDI(ddiInstanceDocument, enoQuestionnaire);
        //
        EnoProcessing enoProcessing = new EnoProcessing(enoParameters);
        enoProcessing.applyProcessing(enoQuestionnaire, Format.DDI);
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        //
        LunaticProcessing lunaticProcessing = new LunaticProcessing(enoParameters);
        lunaticProcessing.applyProcessing(lunaticQuestionnaire, enoQuestionnaire);
        //
        return lunaticQuestionnaire;
    }

    /**
     * Transform given DDI input stream into a Lunatic questionnaire object with default parameters.
     * @param ddiInputStream Input stream of a DDI document.
     * @return Lunatic questionnaire object.
     * @throws DDIParsingException if the input stream given cannot be parsed to a DDI object.
     */
    public static Questionnaire transform(InputStream ddiInputStream) throws DDIParsingException {
        return transform(ddiInputStream, new EnoParameters());
    }

    /**
     * Transform given DDI input stream into a Lunatic questionnaire as a json string, using parameters given.
     * @param ddiInputStream Input stream of a DDI document.
     * @param enoParameters Eno parameters object.
     * @return Lunatic questionnaire serialized in a json string.
     * @throws DDIParsingException if the input stream given cannot be parsed to a DDI object.
     */
    public static String transformToJson(InputStream ddiInputStream, EnoParameters enoParameters)
            throws DDIParsingException, LunaticSerializationException {
        Questionnaire lunaticQuestionnaire = transform(ddiInputStream, enoParameters);

        // Handle missing/resizing (sic) as lunatic model can't support this at this time, really ugly :\
        return JsonLunaticConverter.convert(lunaticQuestionnaire);
    }

    /**
     * Transform given DDI input stream into a Lunatic questionnaire as a json string, using parameters given.
     * @param ddiInputStream Input stream of a DDI document.
     * @param enoParameters Eno parameters object.
     * @return Lunatic questionnaire serialized in a json string.
     * @throws DDIParsingException if the input stream given cannot be parsed to a DDI object.
     */
    public static String transformToJson(InputStream ddiInputStream, EnoParameters enoParameters, OutProcessingInterface<Questionnaire> lunaticPostProcessings)
            throws DDIParsingException, LunaticSerializationException {
        Questionnaire lunaticQuestionnaire = transform(ddiInputStream, enoParameters);
        lunaticPostProcessings.apply(lunaticQuestionnaire);

        // Handle missing/resizing (sic) as lunatic model can't support this at this time, really ugly :\
        return JsonLunaticConverter.convert(lunaticQuestionnaire);
    }
}
