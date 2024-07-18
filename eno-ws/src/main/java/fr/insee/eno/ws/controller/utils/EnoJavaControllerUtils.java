package fr.insee.eno.ws.controller.utils;

import fr.insee.eno.core.exceptions.business.EnoParametersException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.ws.exception.DDIToLunaticException;
import fr.insee.eno.ws.exception.EnoControllerException;
import fr.insee.eno.ws.service.DDIToLunaticService;
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

    private final DDIToLunaticService ddiToLunaticService;
    private final ParameterService parameterService;
    private final SpecificTreatmentsService specificTreatmentsService;

    public EnoJavaControllerUtils(DDIToLunaticService ddiToLunaticService,
                                  ParameterService parameterService,
                                  SpecificTreatmentsService specificTreatmentsService) {
        this.ddiToLunaticService = ddiToLunaticService;
        this.parameterService = parameterService;
        this.specificTreatmentsService = specificTreatmentsService;
    }

    private EnoParameters readEnoJavaParametersFile(MultipartFile parametersFile)
            throws EnoParametersException, IOException {
        if (parametersFile == null || parametersFile.isEmpty())
            throw new EnoParametersException("Parameters file is missing.");
        String fileName = parametersFile.getOriginalFilename();
        if (fileName == null)
            throw new EnoParametersException("Parameters file names is null.");
        if (! fileName.endsWith(".json"))
            throw new EnoParametersException("Eno Java parameters file name must end with '.json'.");
        return parameterService.parse(new ByteArrayInputStream(parametersFile.getBytes()));
    }

    private LunaticPostProcessing createLunaticPostProcessing(MultipartFile specificTreatmentsFile)
            throws IOException {
        if (specificTreatmentsFile == null || specificTreatmentsFile.isEmpty())
            return null;
        return specificTreatmentsService.generateFrom(new ByteArrayInputStream(specificTreatmentsFile.getBytes()));
    }

    public ResponseEntity<String> ddiToLunaticJson(MultipartFile ddiFile, MultipartFile parametersFile,
                                                   MultipartFile specificTreatmentsFile)
            throws EnoParametersException, IOException, EnoControllerException, DDIToLunaticException {
        EnoParameters enoParameters = readEnoJavaParametersFile(parametersFile);
        LunaticPostProcessing lunaticPostProcessing = createLunaticPostProcessing(specificTreatmentsFile);
        return ddiToLunaticJson(ddiFile, enoParameters, lunaticPostProcessing);
    }

    public ResponseEntity<String> ddiToLunaticJson(MultipartFile ddiFile, EnoParameters enoParameters,
                                                   MultipartFile specificTreatmentsFile)
            throws IOException, EnoControllerException, DDIToLunaticException {
        LunaticPostProcessing lunaticPostProcessing = createLunaticPostProcessing(specificTreatmentsFile);
        return ddiToLunaticJson(ddiFile, enoParameters, lunaticPostProcessing);
    }

    private ResponseEntity<String> ddiToLunaticJson(MultipartFile ddiFile, EnoParameters enoParameters,
                                                    LunaticPostProcessing lunaticPostProcessing)
            throws EnoControllerException, IOException, DDIToLunaticException {
        if (ddiFile.isEmpty())
            throw new EnoControllerException("DDI file is missing.");
        String lunaticJson = ddiToLunaticService.transformToJson(
                new ByteArrayInputStream(ddiFile.getBytes()), enoParameters, lunaticPostProcessing);
        return ResponseEntity.ok()
                .headers(HeadersUtils.with(LUNATIC_JSON_FILE_NAME))
                .body(lunaticJson);
    }

}
