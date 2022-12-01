package fr.insee.eno.ws.controller;

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

@Tag(name = "Util")
@RestController
@RequestMapping("/v2/util")
public class UtilController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilController.class);


    @Operation(summary = "Generation of ddi33 questionnaire from ddi32 questionnaire.", description = "It generates a ddi in 3.3 version questionnaire from a a ddi in 3.2 version questionnaire.")
    @PostMapping(value = "ddi32-2-ddi33", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Flux<DataBuffer>>> generateDDI33Questionnaire(
            @RequestPart(value = "in", required = true) MultipartFile in) throws Exception {

        return null;
    }

    @Operation(summary = "Generation of VTL formula from Xpath formula", description = "It generates a VTL in 2.0 version from a Xpath in 1.1 version.")
    @PostMapping(value = "xpath-2-vtl")
    public Mono<ResponseEntity<Flux<String>>> generateVTLFormula(
            @RequestParam(value = "xpath", required = true) String xpath) throws Exception {

        return null;
    }

}
