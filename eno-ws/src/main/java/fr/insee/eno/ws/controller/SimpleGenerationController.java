package fr.insee.eno.ws.controller;

import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.legacy.parameters.CaptureEnum;
import fr.insee.eno.legacy.parameters.Context;
import fr.insee.eno.treatments.LunaticPostProcessings;
import fr.insee.eno.ws.PassePlat;
import fr.insee.eno.ws.controller.utils.V3ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
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
    private final PassePlat passePlat;

    public SimpleGenerationController(V3ControllerUtils controllerUtils, PassePlat passePlat) {
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
            summary = "Generation of Lunatic xml questionnaire according to the context.",
            description = "Generate a Lunatic xml questionnaire from a DDI questionnaire " +
                    "using the default Lunatic parameters in function of context. " +
                    "To see these parameters, you can use the endpoint: `/parameter/{context}/LUNATIC_XML/default`")
    @PostMapping(value = "{context}/lunatic-xml/{mode}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SuppressWarnings("unused")
    public Mono<Void> generateLunaticXmlQuestionnaire(
            @RequestPart(value="in") Mono<FilePart> ddiFile,
            @PathVariable EnoParameters.Context context,
            @PathVariable Mode mode,
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
            @RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
            @PathVariable EnoParameters.Context context,
            @PathVariable Mode mode) {
        //
        LunaticPostProcessings lunaticPostProcessings = controllerUtils.generateLunaticPostProcessings(specificTreatment);
        //
        EnoParameters enoParameters = new EnoParameters(context, Format.LUNATIC);
        enoParameters.getSelectedModes().clear();
        enoParameters.getSelectedModes().add(mode);
        //
        return controllerUtils.ddiToLunaticJson(ddiFile, enoParameters, lunaticPostProcessings);
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
