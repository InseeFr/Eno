package fr.insee.eno.ws.controller;

import fr.insee.eno.legacy.parameters.Context;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Simple Generation of questionnaire")
@RestController
@RequestMapping("/v2/questionnaire")
public class SimpleGenerationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleGenerationController.class);

    @Operation(
            summary = "Generation of fo questionnaire according to the context.",
            description = "It generates a fo questionnaire from a ddi questionnaire using the default fo parameters according to the study unit. "
                    + "See it using the end point : */parameter/{context}/default*"
    )
    @PostMapping(value = "{context}/fo", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Flux<DataBuffer>>> generateFOQuestionnaire(

            // Files
            @RequestPart(value = "in", required = true) MultipartFile in,
            @RequestPart(value = "specificTreatment", required = false) MultipartFile specificTreatment,

            @PathVariable Context context) throws Exception {

        return null;
    }


    @Operation(
            summary = "Generation of xforms questionnaire according to the context.",
            description = "It generates a xforms questionnaire from a ddi questionnaire using the default xforms parameters according to the study unit. "
                    + "See it using the end point : */parameter/{context}/default*"
    )
    @PostMapping(value = "{context}/xforms", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Flux<DataBuffer>>> generateXformsQuestionnaire(

            // Files
            @RequestPart(value = "in", required = true) MultipartFile in,
            @RequestPart(value = "specificTreatment", required = false) MultipartFile specificTreatment,

            @PathVariable Context context) throws Exception {


        return null;
    }


    @Operation(
            summary = "Generation of lunatic-xml questionnaire according  to the context.",
            description = "It generates a lunatic-xml questionnaire from a ddi questionnaire using the default js parameters according to the study unit. "
                    + "See it using the end point : */parameter/{context}/default*"
    )
    @PostMapping(value = "{context}/lunatic-xml", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Flux<DataBuffer>>> generateXMLLunaticQuestionnaire(

            // Files
            @RequestPart(value = "in", required = true) MultipartFile in,
            @RequestPart(value = "specificTreatment", required = false) MultipartFile specificTreatment,

            @PathVariable Context context) throws Exception {

        return null;
    }


    @Operation(
            summary = "Generation of pdf questionnaire according  to the context.",
            description = "It generates a lunatic-json-flat questionnaire from a ddi questionnaire using the default js parameters according to the study unit. "
                    + "See it using the end point : */parameter/{context}/default*"
    )
    @PostMapping(value = "{context}/lunatic-json-flat", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Flux<DataBuffer>>> generateJSONLunaticQuestionnaire(

            // Files
            @RequestPart(value = "in", required = true) MultipartFile in,
            @RequestPart(value = "specificTreatment", required = false) MultipartFile specificTreatment,

            @PathVariable Context context) throws Exception {

        return null;
    }


    @Operation(
            summary = "Generation of fodt questionnaire according  to the context.",
            description = "It generates a odt questionnaire from a ddi questionnaire using the default js parameters according to the study unit. "
                    + "See it using the end point : */parameter/{context}/default*"
    )
    @PostMapping(value = "{context}/fodt", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Flux<DataBuffer>>> generateFodtQuestionnaire(

            // Files
            @RequestPart(value = "in", required = true) MultipartFile in,

            @PathVariable Context context) throws Exception {
        return null;
    }


}