package fr.insee.eno.ws.controller;

import fr.insee.eno.legacy.parameters.Context;
import fr.insee.eno.legacy.parameters.Mode;
import fr.insee.eno.ws.PassePlat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Mono;

@Tag(name = "Simple generation of questionnaire")
@Controller
@RequestMapping("/questionnaire")
@Slf4j
@SuppressWarnings("unused")
public class SimpleGenerationController {

    private final PassePlat passePlat;

    public SimpleGenerationController(PassePlat passePlat) {
        this.passePlat = passePlat;
    }

    @Operation(
            summary = "Generation of XSL-FO questionnaire according to the context.",
            description = "Generate a XSL-FO questionnaire from a DDI questionnaire " +
                    "using the default FO parameters according to the study unit. " +
                    "See it using the end point: `/parameter/{context}/FO/default`")
    @PostMapping(value = "{context}/fo",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> generateFOQuestionnaire(
            @RequestPart(value="in", required=true) Mono<FilePart> in,
            @RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
            @PathVariable Context context,
            ServerHttpRequest request, ServerHttpResponse response) throws Exception {
        return passePlat.passePlatPost(request, response);
    }

    @Operation(
            summary = "Generation of XForms questionnaire according to the context.",
            description = "Generate a XForms questionnaire from a DDI questionnaire " +
                    "using the default XForms parameters according to the study unit. " +
                    "See it using the end point: `/parameter/{context}/XFORMS/default`")
    @PostMapping(value = "{context}/xforms",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> generateXformsQuestionnaire(
            @RequestPart(value="in", required=true) Mono<FilePart> in,
            @RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
            @PathVariable Context context,
            ServerHttpRequest request, ServerHttpResponse response) throws Exception {
        return passePlat.passePlatPost(request, response);
    }

    @Operation(
            summary = "Generation of Lunatic xml questionnaire according to the context.",
            description = "Generate a Lunatic xml questionnaire from a DDI questionnaire " +
                    "using the default Lunatic parameters according to the study unit. " +
                    "See it using the end point: `/parameter/{context}/LUNATIC_XML/default`")
    @PostMapping(value = "{context}/lunatic-xml/{mode}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> generateLunaticXmlQuestionnaire(
            @RequestPart(value="in", required=true) Mono<FilePart> in,
            @RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
            @PathVariable Context context,
            @PathVariable Mode mode,
            ServerHttpRequest request, ServerHttpResponse response) throws Exception {
        return passePlat.passePlatPost(request, response);
    }

    @Operation(
            summary = "Generation of Lunatic json flat questionnaire according to the context.",
            description = "Generate a Lunatic json flat questionnaire from a DDI questionnaire " +
                    "using the default Lunatic parameters according to the study unit. " +
                    "See it using the end point : `/parameter/default`" +
                    "The params *parsingXpathVTL* must be 'true' (default value) if controls language is pseudo-xpath.")
    @PostMapping(value = "{context}/lunatic-json/{mode}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> generateLunaticJsonQuestionnaire(
            @RequestPart(value="in", required=true) Mono<FilePart> in,
            @RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
            @PathVariable Context context,
            @PathVariable Mode mode,
            ServerHttpRequest request, ServerHttpResponse response) throws Exception {
        return passePlat.passePlatPost(request, response);
    }

    @Operation(
            summary = "Generation of FODT questionnaire according to the context.",
            description = "Generate a FODT questionnaire from a DDI questionnaire " +
                    "using the default FODT parameters according to the study unit. " +
                    "See it using the end point : `/parameter/{context}/FODT/default`")
    @PostMapping(value = "{context}/fodt",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> generateFODTQuestionnaire(
            @RequestPart(value="in", required=true) Mono<FilePart> in,
            @PathVariable Context context,
            ServerHttpRequest request, ServerHttpResponse response) throws Exception {
        return passePlat.passePlatPost(request, response);
    }

}
