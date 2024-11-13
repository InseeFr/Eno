package fr.insee.eno.ws.controller;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.ws.controller.utils.EnoJavaControllerUtils;
import fr.insee.eno.ws.controller.utils.EnoXmlControllerUtils;
import fr.insee.eno.ws.exception.*;
import fr.insee.eno.ws.legacy.parameters.CaptureEnum;
import fr.insee.eno.ws.legacy.parameters.OutFormat;
import fr.insee.eno.ws.service.DDIToLunaticService;
import fr.insee.eno.ws.service.PoguesToLunaticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

import static fr.insee.eno.ws.controller.utils.EnoXmlControllerUtils.addMultipartToBody;
import static fr.insee.eno.ws.controller.utils.EnoXmlControllerUtils.questionnaireFilename;

@Tag(name = "Generation of questionnaire (standard parameters)")
@Controller
@RequestMapping("/questionnaire")
@Slf4j
@SuppressWarnings("unused")
public class GenerationStandardController {

    @Value("${eno.direct.pogues.lunatic}")
    private Boolean directPoguesToLunatic;

    private final DDIToLunaticService ddiToLunaticService;
    private final PoguesToLunaticService poguesToLunaticService;
    private final GenerationPoguesController generationPoguesController;
    private final EnoJavaControllerUtils javaControllerUtils;
    private final EnoXmlControllerUtils xmlControllerUtils;

    public GenerationStandardController(
            DDIToLunaticService ddiToLunaticService,
            PoguesToLunaticService poguesToLunaticService,
            GenerationPoguesController generationPoguesController,
            EnoJavaControllerUtils javaControllerUtils,
            EnoXmlControllerUtils xmlControllerUtils) {
        this.ddiToLunaticService = ddiToLunaticService;
        this.poguesToLunaticService = poguesToLunaticService;
        this.generationPoguesController = generationPoguesController;
        this.javaControllerUtils = javaControllerUtils;
        this.xmlControllerUtils = xmlControllerUtils;
    }

    @Operation(
            summary = "Lunatic questionnaire generation from Pogues.",
            description = "Generation of a Lunatic questionnaire from the Pogues `json` questionnaire with standard " +
                    "parameters, in function of context and mode. An optional specific treatment `json` file can be " +
                    "added.")
    @PostMapping(value = "pogues-2-lunatic/{context}/{mode}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> generateLunaticFromPogues(
            @RequestPart(value="in") MultipartFile poguesFile,
            @RequestPart(value="specificTreatment", required = false) MultipartFile specificTreatment,
            @PathVariable Context context,
            @PathVariable(name = "mode") EnoParameters.ModeParameter modeParameter,
            @RequestParam(defaultValue = "false") boolean dsfr)
            throws ModeParameterException, DDIToLunaticException, EnoControllerException, IOException {
        //
        if (EnoParameters.ModeParameter.PAPI.equals(modeParameter))
            throw new ModeParameterException("Lunatic format is not compatible with the mode 'PAPER'.");
        //
        EnoParameters enoParameters = EnoParameters.of(context, modeParameter, Format.LUNATIC);
        enoParameters.getLunaticParameters().setDsfr(dsfr);

        //
        if (Boolean.TRUE.equals(directPoguesToLunatic))
            return javaControllerUtils.transformToLunatic(
                    poguesFile, enoParameters, specificTreatment, poguesToLunaticService);

        //
        String ddiContent = generationPoguesController.generateDDIQuestionnaire(poguesFile).getBody();
        if (ddiContent == null)
            throw new EnoRedirectionException("Result of the Pogues to DDI transformation is null.");
        return javaControllerUtils.transformToLunatic(ddiContent, enoParameters, specificTreatment, ddiToLunaticService);
    }

    @Operation(
            summary = "[Eno Java service] Lunatic questionnaire generation from DDI.",
            description = "**This endpoint uses the 'Java' version of Eno.** " +
                    "Generation a Lunatic questionnaire from the given DDI with standard parameters, " +
                    "in function of context and mode. An optional specific treatment `json` file can be added.")
    @PostMapping(value = "{context}/lunatic-json/{mode}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> generateLunatic(
            @RequestPart(value="in") MultipartFile ddiFile,
            @RequestPart(value="specificTreatment", required = false) MultipartFile specificTreatment,
            @PathVariable Context context,
            @PathVariable(name = "mode") EnoParameters.ModeParameter modeParameter,
            @RequestParam(defaultValue = "false") boolean dsfr)
            throws ModeParameterException, DDIToLunaticException, EnoControllerException, IOException {
        //
        if (EnoParameters.ModeParameter.PAPI.equals(modeParameter))
            throw new ModeParameterException("Lunatic format is not compatible with the mode 'PAPER'.");
        //
        EnoParameters enoParameters = EnoParameters.of(context, modeParameter, Format.LUNATIC);
        enoParameters.getLunaticParameters().setDsfr(dsfr);
        //
        return javaControllerUtils.transformToLunatic(ddiFile, enoParameters, specificTreatment, ddiToLunaticService);
    }

