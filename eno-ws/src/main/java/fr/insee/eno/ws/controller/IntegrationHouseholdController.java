package fr.insee.eno.ws.controller;

import fr.insee.eno.legacy.parameters.Mode;
import fr.insee.eno.ws.PassePlat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Mono;

@Tag(name = "Integration of questionnaire")
@Controller
@RequestMapping("/integration-business")
@Slf4j
@SuppressWarnings("unused")
public class IntegrationHouseholdController {

    private final PassePlat passePlat;

    public IntegrationHouseholdController(PassePlat passePlat) {
        this.passePlat = passePlat;
    }

    @Operation(
            summary = "Integration of questionnaire according to params, metadata and specificTreatment.",
            description= "Generate a questionnaire for integration with default pipeline: using the " +
                    "parameters file (required), metadata file (required) and the specificTreatment file (optional). " +
                    "To use it, you have to upload all necessary files.")
    @PostMapping(value = "ddi-2-lunatic-json/{mode}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> generateLunaticJson(
            @PathVariable Mode mode,
            @RequestPart(value="in", required=true) Mono<FilePart> in,
            @RequestPart(value="params", required=true) Mono<FilePart> params,
            @RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
            ServerHttpRequest request, ServerHttpResponse response) {
        return passePlat.passePlatPost(request, response);
    }

}
