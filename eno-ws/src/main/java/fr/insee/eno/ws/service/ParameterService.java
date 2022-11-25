package fr.insee.eno.ws.service;

import fr.insee.eno.core.parameter.EnoParameters;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ParameterService {

    public EnoParameters parse(InputStream parametersInputStream) throws IOException {
        return EnoParameters.parse(parametersInputStream);
    }
}
