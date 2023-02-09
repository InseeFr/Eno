package fr.insee.eno.ws.controller;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.ws.service.DDIToLunaticService;
import fr.insee.eno.ws.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.SequenceInputStream;

@Tag(name = "V3 Transformations", description = "Temporary endpoints that will be recast in existing endpoints.")
@RestController
@RequestMapping("/v3/questionnaire")
public class DDIToLunaticController {

    // TODO: integrate the V3 services into the existing controllers

    public static final String LUNATIC_OUT_FILE_NAME = "lunatic-form.json";

    private final DDIToLunaticService ddiToLunaticService;
    private final ParameterService parameterService;

    public DDIToLunaticController(DDIToLunaticService ddiToLunaticService, ParameterService parameterService) {
        this.ddiToLunaticService = ddiToLunaticService;
        this.parameterService = parameterService;
    }

    @Operation(
            summary = "Generation of Lunatic json questionnaire (default parameters).",
            description = "**This endpoint uses Eno v3**. " +
                    "Convert DDI questionnaire to a Lunatic json questionnaire with default parameters. " +
                    "See `/v3/parameters/default` endpoint to see these.")
    @PostMapping(value = "ddi-to-lunatic/default-params",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> ddiToLunatic(
            @RequestPart("ddiFile") Mono<FilePart> ddiFile) {
        return transformDDI(ddiFile, new EnoParameters());
    }

    @Operation(
            summary = "Generation of Lunatic json questionnaire with parameters.",
            description = "**This endpoint uses Eno v3**. " +
                    "Convert DDI questionnaire to a Lunatic json questionnaire with parameters. " +
                    "See `/v3/parameters/default` endpoint to have a sample file.")
    @PostMapping(value = "ddi-to-lunatic",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> ddiToLunatic(
            @RequestPart("ddiFile") Mono<FilePart> ddiFile,
            @RequestPart("parameterFile") Mono<FilePart> parametersFile) {
        return parametersFile.flatMap(filePart -> filePart.content()
                            .map(dataBuffer -> dataBuffer.asInputStream(true))
                            .reduce(SequenceInputStream::new))
                    .flatMap(parameterService::parse)
                    .flatMap(enoParameters -> transformDDI(ddiFile, enoParameters));
    }

    // TODO: replace Mono<FilePart> parametersFile with EnoParameters argument
    // TODO: implement API friendly endpoints that return json/xml instead of octet stream

    private Mono<ResponseEntity<String>> transformDDI(Mono<FilePart> ddiFile, EnoParameters enoParameters) {
        //
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+LUNATIC_OUT_FILE_NAME);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        //
        return ddiFile.flatMap(filePart -> filePart.content()
                        .map(dataBuffer -> dataBuffer.asInputStream(true))
                        .reduce(SequenceInputStream::new))
                .flatMap(inputStream -> ddiToLunaticService.transform(inputStream, enoParameters))
                .map(result -> ResponseEntity
                        .ok()
                        .cacheControl(CacheControl.noCache())
                        .headers(headers)
                        .body(result));
    }

}
