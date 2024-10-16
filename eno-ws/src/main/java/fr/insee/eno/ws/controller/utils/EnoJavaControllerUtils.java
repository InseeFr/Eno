package fr.insee.eno.ws.controller.utils;

import fr.insee.eno.core.exceptions.business.EnoParametersException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.ws.exception.EnoControllerException;
import fr.insee.eno.ws.service.LunaticGenerationService;
import fr.insee.eno.ws.service.ParameterService;
import fr.insee.eno.ws.service.SpecificTreatmentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Class to factorize code in Eno Java controllers' methods.
 */
@Component
public class EnoJavaControllerUtils {

    public static final String LUNATIC_JSON_FILE_NAME = "lunatic-form.json";

    private final ParameterService parameterService;
    private final SpecificTreatmentsService specificTreatmentsService;

    public EnoJavaControllerUtils(ParameterService parameterService,
                                  SpecificTreatmentsService specificTreatmentsService) {
        this.parameterService = parameterService;
        this.specificTreatmentsService = specificTreatmentsService;
    }

    public EnoParameters readEnoJavaParametersFile(MultipartFile parametersFile)
            throws EnoParametersException, IOException {
        if (parametersFile == null || parametersFile.isEmpty())
            throw new EnoParametersException("Parameters file is missing.");
        String fileName = parametersFile.getOriginalFilename();
        if (fileName == null)
            throw new EnoParametersException("Parameters file name is null.");
        if (! fileName.endsWith(".json"))
            throw new EnoParametersException("Eno Java parameters file name must end with '.json'.");
        return parameterService.parse(new ByteArrayInputStream(parametersFile.getBytes()));
    }

    private LunaticPostProcessing createLunaticPostProcessing(MultipartFile specificTreatmentsFile)
            throws EnoControllerException, IOException {
        if (specificTreatmentsFile == null || specificTreatmentsFile.isEmpty())
            return null;
        String fileName = specificTreatmentsFile.getOriginalFilename();
        if (fileName == null)
            throw new EnoControllerException("Specific treatments file name is null.");
        if (! fileName.endsWith(".json"))
            throw new EnoControllerException("Eno Java specific treatments file name must end with '.json'.");
        return specificTreatmentsService.generateFrom(new ByteArrayInputStream(specificTreatmentsFile.getBytes()));
    }

    public ResponseEntity<String> transformToLunatic(MultipartFile inFile, MultipartFile parametersFile,
                                                     MultipartFile specificTreatmentsFile,
                                                     LunaticGenerationService generationService)
            throws EnoParametersException, IOException, EnoControllerException {
        EnoParameters enoParameters = readEnoJavaParametersFile(parametersFile);
        LunaticPostProcessing lunaticPostProcessing = createLunaticPostProcessing(specificTreatmentsFile);
        return transformToLunatic(inFile.getBytes(), enoParameters, lunaticPostProcessing, generationService);
    }

    public ResponseEntity<String> transformToLunatic(MultipartFile inFile, EnoParameters enoParameters,
                                                     MultipartFile specificTreatmentsFile,
                                                     LunaticGenerationService generationService)
            throws IOException, EnoControllerException {
        LunaticPostProcessing lunaticPostProcessing = createLunaticPostProcessing(specificTreatmentsFile);
        return transformToLunatic(inFile.getBytes(), enoParameters, lunaticPostProcessing, generationService);
    }

    public ResponseEntity<String> transformToLunatic(String inputContent, EnoParameters enoParameters,
                                                     MultipartFile specificTreatmentsFile,
                                                     LunaticGenerationService generationService)
            throws IOException, EnoControllerException {
        LunaticPostProcessing lunaticPostProcessing = createLunaticPostProcessing(specificTreatmentsFile);
        return transformToLunatic(inputContent.getBytes(), enoParameters, lunaticPostProcessing, generationService);
    }

    private ResponseEntity<String> transformToLunatic(byte[] inputBytes, EnoParameters enoParameters,
                                                      LunaticPostProcessing lunaticPostProcessing,
                                                      LunaticGenerationService generationService)
            throws EnoControllerException {
        if (inputBytes == null || inputBytes.length == 0)
            throw new EnoControllerException("Input file given is empty.");
        String lunaticJson = generationService.transform(
                new ByteArrayInputStream(inputBytes), enoParameters, lunaticPostProcessing);
        return ResponseEntity.ok()
                .headers(HeadersUtils.with(LUNATIC_JSON_FILE_NAME))
                .body(lunaticJson);
    }

}
