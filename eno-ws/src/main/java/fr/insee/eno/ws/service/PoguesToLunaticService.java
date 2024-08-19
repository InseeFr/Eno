package fr.insee.eno.ws.service;

import fr.insee.eno.core.PoguesToLunatic;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.ws.exception.PoguesToLunaticException;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.pogues.conversion.JSONToXMLTranslator;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@PropertySource("classpath:/version.properties")
public class PoguesToLunaticService extends LunaticGenerationService {

    @Override
    Questionnaire mainTransformation(InputStream poguesInputStream, EnoParameters enoParameters) throws Exception {
        return PoguesToLunatic.transform(poguesInputStream, enoParameters);
    }

    @Override
    void handleException(Exception e) {
        throw new PoguesToLunaticException(e);
    }

    public String poguesJsonToXml(String poguesJson) {
        try {
            JSONToXMLTranslator jsonToXmlTranslator = new JSONToXMLTranslator();
            return jsonToXmlTranslator.translate(poguesJson);
        } catch (Exception e) {
            throw new PoguesToLunaticException(e);
        }
    }

}
