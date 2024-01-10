package fr.insee.eno.ws.controller;

import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.ws.PassThrough;
import fr.insee.eno.ws.controller.utils.ReactiveControllerUtils;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Mono;

@Tag(name = "Generation from DDI (custom parameters)")
@Controller
@RequestMapping("/questionnaire")
@Slf4j
@SuppressWarnings("unused")
public class GenerationCustomController {

	private final PassThrough passePlat;
	private final ReactiveControllerUtils controllerUtils;

	public GenerationCustomController(PassThrough passePlat, ReactiveControllerUtils controllerUtils) {
		this.passePlat = passePlat;
		this.controllerUtils = controllerUtils;
	}

	@Operation(
			summary = "[Eno Java service] Lunatic questionnaire generation from DDI.",
			description= "**This endpoint uses the 'Java' version of Eno.** " +
					"Generation a Lunatic questionnaire from the given DDI, using a custom parameters `json` file " +
					"_(required)_ and a specific treatment `json` file _(optional)_. " +
					"You can get a parameters file by using the endpoint `/parameters/java/{context}/LUNATIC/{mode}`")
	@PostMapping(value = "ddi-2-lunatic-json",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<ResponseEntity<String>> generateLunaticCustomParams(
			@RequestPart(value="in") Mono<FilePart> ddiFile,
			@RequestPart(value="params") Mono<FilePart> parametersFile,
			@Parameter(name = "specificTreatment", schema = @Schema(type="string", format="binary"))
			@RequestPart(value="specificTreatment", required=false) Mono<Part> specificTreatment) {

        /*
           specificTreatment parameter is a part instead of a FilePart. This workaround is used to make swagger work
           when empty value is checked for this input file on the endpoint.
           When empty value is checked, swagger send no content-type nor filename for this multipart file. In this case,
           Spring considers having a DefaultFormField object instead of FilePart and exceptions is thrown
           There is no way at this moment to disable the allow empty value when filed is not required.
         */
		Mono<LunaticPostProcessing> lunaticPostProcessing = controllerUtils.generateLunaticPostProcessings(specificTreatment);

		return controllerUtils.readParametersFile(parametersFile)
				.flatMap(enoParameters ->
						controllerUtils.ddiToLunaticJson(ddiFile, enoParameters, lunaticPostProcessing));
	}

	@Operation(
			summary = "[Eno Xml service] Xforms questionnaire generation from DDI.",
			description = "**This endpoint uses the 'Xml' version of Eno.** " +
					"Generation of a Xforms questionnaire (for business web surveys) from the given DDI, using a " +
					"custom parameters `xml` file _(required)_, a metadata `xml` file _(required)_ and a specific " +
					"treatment `xsl` file _(optional)_. " +
					"You can get a parameters file by using the endpoint `/parameters/xml/BUSINESS/XFORMS`")
	@PostMapping(value = "ddi-2-xforms",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<Void> generateXformsCustomParams(
			@RequestPart(value="in") Mono<FilePart> in,
			@RequestPart(value="params") Mono<FilePart> params,
			@RequestPart(value="metadata") Mono<FilePart> metadata,
			@RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
			ServerHttpRequest request, ServerHttpResponse response) {
		return passePlat.passePlatPost(request, response);
	}

	@Operation(
			summary = "[Eno Xml service] FO questionnaire generation from DDI.",
			description = "**This endpoint uses the 'Xml' version of Eno.** " +
					"Generation of a FO questionnaire (for the paper format) from the given DDI, using a " +
					"custom parameters `xml` file _(required)_, a metadata `xml` file _(required)_ and a specific " +
					"treatment `xsl` file _(optional)_. " +
					"You can get a parameters file by using the endpoint `/parameters/xml/{context}/FO`")
	@PostMapping(value = "ddi-2-fo",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<Void> generateFOCustomParams(
			@RequestPart(value="in") Mono<FilePart> in,
			@RequestPart(value="params") Mono<FilePart> params,
			@RequestPart(value="metadata") Mono<FilePart> metadata,
			@RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
			ServerHttpRequest request, ServerHttpResponse response) {
		return passePlat.passePlatPost(request, response);
	}

}
