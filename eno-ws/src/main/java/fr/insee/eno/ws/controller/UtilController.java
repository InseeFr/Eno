package fr.insee.eno.ws.controller;

import fr.insee.eno.core.utils.XpathToVtl;
import fr.insee.eno.ws.PassePlat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Util")
@RestController
@RequestMapping("/util")
@Slf4j
@SuppressWarnings("unused")
public class UtilController {

    private final PassePlat passePlat;

    public UtilController(PassePlat passePlat) {
        this.passePlat = passePlat;
    }

    @Operation(
            summary = "Generation of DDI 3.3 questionnaire from DDI 3.2 questionnaire.",
            description = "Generate a DDI in 3.3 version questionnaire from a a DDI in 3.2 version questionnaire.")
    @PostMapping(value = "ddi32-2-ddi33",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> generateDDI33Questionnaire(
            @RequestPart(value="in") Mono<FilePart> in,
            ServerHttpRequest request, ServerHttpResponse response) {
        return passePlat.passePlatPost(request, response);
    }

    @Operation(
            summary = "Generation of VTL expression from Xpath expression.",
            description = "Generate a VTL in 2.0 version from a Xpath in 1.1 version.")
    @PostMapping(value = "xpath-2-vtl")
    public Mono<ResponseEntity<String>> generateVTLFormula(
            @RequestParam(value="xpath") String xpath) {
        String result = XpathToVtl.parseToVTL(xpath);
        log.info("Xpath expression given parsed to VTL: {}", result);
        return Mono.just(ResponseEntity.ok().body(result));
    }

}
