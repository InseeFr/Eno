package fr.insee.eno.ws.controller;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.ws.exception.ContextException;
import fr.insee.eno.ws.exception.MetadataFileException;
import fr.insee.eno.ws.exception.ModeParameterException;
import fr.insee.eno.legacy.parameters.CaptureEnum;
import fr.insee.eno.legacy.parameters.Context;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.ws.PassThrough;
import fr.insee.eno.ws.controller.utils.ReactiveControllerUtils;
import fr.insee.eno.ws.exception.MultiModelException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Generation from DDI (standard parameters)")
@Controller
@RequestMapping("/questionnaire")
@Slf4j
@SuppressWarnings("unused")
public class GenerationStandardController {

    private final ReactiveControllerUtils controllerUtils;
    private final PassThrough passThrough;

    public GenerationStandardController(ReactiveControllerUtils controllerUtils, PassThrough passThrough) {
        this.controllerUtils = controllerUtils;
        this.passThrough = passThrough;
    }

    @Operation(
            summary = "[Eno Java service] Lunatic questionnaire generation from DDI.",
            description = "**This endpoint uses the 'Java' version of Eno.** " +
                    "Generation a Lunatic questionnaire from the given DDI with standard parameters, " +
                    "in function of context and mode. An optional specific treatment `json` file can be added.")
    @PostMapping(value = "{context}/lunatic-json/{mode}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> generateLunatic(
            @RequestPart(value="in") Mono<FilePart> ddiFile,
            @Parameter(name = "specificTreatment", schema = @Schema(type="string", format="binary"))
            @RequestPart(value="specificTreatment", required = false) Mono<Part> specificTreatment,
            @PathVariable EnoParameters.Context context,
            @PathVariable(name = "mode") EnoParameters.ModeParameter modeParameter) {
        if (EnoParameters.ModeParameter.PAPI.equals(modeParameter))
            return Mono.error(new ModeParameterException("Lunatic format is not compatible with the mode 'PAPER'."));

        /*
           specificTreatment parameter is a part instead of a FilePart. This workaround is used to make swagger work
           when empty value is checked for this input file on the endpoint.
           When empty value is checked, swagger send no content-type nor filename for this multipart file. In this case,
           Spring considers having a DefaultFormField object instead of FilePart and exceptions is thrown
           There is no way at this moment to disable the allow empty value when filed is not required.
         */
        Mono<LunaticPostProcessing> lunaticPostProcessing = controllerUtils.generateLunaticPostProcessings(specificTreatment);
        //
        EnoParameters enoParameters = EnoParameters.of(context, modeParameter, Format.LUNATIC);
        //
        return controllerUtils.ddiToLunaticJson(ddiFile, enoParameters, lunaticPostProcessing);
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
    public Mono<Void> generateXforms(
            @RequestPart(value="in") Mono<FilePart> in,
            @RequestPart(value="metadata", required = false) Mono<FilePart> metadata,
            @RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
            @PathVariable Context context,
            @RequestParam(value="multi-model", required=false, defaultValue="false") boolean multiModel,
            ServerHttpRequest request, ServerHttpResponse response) {
        if (Context.HOUSEHOLD.equals(context))
            return Mono.error(new ContextException("Xforms format is not compatible with 'HOUSEHOLD' context."));
        if (Context.BUSINESS.equals(context) && (! multiModel))
            return Mono.error(new MultiModelException("Multi-model option must be 'true' in 'BUSINESS' context."));
        metadata.hasElement()
                .flatMap(hasElementValue -> {
                    if (Context.BUSINESS.equals(context) && Boolean.FALSE.equals(hasElementValue))
                        return Mono.error(new MetadataFileException(
                                "The metadata file is required in 'BUSINESS' context."));
                    return null;
                });
        return passThrough.passePlatPost(request, response);
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
    public Mono<Void> generateFO(
            @RequestPart(value="in") Mono<FilePart> in,
            @RequestPart(value="metadata", required = false) Mono<FilePart> metadata,
            @RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
            @RequestParam(value="Format-column", required=false) Integer nbColumn,
            @RequestParam(value="Capture", required=false) CaptureEnum capture,
            @PathVariable Context context,
            @RequestParam(value="multi-model", required=false, defaultValue="false") boolean multiModel,
            ServerHttpRequest request, ServerHttpResponse response) {
        if (Context.BUSINESS.equals(context) && (! multiModel))
            return Mono.error(new MultiModelException("Multi-model option must be 'true' in 'BUSINESS' context."));
        metadata.hasElement()
                .flatMap(hasElementValue -> {
                    if (Context.BUSINESS.equals(context) && Boolean.FALSE.equals(hasElementValue))
                        return Mono.error(new MetadataFileException(
                                "The metadata file is required in 'BUSINESS' context."));
                    return null;
                });
        return passThrough.passePlatPost(request, response);
    }

    @Operation(
            summary = "[Eno Xml service] Generation of FODT specifications from DDI.",
            description = "**This endpoint uses the 'Xml' version of Eno.** " +
                    "Generate a FODT description of the of the given DDI with standard parameters, in function of " +
                    "context.")
    @PostMapping(value = "{context}/fodt",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> generateFODT(
            @RequestPart(value="in") Mono<FilePart> in,
            @PathVariable Context context,
            ServerHttpRequest request, ServerHttpResponse response) {
        return passThrough.passePlatPost(request, response);
    }

}
