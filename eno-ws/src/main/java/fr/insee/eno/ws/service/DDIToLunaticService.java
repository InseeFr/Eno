package fr.insee.eno.ws.service;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.ws.exception.DDIToLunaticException;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class DDIToLunaticService extends LunaticGenerationService {

    @Override
    Questionnaire mainTransformation(InputStream ddiInputStream, EnoParameters enoParameters) throws Exception {
        return DDIToLunatic.fromInputStream(ddiInputStream).transform(enoParameters);
    }

    @Override
    void handleException(Exception e) {
        throw new DDIToLunaticException(e);
    }

}
