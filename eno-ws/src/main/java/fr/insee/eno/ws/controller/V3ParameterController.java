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

@Tag(name="Parameters (V3)")
@RestController()
@RequestMapping("/v3/parameter")
public class V3ParameterController {

    private final ParameterService parameterService;

    public V3ParameterController(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @Operation(
            summary = "Get V3 default parameters with context.",
            description = "Return default parameters file in function of context given, " +
                    "to be used in V3 endpoints that require a parameter file.")
    @GetMapping(value = "{context}/{outFormat}/default", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<String>> v3Parameters(
            @PathVariable EnoParameters.Context context,
            @PathVariable Format outFormat,
            @RequestParam(value = "Mode") EnoParameters.ModeParameter modeParameter) {
        //
        String parametersFileName = "eno-parameters-" + context + "-" + modeParameter + "-" + outFormat + ".json";
        //
        return parameterService.defaultParams(context, outFormat, modeParameter)
                .map(params -> ResponseEntity
                        .ok()
                        .cacheControl(CacheControl.noCache())
                        .headers(HeadersUtils.with(parametersFileName))
                        .body(params));
    }

}
