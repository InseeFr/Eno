package fr.insee.eno.ws.controller;

import fr.insee.eno.legacy.parameters.OutFormat;
import fr.insee.eno.ws.controller.utils.EnoXmlControllerUtils;
import fr.insee.eno.ws.exception.EnoControllerException;
import fr.insee.eno.ws.exception.PoguesToLunaticException;
import fr.insee.eno.ws.service.PoguesToLunaticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

import static fr.insee.eno.ws.controller.utils.EnoXmlControllerUtils.*;

@Tag(name = "Generation of DDI")
@Controller
@RequestMapping("/questionnaire")
@Slf4j
@SuppressWarnings("unused")
public class GenerationPoguesController {

    private final PoguesToLunaticService poguesToLunaticService;
    private final EnoXmlControllerUtils xmlControllerUtils;

    public GenerationPoguesController(
            PoguesToLunaticService poguesToLunaticService,
            EnoXmlControllerUtils xmlControllerUtils) {
        this.poguesToLunaticService = poguesToLunaticService;
        this.xmlControllerUtils = xmlControllerUtils;
    }

    @Operation(
            summary = "[Eno Xml service] DDI Generation from Pogues xml questionnaire.",
            description = "**This endpoint uses the 'Xml' version of Eno.** " +
                    "Generation of a DDI from a Pogues questionnaire (in the xml format).")
    @PostMapping(value="poguesxml-2-ddi",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> generateDDIQuestionnaireFromXml(
            @RequestPart(value="in") MultipartFile poguesXmlFile) throws EnoControllerException {
        //
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        addMultipartToBody(multipartBodyBuilder, poguesXmlFile, "in");
        //
        URI uri = xmlControllerUtils.newUriBuilder().path("questionnaire/poguesxml-2-ddi").build().toUri();
        String outFilename = questionnaireFilename(OutFormat.DDI, false);
        return xmlControllerUtils.sendPostRequest(uri, multipartBodyBuilder, outFilename);
    }

    @Operation(
            summary = "[Eno Xml service] DDI Generation from Pogues xml questionnaire.",
            description = "**This endpoint uses the 'Xml' version of Eno.** " +
                    "Generation of a DDI from a Pogues questionnaire (in the json format).")
    @PostMapping(value="pogues-2-ddi",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> generateDDIQuestionnaire(
            @RequestPart(value="in") MultipartFile poguesJsonFile) {
        // Convert json to xml
        String poguesXml;
        try {
            poguesXml = poguesToLunaticService.poguesJsonToXml(new String(poguesJsonFile.getBytes()));
        } catch (IOException e) {
            log.error("Pogues json to xml conversion failed.");
            throw new PoguesToLunaticException(e);
        }
        // Attach Pogues xml content in a multipart
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        addStringToMultipartBody(multipartBodyBuilder, poguesXml, "pogues.xml", "in");
        // Send request to the legacy pogues xml to ddi endpoint
        URI uri = xmlControllerUtils.newUriBuilder().path("questionnaire/poguesxml-2-ddi").build().toUri();
        String outFilename = questionnaireFilename(OutFormat.DDI, false);
        return xmlControllerUtils.sendPostRequest(uri, multipartBodyBuilder, outFilename);
    }

}
