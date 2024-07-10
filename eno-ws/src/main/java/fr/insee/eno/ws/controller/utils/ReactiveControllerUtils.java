package fr.insee.eno.ws.controller.utils;

import fr.insee.eno.core.exceptions.business.EnoParametersException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.ws.service.DDIToLunaticService;
import fr.insee.eno.ws.service.ParameterService;
import fr.insee.eno.ws.service.SpecificTreatmentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.io.SequenceInputStream;

/** Class to factorize code in Eno Java controllers' methods. */
@Component
@Slf4j
public class ReactiveControllerUtils {

    public static final String LUNATIC_JSON_FILE_NAME = "lunatic-form.json";

    private final DDIToLunaticService ddiToLunaticService;
    private final ParameterService parameterService;
    private final SpecificTreatmentsService specificTreatmentsService;

    public ReactiveControllerUtils(DDIToLunaticService ddiToLunaticService,
                                   ParameterService parameterService,
                                   SpecificTreatmentsService specificTreatmentsService) {
        this.ddiToLunaticService = ddiToLunaticService;
        this.parameterService = parameterService;
        this.specificTreatmentsService = specificTreatmentsService;
    }

    // NB: code is quite well factored here, notice that most methods are private

    private Mono<InputStream> filePartToInputStream(FilePart filePart) {
        return filePart.content()
                .map(dataBuffer -> dataBuffer.asInputStream(true))
                .reduce(SequenceInputStream::new);
    }

    private Mono<EnoParameters> readEnoJavaParametersFile(Mono<FilePart> parametersFile) {
        return parametersFile
                .flatMap(this::validateEnoJavaParametersFileName)
                .flatMap(this::filePartToInputStream)
                .flatMap(parameterService::parse);
    }

    private Mono<FilePart> validateEnoJavaParametersFileName(FilePart filePart) {
        if (! filePart.filename().endsWith(".json"))
            return Mono.error(new EnoParametersException("Eno Java parameters file name must end with '.json'."));
        return Mono.just(filePart);
    }

    private Mono<LunaticPostProcessing> createLunaticPostProcessing(Mono<Part> specificTreatment, EnoParameters enoParameters) {
        return specificTreatment
                .filter(FilePart.class::isInstance)
                .map(FilePart.class::cast)
                .flatMap(this::filePartToInputStream)
                .flatMap(specificTreatmentStream ->
                        specificTreatmentsService.generateFrom(specificTreatmentStream, enoParameters))
                .switchIfEmpty(Mono.just(new LunaticPostProcessing()));
        /*
         * This workaround (next filter) is used to make swagger works when empty value is checked for this input file on the endpoint
         * - there is no way to disallow empty checkbox value at this moment on swagger (though openAPI support configuring this)
         * - when empty value, spring boot considers the input as a DefaultFormField and not a file part, causing exceptions
         * if trying to cast to file part :-/
         */
    }

    public Mono<ResponseEntity<String>> ddiToLunaticJson(Mono<FilePart> ddiFile, Mono<FilePart> parametersFile,
                                                         Mono<Part> specificTreatmentsFile) {
        Mono<EnoParameters> parametersMono = readEnoJavaParametersFile(parametersFile);
        Mono<LunaticPostProcessing> postProcessingMono = parametersMono.flatMap(enoParameters ->
                createLunaticPostProcessing(specificTreatmentsFile, enoParameters));
        return Mono.zip(parametersMono, postProcessingMono).flatMap(tuple ->
                ddiToLunaticJson(ddiFile, tuple.getT1(), tuple.getT2()));
    }

    public Mono<ResponseEntity<String>> ddiToLunaticJson(Mono<FilePart> ddiFile, EnoParameters enoParameters,
                                                         Mono<Part> specificTreatmentsFile) {
        return createLunaticPostProcessing(specificTreatmentsFile, enoParameters).flatMap(lunaticPostProcessing ->
                ddiToLunaticJson(ddiFile, enoParameters, lunaticPostProcessing));
    }

    private Mono<ResponseEntity<String>> ddiToLunaticJson(Mono<FilePart> ddiFile, EnoParameters enoParameters,
                                                         LunaticPostProcessing lunaticPostProcessing) {
        return ddiFile
                .flatMap(this::filePartToInputStream)
                .flatMap(inputStream -> ddiToLunaticService.transformToJson(inputStream, enoParameters, lunaticPostProcessing))
                .map(result -> ResponseEntity
                        .ok()
                        .cacheControl(CacheControl.noCache())
                        .headers(HeadersUtils.with(LUNATIC_JSON_FILE_NAME))
                        .body(result));
    }

}