    @Operation(
            summary = "[Eno Xml service] Xforms questionnaire generation from DDI.",
            description = "**This endpoint uses the 'Xml' version of Eno.** " +
                    "Generation of a Xforms questionnaire (for business web surveys) from the given DDI with " +
                    "standard parameters, in function of context. " +
                    "An metadata `xml` file can be added and is required is the context is 'BUSINESS'. " +
                    "An optional specific treatment `xsl` file can be added. " +
                    "If the multi-model option is set to true, the output questionnaire(s) are put in a zip file.")
    @PostMapping(value = "{context}/xforms",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> generateXforms(
            @RequestPart(value="in") MultipartFile in,
            @RequestPart(value="metadata", required = false) MultipartFile metadata,
            @RequestPart(value="specificTreatment", required=false) MultipartFile specificTreatment,
            @PathVariable Context context,
            @RequestParam(value="multi-model", required=false, defaultValue="false") boolean multiModel)
            throws MetadataFileException, ContextException, MultiModelException, EnoControllerException {
        //
        if (Context.HOUSEHOLD.equals(context))
            throw new ContextException("Xforms format is not compatible with 'HOUSEHOLD' context.");
        if (Context.BUSINESS.equals(context) && (! multiModel))
            throw new MultiModelException("Multi-model option must be 'true' in 'BUSINESS' context.");
        if (Context.BUSINESS.equals(context) && (metadata == null))
            throw new MetadataFileException("The metadata file is required in 'BUSINESS' context.");
        //
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        addMultipartToBody(multipartBodyBuilder, in, "in");
        if (metadata != null)
            addMultipartToBody(multipartBodyBuilder, metadata, "metadata");
        if (specificTreatment != null)
            addMultipartToBody(multipartBodyBuilder, specificTreatment, "specificTreatment");
        //
        URI uri = xmlControllerUtils.newUriBuilder()
                .path("/questionnaire/{context}/xforms")
                .queryParam("multi-model", multiModel)
                .build(context);
        return xmlControllerUtils.sendPostRequestByte(uri, multipartBodyBuilder);
    }

    @Operation(
            summary = "[Eno Xml service] XSL-FO questionnaire generation from DDI.",
            description = "**This endpoint uses the 'Xml' version of Eno.** " +
                    "Generation of a XSL-FO questionnaire (for the paper format) from the given DDI with standard " +
                    "parameters, in function of context. Custom values can be passed for format of columns and " +
                    "capture mode. " +
                    "An metadata `xml` file can be added and is required is the context is 'BUSINESS'. " +
                    "An optional specific treatment `xsl` file can be added. " +
                    "If the multi-model option is set to true, the output questionnaire(s) are put in a zip file." )
    @PostMapping(value = "{context}/fo",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> generateFO(
            @RequestPart(value="in") MultipartFile in,
            @RequestPart(value="metadata", required = false) MultipartFile metadata,
            @RequestPart(value="specificTreatment", required=false) MultipartFile specificTreatment,
            @RequestParam(value="Format-column", required=false) Integer nbColumn,
            @RequestParam(value="Capture", required=false) CaptureEnum capture,
            @PathVariable Context context,
            @RequestParam(value="multi-model", required=false, defaultValue="false") boolean multiModel)
            throws MultiModelException, MetadataFileException, EnoControllerException {
        //
        if (Context.BUSINESS.equals(context) && (! multiModel))
            throw new MultiModelException("Multi-model option must be 'true' in 'BUSINESS' context.");
        if (Context.BUSINESS.equals(context) && metadata != null)
            throw new MetadataFileException("The metadata file is required in 'BUSINESS' context.");
        //
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        addMultipartToBody(multipartBodyBuilder, in, "in");
        if (metadata != null)
            addMultipartToBody(multipartBodyBuilder, metadata, "metadata");
        if (specificTreatment != null)
            addMultipartToBody(multipartBodyBuilder, specificTreatment, "specificTreatment");
        //
        URI uri = xmlControllerUtils.newUriBuilder()
                .path("/questionnaire/{context}/fo")
                .queryParam("Format-column", nbColumn)
                .queryParam("Capture", capture)
                .queryParam("multi-model", multiModel)
                .build(context);
        String outFilename = questionnaireFilename(OutFormat.FO, multiModel);
        return xmlControllerUtils.sendPostRequest(uri, multipartBodyBuilder, outFilename);
    }

    @Operation(
            summary = "[Eno Xml service] Generation of FODT specifications from DDI.",
            description = "**This endpoint uses the 'Xml' version of Eno.** " +
                    "Generate a FODT description of the of the given DDI with standard parameters, in function of " +
                    "context.")
    @PostMapping(value = "{context}/fodt",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> generateFODT(
            @RequestPart(value="in") MultipartFile in,
            @PathVariable Context context) throws EnoControllerException {
        //
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        addMultipartToBody(multipartBodyBuilder, in, "in");
        //
        URI uri = xmlControllerUtils.newUriBuilder().path("/questionnaire/{context}/fodt").build(context);
        String outFilename = questionnaireFilename(OutFormat.FODT, false);
        return xmlControllerUtils.sendPostRequest(uri, multipartBodyBuilder, outFilename);
    }

}
