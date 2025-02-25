package fr.insee.eno.ws.controller;

import fr.insee.eno.core.exceptions.business.EnoParametersException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.ws.controller.utils.ResponseUtils;
import fr.insee.eno.ws.exception.DDIToLunaticException;
import fr.insee.eno.ws.exception.EnoControllerException;
import fr.insee.eno.ws.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static fr.insee.eno.ws.controller.utils.ControllerUtils.addMultipartToBody;

@Tag(name = "Generation of questionnaire (custom parameters)")
@Controller
@RequestMapping("/questionnaire")
@RequiredArgsConstructor
@Slf4j
public class GenerationCustomController {

	@Value("${eno.direct.pogues.lunatic}")
	private Boolean directPoguesToLunatic;

	private final PoguesToLunaticService poguesToLunaticService;
	private final DDIToLunaticService ddiToLunaticService;
	private final DDIToXformsService ddiToXformsService;
	private final DDIToFOService ddiToFOService;
	private final ParameterService parameterService;
	private final SpecificTreatmentsService specificTreatmentsService;

	@Operation(
			summary = "Lunatic questionnaire generation from Pogues.",
			description= "Generation a Lunatic questionnaire from the Pogues `json` source, using a custom " +
					"parameters `json` file _(required)_ and a specific treatment `json` file _(optional)_. " +
					"You can get a parameters file by using the endpoint `/parameters/java/{context}/LUNATIC/{mode}`")
	@PostMapping(value = "pogues-2-lunatic",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<byte[]> generateLunaticFromPoguesCustomParams(
			@RequestPart(value="in") MultipartFile poguesFile,
			@RequestPart(value="params") MultipartFile parametersFile,
			@RequestPart(value="specificTreatment", required=false) MultipartFile specificTreatment)
			throws DDIToLunaticException, EnoControllerException, EnoParametersException, IOException {

		EnoParameters enoParameters = parameterService.parse(parametersFile);
		LunaticPostProcessing lunaticPostProcessing = specificTreatmentsService.generateFrom(specificTreatment);

		return ResponseUtils.okFromFileDto(
				poguesToLunaticService.transform(poguesFile, enoParameters, lunaticPostProcessing));
	}

	//@deprecated Some features are not fully described in DDI. The Pogues to Lunatic endpoint should be used instead.
	@Operation(
			summary = "[Eno Java service] Lunatic questionnaire generation from DDI.",
			description= "**This endpoint is deprecated: use the `pogues-2-lunatic` endpoint.** " +
					"Generation a Lunatic questionnaire from the given DDI, using a custom parameters `json` file " +
					"_(required)_ and a specific treatment `json` file _(optional)_. " +
					"You can get a parameters file by using the endpoint `/parameters/java/{context}/LUNATIC/{mode}`")
	@PostMapping(value = "ddi-2-lunatic-json",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	//@Deprecated(since = "3.33.0")
	public ResponseEntity<byte[]> generateLunaticCustomParams(
			@RequestPart(value="in") MultipartFile ddiFile,
			@RequestPart(value="params") MultipartFile parametersFile,
			@RequestPart(value="specificTreatment", required=false) MultipartFile specificTreatment)
			throws DDIToLunaticException, EnoControllerException, EnoParametersException, IOException {
		EnoParameters enoParameters = parameterService.parse(parametersFile);
		LunaticPostProcessing lunaticPostProcessing = specificTreatmentsService.generateFrom(specificTreatment);
		return ResponseUtils.okFromFileDto(
				ddiToLunaticService.transform(ddiFile, enoParameters, lunaticPostProcessing));
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
	public ResponseEntity<byte[]> generateXformsCustomParams(
			@RequestPart(value="in") MultipartFile in,
			@RequestPart(value="params") MultipartFile params,
			@RequestPart(value="metadata") MultipartFile metadata,
			@RequestPart(value="specificTreatment", required=false) MultipartFile specificTreatment)
			throws EnoControllerException {
		//
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		addMultipartToBody(multipartBodyBuilder, in, "in");
		addMultipartToBody(multipartBodyBuilder, params, "params");
		if (metadata != null)
			addMultipartToBody(multipartBodyBuilder, metadata, "metadata");
		if (specificTreatment != null)
			addMultipartToBody(multipartBodyBuilder, specificTreatment, "specificTreatment");
		//
		return ResponseUtils.okFromFileDto(ddiToXformsService.transformWithCustomParams(multipartBodyBuilder));
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
	public ResponseEntity<byte[]> generateFOCustomParams(
			@RequestPart(value="in") MultipartFile in,
			@RequestPart(value="params") MultipartFile params,
			@RequestPart(value="metadata") MultipartFile metadata,
			@RequestPart(value="specificTreatment", required=false) MultipartFile specificTreatment)
			throws EnoControllerException {
		//
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		addMultipartToBody(multipartBodyBuilder, in, "in");
		addMultipartToBody(multipartBodyBuilder, params, "params");
		if (metadata != null)
			addMultipartToBody(multipartBodyBuilder, metadata, "metadata");
		if (specificTreatment != null)
			addMultipartToBody(multipartBodyBuilder, specificTreatment, "specificTreatment");
		//
		return ResponseUtils.okFromFileDto(ddiToFOService.transformWithCustomParams(multipartBodyBuilder));
	}

}
