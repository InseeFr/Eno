package fr.insee.eno.ws.controller;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.ws.controller.utils.V3ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "V3 Transformations", description = "Temporary endpoints that will be recast in existing endpoints.")
@RestController
@RequestMapping("/v3/questionnaire")
public class DDIToLunaticController {

    private final V3ControllerUtils controllerUtils;
    public DDIToLunaticController(V3ControllerUtils controllerUtils) {
        this.controllerUtils = controllerUtils;
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
        return controllerUtils.ddiToLunaticJson(ddiFile,
                EnoParameters.of(Context.DEFAULT, Format.LUNATIC, ModeParameter.PROCESS));
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
        return controllerUtils.ddiToLunaticJson(ddiFile, parametersFile);
    }

}
