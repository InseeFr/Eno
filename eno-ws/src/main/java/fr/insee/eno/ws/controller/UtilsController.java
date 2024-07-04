package fr.insee.eno.ws.controller;

import fr.insee.eno.core.utils.XpathToVtl;
import fr.insee.eno.ws.PassThrough;
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

@Tag(name = "Utils")
@RestController
@RequestMapping("/utils")
@Slf4j
@SuppressWarnings("unused")
public class UtilsController {

    private final PassThrough passThrough;

    public UtilsController(PassThrough passThrough) {
        this.passThrough = passThrough;
    }

    @Operation(
            summary = "Generation of DDI 3.3 from DDI 3.2.",
            description = "Generation of a DDI in 3.3 version from the given DDI in 3.2 version.")
    @PostMapping(value = "ddi32-2-ddi33",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> convertDDI32ToDDI33(
            @RequestPart(value="in") Mono<FilePart> in,
            ServerHttpRequest request, ServerHttpResponse response) {
        return passThrough.passePlatPost(request, response);
    }

    /**
     * Converts XPath expression to VTL.
     * @param xpath A XPath expression.
     * @return The XPath expression converted to VTL in a reactive response entity.
     * @deprecated The usage of XPath in questionnaires is deprecated.
     */
    @Operation(
            summary = "Conversion of Xpath expression to VTL expression.",
            description = "Converts the given Xpath 1.1 expression to a VTL 2.0 expression. " +
                    "_Note: The usage of XPath in questionnaires is now deprecated._")
    @PostMapping(value = "xpath-2-vtl")
    @Deprecated(since = "3.18.1")
    public Mono<ResponseEntity<String>> convertXpathToVTL(
            @RequestParam(value="xpath") String xpath) {
        String result = XpathToVtl.parseToVTL(xpath);
        log.info("Xpath expression given parsed to VTL: {}", result);
        return Mono.just(ResponseEntity.ok().body(result));
    }

}
