package fr.insee.eno.ws.controller;

import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.params.ValorizatorParameters;
import fr.insee.eno.params.ValorizatorParametersImpl;
import fr.insee.eno.service.MultiModelService;
import fr.insee.eno.service.ParameterizedGenerationService;
import fr.insee.eno.ws.controller.utils.ResponseUtils;
import fr.insee.eno.ws.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Tag(name="Generation with custom mapping")
@RestController
@RequestMapping("/questionnaire")
@Slf4j
public class GenerationWithMappingController {

	private final ParameterService parameterService;
	// Eno core services
	private final ParameterizedGenerationService parametrizedGenerationService = new ParameterizedGenerationService();

	private ValorizatorParameters valorizatorParameters = new ValorizatorParametersImpl();
	private final MultiModelService multiModelService =  new MultiModelService();

	public GenerationWithMappingController(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	// Weird endpoint to do weird things
	@Operation(
			summary = "Questionnaire generation according to params, metadata, specific treatment and mapping.",
			description = "Generation of one or multiple questionnaires from the input file given, " +
					"using a parameters file _(required)_, a metadata file _(optional)_, a specific treatment file " +
					"_(optional)_ and a mapping file _(optional)_. " +
					"If the multi-model option is set to true, the output questionnaire(s) are put in a zip file."
	)
	@PostMapping(value = "in-2-out",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<StreamingResponseBody> generate(
			//
			@RequestPart(value="in") MultipartFile in,
			@RequestPart(value="params") MultipartFile params,
			@RequestPart(value="metadata",required=false) MultipartFile metadata,
			@RequestPart(value="specificTreatment",required=false) MultipartFile specificTreatment,
			@RequestPart(value="mapping",required=false) MultipartFile mapping,
			//
			@RequestParam(value="multi-model",required=false,defaultValue="false") boolean multiModel) throws Exception {


		byte[] paramsBytes = params.getBytes();

		ENOParameters enoParameters = valorizatorParameters.getParameters(new ByteArrayInputStream(paramsBytes));
		ByteArrayOutputStream enoOutput;

		try(
				InputStream inputIS = in.getInputStream();
				InputStream paramIS = new ByteArrayInputStream(paramsBytes);
				InputStream metadataIS = metadata!=null ? metadata.getInputStream():null;
				InputStream specificTreatmentIS = specificTreatment!=null ? specificTreatment.getInputStream():null;
				InputStream mappingIS = mapping!=null ? mapping.getInputStream():null){
			if(multiModel) {
				enoOutput = multiModelService.generateQuestionnaire(
						inputIS, paramIS, metadataIS, specificTreatmentIS, mappingIS);
			}
			else {
				enoOutput = parametrizedGenerationService.generateQuestionnaire(
						inputIS, paramIS, metadataIS, specificTreatmentIS, mappingIS);
			}

			log.info("END of Eno 'in to out' processing");
		}

		return ResponseUtils.generateResponseFromOutputStream(enoOutput, parameterService.getFileNameFromParameters(enoParameters, multiModel));
	}

}
