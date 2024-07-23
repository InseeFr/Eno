package fr.insee.eno.ws.controller;

import fr.insee.eno.ws.controller.utils.EnoXmlControllerUtils;
import fr.insee.eno.ws.exception.EnoControllerException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static fr.insee.eno.ws.controller.utils.EnoXmlControllerUtils.addMultipartToBody;

@Tag(name = "Generation with custom mapping")
@Controller
@RequestMapping("/questionnaire")
@Slf4j
@SuppressWarnings("unused")
public class GenerationWithMappingController {

    private final EnoXmlControllerUtils xmlControllerUtils;

    public GenerationWithMappingController(EnoXmlControllerUtils xmlControllerUtils) {
        this.xmlControllerUtils = xmlControllerUtils;
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
    public ResponseEntity<String> generate(
            @RequestPart(value="in") MultipartFile in,
            @RequestPart(value="params") MultipartFile params,
            @RequestPart(value="metadata", required=false) MultipartFile metadata,
            @RequestPart(value="specificTreatment", required=false) MultipartFile specificTreatment,
            @RequestPart(value="mapping", required=false) MultipartFile mapping,
            @RequestParam(value="multi-model", required=false, defaultValue="false") boolean multiModel)
            throws EnoControllerException {
        //
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        addMultipartToBody(multipartBodyBuilder, in, "in");
        addMultipartToBody(multipartBodyBuilder, params, "params");
        if (metadata != null)
            addMultipartToBody(multipartBodyBuilder, metadata, "metadata");
        if (specificTreatment != null)
            addMultipartToBody(multipartBodyBuilder, specificTreatment, "specificTreatment");
        if (mapping != null)
            addMultipartToBody(multipartBodyBuilder, mapping, "mapping");
        //
        URI uri = xmlControllerUtils.newUriBuilder().path("questionnaire/in-2-out").build().toUri();
        String outFilename = multiModel ? "questionnaire.zip" : "questionnaire.txt";
        return xmlControllerUtils.sendPostRequest(uri, multipartBodyBuilder, outFilename);
    }

}
