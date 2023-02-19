package fr.insee.eno.ws.controller;

import fr.insee.eno.ws.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name="Parameters (V3)")
@RestController()
@RequestMapping("/v3/parameter")
public class V3ParameterController {

    public static final String PARAMETERS_V3_FILE_NAME = "default-parameters-v3.json";

    private final ParameterService parameterService;

    public V3ParameterController(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @Operation(
            summary="Get V3 default parameters.",
            description="Return default parameters file, to be used in V3 endpoints that require a parameter file.")
    @GetMapping(value = "default", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<String>> v3DefaultParameters() {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+PARAMETERS_V3_FILE_NAME);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return parameterService.defaultParams()
                .map(params -> ResponseEntity
                        .ok()
                        .cacheControl(CacheControl.noCache())
                        .headers(headers)
                        .body(params));
    }

}
