package fr.insee.eno.ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.eno.core.exceptions.business.EnoParametersException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ParameterService {

    public EnoParameters parse(InputStream parametersInputStream) throws EnoParametersException, IOException {
        return EnoParameters.parse(parametersInputStream);
    }

    public String defaultParams(EnoParameters.Context context, Format format, ModeParameter modeParameter) throws JsonProcessingException {
        return EnoParameters.serialize(EnoParameters.of(context, modeParameter, format));
    }

}
