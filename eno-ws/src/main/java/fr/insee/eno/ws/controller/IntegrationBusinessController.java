package fr.insee.eno.ws.controller;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.ws.PassePlat;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Mono;

@Tag(name = "Integration of questionnaire")
@Controller
@RequestMapping("/integration-business")
@Slf4j
@SuppressWarnings("unused")
public class IntegrationBusinessController {

	private final PassePlat passePlat;
	private final V3ControllerUtils controllerUtils;

	public IntegrationBusinessController(PassePlat passePlat, V3ControllerUtils controllerUtils) {
		this.passePlat = passePlat;
		this.controllerUtils = controllerUtils;
	}

	@Operation(
			summary = "Integration of business questionnaire according to params, metadata and specificTreatment " +
					"(business default pipeline is used).",
			description = "Generate a questionnaire for integration with default business pipeline: using the " +
					"parameters file (required), metadata file (required) and the specificTreatment file (optional). " +
					"To use it, you have to upload all necessary files.")
	@PostMapping(value = "ddi-2-xforms",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<Void> generateXforms(
			@RequestPart(value="in") Mono<FilePart> in,
			@RequestPart(value="params") Mono<FilePart> params,
			@RequestPart(value="metadata") Mono<FilePart> metadata,
			@RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
			ServerHttpRequest request, ServerHttpResponse response) {
		return passePlat.passePlatPost(request, response);
	}

	@Operation(
			summary = "Integration of questionnaire according to params, metadata and specificTreatment.",
			description = "Generate a questionnaire for integration with default pipeline: using the " +
					"parameters file (required), metadata file (required) and the specificTreatment file (optional). " +
					"To use it, you have to upload all necessary files.")
	@PostMapping(value = "ddi-2-fo",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<Void> generateFo(
			@RequestPart(value="in") Mono<FilePart> in,
			@RequestPart(value="params") Mono<FilePart> params,
			@RequestPart(value="metadata") Mono<FilePart> metadata,
			@RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
			ServerHttpRequest request, ServerHttpResponse response) {
		return passePlat.passePlatPost(request, response);
	}

	@Operation(
			summary = "[V3] Integration of questionnaire according to mode, parameters, and specificTreatment.",
			description= "**This endpoint uses Eno v3**. " +
					"Generate a Lunatic questionnaire for integration, using the collection mode given, " +
					"the parameters file (required), and the specificTreatment file (optional).")
	@PostMapping(value = "ddi-2-lunatic-json/{mode}",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<ResponseEntity<String>> generateLunaticJson(
			@PathVariable(name = "mode") EnoParameters.ModeParameter modeParameter,
			@RequestPart(value="in") Mono<FilePart> ddiFile,
			@RequestPart(value="params") Mono<FilePart> parametersFile,
			@Parameter(name = "specificTreatment",
					schema = @Schema(type="string", format="binary"))
			@RequestPart(value="specificTreatment", required=false) Mono<Part> specificTreatment) {

        /*
           specificTreatment parameter is a part instead of a FilePart. This workaround is used to make swagger work
           when empty value is checked for this input file on the endpoint.
           When empty value is checked, swagger send no content-type nor filename for this multipart file. In this case,
           Spring considers having a DefaultFormField object instead of FilePart and exceptions is thrown
           There is no way at this moment to disable the allow empty value when filed is not required.
         */
		Mono<LunaticPostProcessing> lunaticPostProcessings = controllerUtils.generateLunaticPostProcessings(specificTreatment);

		return controllerUtils.readParametersFile(parametersFile)
				.flatMap(enoParameters -> {
					// Context
					if (enoParameters.getContext() == null)
						enoParameters.setContext(EnoParameters.Context.BUSINESS);
					if (! validateBusinessParameters(enoParameters))
						return Mono.error(new IllegalArgumentException(
								"Invalid business parameters. Context is 'HOUSEHOLD' in parameters given."));
					// Mode
					enoParameters.setModeParameter(modeParameter);
					return Mono.just(enoParameters);
				})
				.flatMap(enoParameters -> controllerUtils.ddiToLunaticJson(ddiFile, enoParameters, lunaticPostProcessings));
	}

	private boolean validateBusinessParameters(EnoParameters enoParameters) {
		return !EnoParameters.Context.HOUSEHOLD.equals(enoParameters.getContext());
	}

}
