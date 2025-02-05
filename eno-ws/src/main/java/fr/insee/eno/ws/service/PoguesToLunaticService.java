package fr.insee.eno.ws.service;

import fr.insee.eno.core.PoguesToLunatic;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.ws.exception.PoguesToLunaticException;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@PropertySource("classpath:/version.properties")
public class PoguesToLunaticService extends LunaticGenerationService {

    @Override
    Questionnaire mainTransformation(InputStream poguesInputStream, EnoParameters enoParameters) throws Exception {
        return PoguesToLunatic.fromInputStream(poguesInputStream).transform(enoParameters);
    }

    @Override
    void handleException(Exception e) {
        throw new PoguesToLunaticException(e);
    }

}
