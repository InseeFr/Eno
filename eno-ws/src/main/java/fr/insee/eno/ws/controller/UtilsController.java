package fr.insee.eno.ws.controller;

import fr.insee.eno.core.utils.XpathToVtl;
import fr.insee.eno.ws.controller.utils.EnoXmlControllerUtils;
import fr.insee.eno.ws.exception.EnoControllerException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static fr.insee.eno.ws.controller.utils.EnoXmlControllerUtils.addMultipartToBody;

@Tag(name = "Utils")
@RestController
@RequestMapping("/utils")
@Slf4j
@SuppressWarnings("unused")
public class UtilsController {

    private final EnoXmlControllerUtils xmlControllerUtils;

    public UtilsController(EnoXmlControllerUtils xmlControllerUtils) {
        this.xmlControllerUtils = xmlControllerUtils;
    }

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
    public ResponseEntity<String> convertDDI32ToDDI33(
            @RequestPart(value="in") MultipartFile in) throws EnoControllerException {
        //
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        addMultipartToBody(multipartBodyBuilder, in, "in");
        //
        URI uri = xmlControllerUtils.newUriBuilder().path("utils/ddi32-2-ddi33").build().toUri();
        return xmlControllerUtils.sendPostRequest(uri, multipartBodyBuilder, "ddi33.xml");
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
        String result = XpathToVtl.parseToVTL(xpath);
        log.info("Xpath expression given parsed to VTL: {}", result);
        return ResponseEntity.ok().body(result);
    }

}
