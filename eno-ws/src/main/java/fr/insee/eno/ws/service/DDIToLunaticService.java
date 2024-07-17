package fr.insee.eno.ws.service;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.serialize.LunaticSerializer;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.ws.exception.DDIToLunaticException;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@PropertySource("classpath:/version.properties")
public class DDIToLunaticService {

    @Value("${version.eno}")
    String enoVersion;
    @Value("${version.lunatic.model}")
    String lunaticModelVersion;

    public String transformToJson(InputStream ddiInputStream, EnoParameters enoParameters, LunaticPostProcessing lunaticPostProcessings)
            throws DDIToLunaticException {
        try {
            Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(ddiInputStream, enoParameters);
            lunaticQuestionnaire.setEnoCoreVersion(enoVersion);
            lunaticQuestionnaire.setLunaticModelVersion(lunaticModelVersion);
            lunaticPostProcessings.apply(lunaticQuestionnaire);
            return LunaticSerializer.serializeToJson(lunaticQuestionnaire);
        } catch (Exception e) {
            throw new DDIToLunaticException(e);
        }
    }

    public String transformToJson(InputStream ddiInputStream, EnoParameters enoParameters)
            throws DDIToLunaticException {
        try {
            Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(ddiInputStream, enoParameters);
            lunaticQuestionnaire.setEnoCoreVersion(enoVersion);
            lunaticQuestionnaire.setLunaticModelVersion(lunaticModelVersion);
            return LunaticSerializer.serializeToJson(lunaticQuestionnaire);
        } catch (Exception e) {
            throw new DDIToLunaticException(e);
        }
    }

}
