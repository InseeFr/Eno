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

@Tag(name = "Generation with custom mapping")
@Controller
@RequestMapping("/questionnaire")
@Slf4j
@SuppressWarnings("unused")
public class GenerationWithMappingController {

    private final PassThrough passThrough;

    public GenerationWithMappingController(PassThrough passThrough) {
        this.passThrough = passThrough;
    }

    @Operation(
            summary = "[Eno Xml service] Generation of questionnaire according to parameters.",
            description = "**This endpoint uses the 'Xml' version of Eno.** " +
                    "Generation of a questionnaire from the input file given, " +
                    "using a parameters file _(required)_, a metadata file _(optional)_, a specific treatment file " +
                    "_(optional)_ and a mapping file _(optional)_. " +
                    "If the multi-model option is set to true, the output questionnaire(s) are put in a zip file.")
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
