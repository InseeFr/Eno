package fr.insee.eno.ws.controller;

import fr.insee.eno.legacy.parameters.Context;
import fr.insee.eno.legacy.parameters.Mode;
import fr.insee.eno.ws.PassePlat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Simple Generation of questionnaire")
@RestController
@RequestMapping("/questionnaire")
public class SimpleGenerationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleGenerationController.class);

    @Autowired
    private PassePlat passePlat;

    @Operation(
            summary = "Generation of fo questionnaire according to the context.",
            description = "It generates a fo questionnaire from a ddi questionnaire using the default fo parameters according to the study unit. "
                    + "See it using the end point : */parameter/{context}/default*"
    )
    @PostMapping(value = "{context}/fo", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Flux<DataBuffer>>> generateFOQuestionnaire(
            @RequestPart(value = "in", required = true) MultipartFile in,
            @RequestPart(value = "specificTreatment", required = false) MultipartFile specificTreatment,
            @PathVariable Context context,
            ServerHttpRequest serverRequest, ServerHttpResponse serverHttpResponse) throws Exception {
        return null;
    }


    @Operation(
            summary = "Generation of xforms questionnaire according to the context.",
            description = "It generates a xforms questionnaire from a ddi questionnaire using the default xforms parameters according to the study unit. "
                    + "See it using the end point : */parameter/{context}/default*"
    )
    @PostMapping(value = "{context}/xforms", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Flux<DataBuffer>>> generateXformsQuestionnaire(
            @RequestPart(value = "in", required = true) MultipartFile in,
            @RequestPart(value = "specificTreatment", required = false) MultipartFile specificTreatment,
            @PathVariable Context context,
            ServerHttpRequest serverRequest, ServerHttpResponse serverHttpResponse) throws Exception {
        return null;
    }


    @Operation(
            summary = "Generation of lunatic-xml questionnaire according  to the context.",
            description = "It generates a lunatic-xml questionnaire from a ddi questionnaire using the default js parameters according to the study unit. "
                    + "See it using the end point : */parameter/{context}/default*"
    )
    @PostMapping(value = "{context}/lunatic-xml", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Flux<DataBuffer>>> generateXMLLunaticQuestionnaire(
            @RequestPart(value = "in", required = true) MultipartFile in,
            @RequestPart(value = "specificTreatment", required = false) MultipartFile specificTreatment,
            @PathVariable Context context,
            ServerHttpRequest serverRequest, ServerHttpResponse serverHttpResponse) throws Exception {
        return null;
    }


    @Operation(
            summary="Generation of Lunatic json flat questionnaire according to the context.",
            description="Generate a Lunatic json flat questionnaire from a ddi questionnaire using the default js parameters according to the study unit. "
                    + "See it using the end point : */parameter/{context}/default*"
                    + "The params *parsingXpathVTL* must be 'true' (default value) if controls language is pseudo-xpath."
    )
    @PostMapping(value = "{context}/lunatic-json/{mode}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> generateJSONLunaticQuestionnaire(
            @RequestPart(value = "in", required = true) Mono<FilePart> in,
            //@RequestPart(value = "specificTreatment", required = false) Mono<FilePart> specificTreatment,
            @PathVariable Context context,
            @PathVariable Mode mode,
            ServerHttpRequest serverRequest, ServerHttpResponse serverHttpResponse) throws Exception {
        return passePlat.passePlatPost(serverRequest, serverHttpResponse);
    }


    @Operation(
            summary = "Generation of fodt questionnaire according  to the context.",
            description = "It generates a odt questionnaire from a ddi questionnaire using the default js parameters according to the study unit. "
                    + "See it using the end point : */parameter/{context}/default*"
    )
    @PostMapping(value = "{context}/fodt", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Flux<DataBuffer>>> generateFodtQuestionnaire(
            @RequestPart(value = "in", required = true) MultipartFile in,
            @PathVariable Context context,
            ServerHttpRequest serverRequest, ServerHttpResponse serverHttpResponse) throws Exception {
        return null;
    }


}