package fr.insee.eno.ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.eno.core.exceptions.business.EnoParametersException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.ws.controller.ParametersJavaController.OutFormat;
import fr.insee.eno.ws.dto.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

/**
 * Service that manages both Eno Java and Eno Xml legacy parameters.
 * */
@Service
@RequiredArgsConstructor
public class ParameterService {

    public static Format toCoreFormat(OutFormat outFormat) {
        return switch (outFormat) {
            case LUNATIC -> Format.LUNATIC;
        };
    }

    private final EnoXmlClient enoXmlClient;

    /**
     * Throws an exception if the name of the given file is not a valid parameters file name.
     * For legacy parameters, see the Eno Xml service.
     * @param parametersFile An Eno parameters file (json).
     * @throws EnoParametersException if the file name is invalid.
     */
    private void validateParametersFile(MultipartFile parametersFile) throws EnoParametersException {
        String fileName = parametersFile.getOriginalFilename();
        if (fileName == null)
            throw new EnoParametersException("Parameters file name is null.");
        if (! fileName.endsWith(".json"))
            throw new EnoParametersException("Eno Java parameters file name must end with '.json'.");
    }

    public EnoParameters parse(MultipartFile parametersFile) throws EnoParametersException, IOException {
        validateParametersFile(parametersFile);
        return EnoParameters.parse(parametersFile.getInputStream());
    }

    public FileDto defaultParameters(Context context, ModeParameter modeParameter, OutFormat outFormat) throws JsonProcessingException {
        EnoParameters enoParameters = EnoParameters.of(context, modeParameter, toCoreFormat(outFormat));
        return FileDto.builder()
                .name(parametersFileName(context, modeParameter, outFormat))
                .content(EnoParameters.serialize(enoParameters).getBytes())
                .build();
    }

    public FileDto getLegacyParameters(
            Context context, ModeParameter modeParameter, fr.insee.eno.ws.legacy.parameters.OutFormat outFormat) {
        URI uri = enoXmlClient.newUriBuilder()
                .path("parameters/xml/{context}/{outFormat}")
                .queryParam("Mode", modeParameter)
                .build(context, outFormat);
        FileDto result = enoXmlClient.sendGetRequest(uri);
        result.setName(enoXmlParametersFilename(context, modeParameter, outFormat));
        return result;
    }

    public FileDto getAllLegacyParameters() {
        URI uri = enoXmlClient.newUriBuilder().path("parameters/xml/all").build().toUri();
        FileDto result = enoXmlClient.sendGetRequest(uri);
        result.setName("eno-parameters-ALL.xml");
        return result;
    }

    private static String parametersFileName(
            Context context, ModeParameter modeParameter, OutFormat outFormat) {
        return "eno-parameters-" + context + "-" + modeParameter + "-" + outFormat + ".json";
    }

    private static String enoXmlParametersFilename(
            Context context, ModeParameter mode, fr.insee.eno.ws.legacy.parameters.OutFormat outFormat) {
        String contextSuffix = "-" + context;
        String modeSuffix = mode != null ? "-" + mode : "";
        String outFormatSuffix = "-" + outFormat;
        return "eno-parameters" + contextSuffix + modeSuffix + outFormatSuffix + ".xml";
    }

}
