package fr.insee.eno.ws.controller.utils;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.ws.service.DDIToLunaticService;
import fr.insee.eno.ws.service.ParameterService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.SequenceInputStream;

/** Class to factorize code in v3 controllers' methods. */
@Component
public class V3ControllerUtils {

    public static final String LUNATIC_JSON_FILE_NAME = "lunatic-form.json";
    public static final String LUNATIC_XML_FILE_NAME = "lunatic-form.xml";

    private final DDIToLunaticService ddiToLunaticService;
    private final ParameterService parameterService;

    public V3ControllerUtils(DDIToLunaticService ddiToLunaticService, ParameterService parameterService) {
        this.ddiToLunaticService = ddiToLunaticService;
        this.parameterService = parameterService;
    }

    // TODO: replace Mono<FilePart> parametersFile with EnoParameters argument (automatic deserialization)
    // TODO: implement API friendly endpoints that return json/xml instead of octet stream

    public Mono<EnoParameters> readParametersFile(Mono<FilePart> parametersFile) {
        return parametersFile.flatMap(filePart -> filePart.content()
                        .map(dataBuffer -> dataBuffer.asInputStream(true))
                        .reduce(SequenceInputStream::new))
                .flatMap(parameterService::parse);
    }

    public Mono<ResponseEntity<String>> ddiToLunaticJson(Mono<FilePart> ddiFile, Mono<FilePart> parametersFile) {
        return readParametersFile(parametersFile)
                .flatMap(enoParameters -> ddiToLunaticJson(ddiFile, enoParameters));
    }

    public Mono<ResponseEntity<String>> ddiToLunaticJson(Mono<FilePart> ddiFile, EnoParameters enoParameters) {
        return ddiFile.flatMap(filePart -> filePart.content()
                        .map(dataBuffer -> dataBuffer.asInputStream(true))
                        .reduce(SequenceInputStream::new))
                .flatMap(inputStream -> ddiToLunaticService.transformToJson(inputStream, enoParameters))
                .map(result -> ResponseEntity
                        .ok()
                        .cacheControl(CacheControl.noCache())
                        .headers(HeadersUtils.with(LUNATIC_JSON_FILE_NAME))
                        .body(result));
    }

    public Mono<ResponseEntity<String>> ddiToLunaticXml(Mono<FilePart> ddiFile, Mono<FilePart> parametersFile) {
        return readParametersFile(parametersFile)
                .flatMap(enoParameters -> ddiToLunaticXml(ddiFile, enoParameters));
    }

    public Mono<ResponseEntity<String>> ddiToLunaticXml(Mono<FilePart> ddiFile, EnoParameters enoParameters) {
        return ddiFile.flatMap(filePart -> filePart.content()
                        .map(dataBuffer -> dataBuffer.asInputStream(true))
                        .reduce(SequenceInputStream::new))
                .flatMap(inputStream -> ddiToLunaticService.transformToXml(inputStream, enoParameters))
                .map(result -> ResponseEntity
                        .ok()
                        .cacheControl(CacheControl.noCache())
                        .headers(HeadersUtils.with(LUNATIC_XML_FILE_NAME))
                        .body(result));
    }

}
