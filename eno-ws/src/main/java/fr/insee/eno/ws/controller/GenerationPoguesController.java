package fr.insee.eno.ws.controller;

import fr.insee.eno.parameters.*;
import fr.insee.eno.service.ParameterizedGenerationService;
import fr.insee.eno.ws.controller.utils.ResponseUtils;
import fr.insee.eno.ws.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Tag(name="Generation from Pogues")
@RestController
@RequestMapping("/questionnaire")
@Slf4j
public class GenerationPoguesController {
	private final ParameterService parameterService;

	// Eno core service
	private final ParameterizedGenerationService parametrizedGenerationService = new ParameterizedGenerationService();

	public GenerationPoguesController(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	@Operation(
			summary = "Generation DDI from Pogues XML questionnaire.",
			description = "Generation of a DDI from the given Pogues XML questionnaire."
	)
	@PostMapping(value="poguesxml-2-ddi", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<StreamingResponseBody> generateDDIQuestionnaire(
			@RequestPart(value="in") MultipartFile in) throws Exception {

		InputStream enoInput = in.getInputStream();
		ENOParameters enoParameters = new ENOParameters();
		Pipeline pipeline = new Pipeline();
		pipeline.setInFormat(InFormat.POGUES_XML);
		pipeline.setOutFormat(OutFormat.DDI);
		pipeline.getPreProcessing().add(PreProcessing.POGUES_XML_INSERT_FILTER_LOOP_INTO_QUESTION_TREE);
		pipeline.getPreProcessing().add(PreProcessing.POGUES_XML_GOTO_2_ITE);

		enoParameters.setPipeline(pipeline);
		
		ByteArrayOutputStream enoOutput = parametrizedGenerationService.generateQuestionnaire(
				enoInput, enoParameters, null, null, null);


		log.info("END of Eno DDI generation processing");

		StreamingResponseBody stream = out -> out.write(enoOutput.toByteArray());
		enoOutput.close();

		return ResponseUtils.generateResponseFromOutputStream(enoOutput, parameterService.getFileNameFromParameters(enoParameters, false));
	}

}
