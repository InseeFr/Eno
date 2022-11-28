package fr.insee.eno.ws.controller;

import fr.insee.eno.ws.service.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class V3ParameterController {

    public final static String PARAMETERS_V3_FILE_NAME = "v3-default-parameters.json";

    @Autowired
    ParameterService parameterService;

    @GetMapping(value = "v3/default-parameters", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
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
