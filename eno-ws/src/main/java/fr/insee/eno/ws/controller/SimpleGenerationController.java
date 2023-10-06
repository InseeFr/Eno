package fr.insee.eno.ws.controller;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.legacy.parameters.CaptureEnum;
import fr.insee.eno.legacy.parameters.Context;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.ws.PassThrough;
import fr.insee.eno.ws.controller.utils.V3ControllerUtils;
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

@Tag(name = "Simple generation of questionnaire")
@Controller
@RequestMapping("/questionnaire")
@Slf4j
public class SimpleGenerationController {

    private final V3ControllerUtils controllerUtils;
    private final PassThrough passePlat;

    public SimpleGenerationController(V3ControllerUtils controllerUtils, PassThrough passePlat) {
        this.controllerUtils = controllerUtils;
        this.passePlat = passePlat;
    }

    @Operation(
            summary = "Generation of XSL-FO questionnaire according to the context.",
            description = "Generate a XSL-FO questionnaire from a DDI questionnaire " +
                    "using the default FO parameters according to the study unit. " +
                    "See it using the end point: `/parameter/{context}/FO/default`")
    @PostMapping(value = "{context}/fo",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SuppressWarnings("unused")
    public Mono<Void> generateFOQuestionnaire(
            @RequestPart(value="in") Mono<FilePart> in,
            @RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
            @RequestParam(value="Format-column", required=false) Integer nbColumn,
            @RequestParam(value="Capture", required=false) CaptureEnum capture,
            @PathVariable Context context,
            ServerHttpRequest request, ServerHttpResponse response) {
        return passePlat.passePlatPost(request, response);
    }

    @Operation(
            summary = "Generation of XForms questionnaire according to the context.",
            description = "Generate a XForms questionnaire from a DDI questionnaire " +
                    "using the default XForms parameters according to the study unit. " +
                    "See it using the end point: `/parameter/{context}/XFORMS/default`")
    @PostMapping(value = "{context}/xforms",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SuppressWarnings("unused")
    public Mono<Void> generateXformsQuestionnaire(
            @RequestPart(value="in") Mono<FilePart> in,
            @RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
            @PathVariable Context context,
            ServerHttpRequest request, ServerHttpResponse response) {
        return passePlat.passePlatPost(request, response);
    }

    @Operation(
            summary = "[V3] Generation of Lunatic json flat questionnaire according to the context.",
            description = "**This endpoint uses Eno V3.** " +
                    "Generate a Lunatic json flat questionnaire from a DDI questionnaire " +
                    "using the default Lunatic parameters in function of context. " +
                    "To see these parameters, you can use the endpoint: `/v3/parameter/{context}/LUNATIC/default`")
    @PostMapping(value = "{context}/lunatic-json/{mode}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> generateLunaticJsonQuestionnaire(
            @RequestPart(value="in") Mono<FilePart> ddiFile,
            @Parameter(name = "specificTreatment",
                    schema = @Schema(type="string", format="binary"))
            @RequestPart(value="specificTreatment", required = false) Mono<Part> specificTreatment,
            @PathVariable EnoParameters.Context context,
            @PathVariable(name = "mode") EnoParameters.ModeParameter modeParameter) {

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
            summary = "Generation of FODT questionnaire according to the context.",
            description = "Generate a FODT questionnaire from a DDI questionnaire " +
                    "using the default FODT parameters according to the study unit. " +
                    "See it using the end point : `/parameter/{context}/FODT/default`")
    @PostMapping(value = "{context}/fodt",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SuppressWarnings("unused")
    public Mono<Void> generateFODTQuestionnaire(
            @RequestPart(value="in") Mono<FilePart> in,
            @PathVariable Context context,
            ServerHttpRequest request, ServerHttpResponse response) {
        return passePlat.passePlatPost(request, response);
    }

}
