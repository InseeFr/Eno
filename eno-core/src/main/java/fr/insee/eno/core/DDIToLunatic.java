package fr.insee.eno.core;

import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.output.LunaticSerializer;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.EnoProcessing;
import fr.insee.eno.core.processing.LunaticProcessing;
import fr.insee.lunatic.model.flat.Questionnaire;
import instance33.DDIInstanceDocument;

import java.io.IOException;
import java.io.InputStream;

public class DDIToLunatic {

    private DDIToLunatic() {}

    public static Questionnaire transform(InputStream ddiInputStream, EnoParameters enoParameters)
            throws IOException, DDIParsingException {
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

    public static String transformToJson(InputStream ddiInputStream, EnoParameters enoParameters)
            throws IOException, DDIParsingException, LunaticSerializationException {
        Questionnaire lunaticQuestionnaire = transform(ddiInputStream, enoParameters);
        return LunaticSerializer.serializeToJson(lunaticQuestionnaire);
    }

}
