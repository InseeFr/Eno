package fr.insee.eno.ws.controller;

import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.ws.controller.utils.HeadersUtils;
import fr.insee.eno.ws.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name="Parameters (Eno Java)")
@RestController()
@RequestMapping("/parameters/java")
@SuppressWarnings("unused")
public class ParametersJavaController {

    /** Enum to be used in parameters of the web-service,
     * since the core Format enum doesn't distinguish in/out. */
    public enum OutFormat {LUNATIC} // For now only the Lunatic out format is supported by Eno Java

    public static Format toCoreFormat(OutFormat outFormat) {
        return switch (outFormat) {
            case LUNATIC -> Format.LUNATIC;
        };
    }

    private final ParameterService parameterService;

    public ParametersJavaController(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @Operation(
            summary = "Get parameters file for Eno Java services.",
            description = "Returns a `json` parameters file with standard values, in function of context and mode, " +
                    "for the concerned out format, to be used in _Eno Java_ services that require a parameters file.")
    @GetMapping(value = "{context}/{outFormat}/{mode}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<String>> getJavaParameters(
            @PathVariable EnoParameters.Context context,
            @PathVariable OutFormat outFormat,
            @PathVariable(name = "mode") EnoParameters.ModeParameter modeParameter) {
        //
        String parametersFileName = "eno-parameters-" + context + "-" + modeParameter + "-" + outFormat + ".json";
        //
        return parameterService.defaultParams(context, toCoreFormat(outFormat), modeParameter)
                .map(params -> ResponseEntity
                        .ok()
                        .cacheControl(CacheControl.noCache())
                        .headers(HeadersUtils.with(parametersFileName))
                        .body(params));
    }

}
