package fr.insee.eno.ws.controller;

import fr.insee.eno.ws.controller.utils.ResponseUtils;
import fr.insee.eno.ws.dto.FileDto;
import fr.insee.eno.ws.exception.EnoControllerException;
import fr.insee.eno.ws.service.PoguesToDDIService;
import fr.insee.eno.ws.service.PoguesToLunaticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

import static fr.insee.eno.ws.controller.utils.ControllerUtils.addMultipartToBody;

@Tag(name = "Generation of DDI")
@Controller
@RequestMapping("/questionnaire")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class GenerationPoguesController {

    private final PoguesToDDIService poguesToDDIService;
    private final PoguesToLunaticService poguesToLunaticService;

    @Operation(
            summary = "[Eno Xml service] DDI Generation from Pogues xml questionnaire.",
            description = "**This endpoint uses the 'Xml' version of Eno.** " +
                    "Generation of a DDI from a Pogues questionnaire (in the xml format).")
    @PostMapping(value="poguesxml-2-ddi",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> generateDDIQuestionnaireFromXml(
            @RequestPart(value="in") MultipartFile poguesXmlFile) throws EnoControllerException, IOException {
        //
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        addMultipartToBody(multipartBodyBuilder, poguesXmlFile, "in");
        //
        return ResponseUtils.okFromFileDto(poguesToDDIService.transformFromXml(poguesXmlFile));
    }

    @Operation(
            summary = "[Eno Xml service] DDI Generation from Pogues xml questionnaire.",
            description = "**This endpoint uses the 'Xml' version of Eno.** " +
                    "Generation of a DDI from a Pogues questionnaire (in the json format).")
    @PostMapping(value="pogues-2-ddi",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> generateDDIQuestionnaire(
            @RequestPart(value="in") MultipartFile poguesJsonFile) throws IOException {
        //
        FileDto poguesXmlFileDto = poguesToDDIService.convertPoguesFileToXml(poguesJsonFile);
        //
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        addMultipartToBody(multipartBodyBuilder, poguesXmlFileDto, "in");
        //
        return ResponseUtils.okFromFileDto(poguesToDDIService.transform(poguesJsonFile));
    }

}
