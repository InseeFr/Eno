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
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Mono;

@Tag(name = "Generation from Pogues")
@Controller
@RequestMapping("/questionnaire")
@Slf4j
@SuppressWarnings("unused")
public class GenerationPoguesController {

    private final PassThrough passThrough;

    public GenerationPoguesController(PassThrough passThrough) {
        this.passThrough = passThrough;
    }

    @Operation(
            summary = "[Eno Xml service] DDI Generation from Pogues xml questionnaire.",
            description = "**This endpoint uses the 'Xml' version of Eno.** " +
                    "Generation of a DDI from a Pogues questionnaire (in the xml format).")
    @PostMapping(value="poguesxml-2-ddi",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> generateDDIQuestionnaire(
            @RequestPart(value="in") Mono<FilePart> in,
            ServerHttpRequest request, ServerHttpResponse response) {
        return passThrough.passePlatPost(request, response);
    }

}
