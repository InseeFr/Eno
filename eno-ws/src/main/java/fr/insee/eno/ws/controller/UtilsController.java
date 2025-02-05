package fr.insee.eno.ws.controller;

import fr.insee.eno.ws.controller.utils.ResponseUtils;
import fr.insee.eno.ws.exception.EnoControllerException;
import fr.insee.eno.ws.service.EnoXmlClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static fr.insee.eno.ws.controller.utils.ControllerUtils.addMultipartToBody;

@Tag(name = "Utils")
@RestController
@RequestMapping("/utils")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class UtilsController {

    private final EnoXmlClient enoXmlClient;

    /**
     * Converts a DDI 3.2 file to a DDI 3.3 file.
     * @param in DDI 3.2 file.
     * @return DDI file converted to DDI 3.3 version.
     * @deprecated DDI 3.2 is no longer supported.
     */
    @Operation(
            summary = "Generation of DDI 3.3 from DDI 3.2.",
            description = "Generation of a DDI in 3.3 version from the given DDI in 3.2 version. " +
                    "_Note: DDI 3.2 is no longer supported._")
    @PostMapping(value = "ddi32-2-ddi33",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Deprecated(since = "3.24.0")
    public ResponseEntity<byte[]> convertDDI32ToDDI33(
            @RequestPart(value="in") MultipartFile in) throws EnoControllerException {
        //
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        addMultipartToBody(multipartBodyBuilder, in, "in");
        //
        URI uri = enoXmlClient.newUriBuilder().path("utils/ddi32-2-ddi33").build().toUri();
        return ResponseUtils.okFromFileDto(enoXmlClient.sendPostRequest(uri, multipartBodyBuilder));
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
    public ResponseEntity<String> convertXpathToVTL(
            @RequestParam(value="xpath") String xpath) {
        log.info("Sending Xpath expression to Eno legacy service: {}", xpath);
        URI uri = enoXmlClient.newUriBuilder()
                .path("/utils/xpath-2-vtl")
                .queryParam("xpath", xpath)
                .build().toUri();
        ResponseEntity<String> response = enoXmlClient.sendPostRequest(uri);
        log.info("VTL expression received: {}", response.getBody());
        return response;
    }

}
