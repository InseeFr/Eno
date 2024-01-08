package fr.insee.eno.ws.controller;

import fr.insee.eno.ws.PassThrough;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Mono;

@Tag(name = "Multi-module generation")
@Controller
@RequestMapping("/questionnaire")
@Slf4j
@SuppressWarnings("unused")
public class GenerationMultiModelController {

    private final PassThrough passThrough;

    public GenerationMultiModelController(PassThrough passThrough) {
        this.passThrough = passThrough;
    }

    @Operation(
            summary = "[Eno Xml service] Generation of questionnaire according to parameters.",
            description = "**This endpoint uses the 'Xml' version of Eno.** " +
                    "Generation of a questionnaire using a parameters `xml` file _(required)_, metadata `xml` file " +
                    "_(optional)_ and the specific treatment `xsl` file _(optional)_. This service contains a " +
                    "multi-model parameter to generate several questionnaires in a zip file in a single request.")
    @PostMapping(value = "in-2-out",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> generate(
            @RequestPart(value="in") Mono<FilePart> in,
            @RequestPart(value="params") Mono<FilePart> params,
            @RequestPart(value="metadata", required=false) Mono<FilePart> metadata,
            @RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
            @RequestPart(value="mapping", required=false) Mono<FilePart> mapping,
            @RequestParam(value="multi-model", required=false, defaultValue="false") boolean multiModel,
            ServerHttpRequest request, ServerHttpResponse response) {
        return passThrough.passePlatPost(request, response);
    }

}
