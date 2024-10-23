package fr.insee.eno.ws.controller;

import fr.insee.eno.core.exceptions.business.EnoParametersException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.ws.controller.utils.EnoJavaControllerUtils;
import fr.insee.eno.ws.controller.utils.EnoXmlControllerUtils;
import fr.insee.eno.ws.exception.DDIToLunaticException;
import fr.insee.eno.ws.exception.EnoControllerException;
import fr.insee.eno.ws.exception.EnoRedirectionException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

import static fr.insee.eno.ws.controller.utils.EnoXmlControllerUtils.addMultipartToBody;
import static fr.insee.eno.ws.controller.utils.EnoXmlControllerUtils.questionnaireFilename;

@Tag(name = "Generation of questionnaire (custom parameters)")
@Controller
@RequestMapping("/questionnaire")
@Slf4j
@SuppressWarnings("unused")
public class GenerationCustomController {

	@Value("${eno.direct.pogues.lunatic}")
	private Boolean directPoguesToLunatic;

	private final DDIToLunaticService ddiToLunaticService;
	private final PoguesToLunaticService poguesToLunaticService;
	private final GenerationPoguesController generationPoguesController;
	private final EnoJavaControllerUtils javaControllerUtils;
	private final EnoXmlControllerUtils xmlControllerUtils;

	public GenerationCustomController(
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
			description= "Generation a Lunatic questionnaire from the Pogues `json` source, using a custom " +
					"parameters `json` file _(required)_ and a specific treatment `json` file _(optional)_. " +
					"You can get a parameters file by using the endpoint `/parameters/java/{context}/LUNATIC/{mode}`")
	@PostMapping(value = "pogues-2-lunatic",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> generateLunaticFromPoguesCustomParams(
			@RequestPart(value="in") MultipartFile poguesFile,
			@RequestPart(value="params") MultipartFile parametersFile,
			@RequestPart(value="specificTreatment", required=false) MultipartFile specificTreatment)
			throws DDIToLunaticException, EnoControllerException, EnoParametersException, IOException {

		if (Boolean.TRUE.equals(directPoguesToLunatic))
			return javaControllerUtils.transformToLunatic(
					poguesFile, parametersFile, specificTreatment, poguesToLunaticService);

		String ddiContent = generationPoguesController.generateDDIQuestionnaire(poguesFile).getBody();
		if (ddiContent == null)
			throw new EnoRedirectionException("Result of the Pogues to DDI transformation is null.");
		EnoParameters enoParameters = javaControllerUtils.readEnoJavaParametersFile(parametersFile);
		return javaControllerUtils.transformToLunatic(ddiContent, enoParameters, specificTreatment, ddiToLunaticService);
	}

	@Operation(
			summary = "[Eno Java service] Lunatic questionnaire generation from DDI.",
			description= "**This endpoint uses the 'Java' version of Eno.** " +
					"Generation a Lunatic questionnaire from the given DDI, using a custom parameters `json` file " +
					"_(required)_ and a specific treatment `json` file _(optional)_. " +
					"You can get a parameters file by using the endpoint `/parameters/java/{context}/LUNATIC/{mode}`")
	@PostMapping(value = "ddi-2-lunatic-json",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> generateLunaticCustomParams(
			@RequestPart(value="in") MultipartFile ddiFile,
			@RequestPart(value="params") MultipartFile parametersFile,
			@RequestPart(value="specificTreatment", required=false) MultipartFile specificTreatment)
			throws DDIToLunaticException, EnoControllerException, EnoParametersException, IOException {
		return javaControllerUtils.transformToLunatic(ddiFile, parametersFile, specificTreatment, ddiToLunaticService);
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
	public ResponseEntity<Byte[]> generateXformsCustomParams(
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
		URI uri = xmlControllerUtils.newUriBuilder().path("questionnaire/ddi-2-xforms").build().toUri();
		String outFilename = questionnaireFilename(OutFormat.XFORMS, true);
		return xmlControllerUtils.sendPostRequestByte(uri, multipartBodyBuilder, outFilename);
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
	public ResponseEntity<String> generateFOCustomParams(
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
		URI uri = xmlControllerUtils.newUriBuilder().path("questionnaire/ddi-2-fo").build().toUri();
		String outFilename = questionnaireFilename(OutFormat.XFORMS, false);
		return xmlControllerUtils.sendPostRequest(uri, multipartBodyBuilder, outFilename);
	}

}
