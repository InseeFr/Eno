package fr.insee.eno.core;

import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.exceptions.DDIParsingException;
import fr.insee.eno.core.exceptions.LunaticSerializationException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.output.LunaticSerializer;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.EnoProcessing;
import fr.insee.eno.core.processing.LunaticProcessing;
import fr.insee.lunatic.conversion.JSONSerializer;
import fr.insee.lunatic.model.flat.Questionnaire;
import instance33.DDIInstanceDocument;
import instance33.DDIInstanceType;
import org.apache.xmlbeans.XmlException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;

public class DDIToLunatic {

    public String transform(InputStream ddiInputStream, EnoParameters enoParameters)
            throws IOException, DDIParsingException, LunaticSerializationException {
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
        return LunaticSerializer.serializeToJson(lunaticQuestionnaire);
    }
}
