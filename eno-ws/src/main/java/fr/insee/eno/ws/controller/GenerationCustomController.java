package fr.insee.eno.ws.controller;

import fr.insee.eno.exception.EnoParametersException;
import fr.insee.eno.parameters.*;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Tag(name="Generation from DDI (custom parameters)")
@RestController
@RequestMapping("/questionnaire")
@Slf4j
public class GenerationCustomController {
	// Eno-WS services
	private final ParameterService parameterService;

	// Eno core service
	private final MultiModelService multiModelService = new MultiModelService();
	private final ValorizatorParameters valorizatorParameters = new ValorizatorParametersImpl();

	public GenerationCustomController(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

	@Operation(
			summary = "Generation of Xforms questionnaire from DDI.",
			description = "Generation of a Xforms questionnaire from the given DDI with default business " +
					"pipeline, using a custom parameters file _(required)_, a metadata file _(required)_ and a " +
					"specific treatment file _(optional)_."
	)
	@PostMapping(value = "ddi-2-xforms",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<StreamingResponseBody> generateXformsCustomParams(
			@RequestPart(value="in") MultipartFile in,
			@RequestPart(value="params") MultipartFile params,
			@RequestPart(value="metadata") MultipartFile metadata,
			@RequestPart(value="specificTreatment",required=false) MultipartFile specificTreatment) throws Exception {

		log.info("Received request to transform DDI to a Xforms questionnaire.");

		InputStream paramsIS = params != null ? params.getInputStream() : null;
		ENOParameters enoParameters = valorizatorParameters.getParameters(paramsIS);
		Context context = enoParameters.getParameters().getContext();
		Mode mode = enoParameters.getMode();

		nonNullContextCheck(context);
		if (Context.HOUSEHOLD.equals(context))
			throw new EnoParametersException("Context 'HOUSEHOLD' is not compatible with Xforms format.");
		nonNullModeCheck(mode);
		if (Mode.CAPI.equals(mode) || Mode.CATI.equals(mode) || Mode.PAPI.equals(mode))
			throw new EnoParametersException("Mode '" + mode + "' is not compatible with Xforms format.");

		logModeAndContext(context, mode);

		ENOParameters defaultEnoParamsDDI2Xforms =  parameterService.getDefaultCustomParameters(
				context, OutFormat.XFORMS, null);
		
		Pipeline defaultPipeline = defaultEnoParamsDDI2Xforms.getPipeline();
		enoParameters.setPipeline(defaultPipeline);

		ByteArrayOutputStream enoOutput;
		try(
				InputStream enoInput = in.getInputStream();
				InputStream metadataIS = metadata != null ? metadata.getInputStream() : null;
				InputStream specificTreatmentIS = specificTreatment != null ? specificTreatment.getInputStream() : null){

			enoOutput = multiModelService.generateQuestionnaire(enoInput, enoParameters, metadataIS, specificTreatmentIS, null);
		}
		log.info("END of Eno Xforms generation processing");

		return ResponseUtils.generateResponseFromOutputStream(enoOutput, parameterService.getFileNameFromCampagneName(enoParameters));
	}

	@Operation(
			summary = "Generation of FO questionnaire from DDI.",
			description = "Generation of a FO questionnaire from the given DDI with default pipeline, " +
					"using a custom parameters file _(required)_, a metadata file _(optional)_ and a " +
					"specific treatment file _(optional)_."
	)
	@PostMapping(value = "ddi-2-fo",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<StreamingResponseBody> generateFOCustomParams(
			@RequestPart(value="in") MultipartFile in,
			@RequestPart(value="params") MultipartFile params,
			@RequestPart(value="metadata", required = false) MultipartFile metadata,
			@RequestPart(value="specificTreatment",required=false) MultipartFile specificTreatment) throws Exception {

		log.info("Received request to transform DDI to a FO questionnaire.");

		InputStream paramsIS = params != null ? params.getInputStream() : null;
		if (paramsIS == null)
			throw new EnoParametersException("Parameters file's content is null.");

		log.debug("Parsing XML parameters file...");
		ENOParameters enoParameters = valorizatorParameters.getParameters(paramsIS);
		log.debug("XML parameters parsed.");
		Context context = enoParameters.getParameters().getContext();
		Mode mode = enoParameters.getMode();

		nonNullContextCheck(context);
		nonNullModeCheck(mode);
		if (Mode.CAPI.equals(mode) || Mode.CATI.equals(mode) || Mode.CAWI.equals(mode))
			throw new EnoParametersException("Mode '" + mode + "' is not compatible with FO format.");

		logModeAndContext(context, mode);

		ENOParameters defaultEnoParamsDDI2Fo =  parameterService.getDefaultCustomParameters(
				context, OutFormat.FO, null);
		
		Pipeline defaultPipeline = defaultEnoParamsDDI2Fo.getPipeline();
		enoParameters.setPipeline(defaultPipeline);

		ByteArrayOutputStream enoOutput;
		try(
				InputStream enoInput = in.getInputStream();
				InputStream metadataIS = metadata != null ? metadata.getInputStream() : null;
				InputStream specificTreatmentIS = specificTreatment != null ? specificTreatment.getInputStream() : null) {

			enoOutput = multiModelService.generateQuestionnaire(enoInput, enoParameters, metadataIS, specificTreatmentIS, null);
		}
		log.info("END of Eno FO generation processing");
		return ResponseUtils.generateResponseFromOutputStream(enoOutput, parameterService.getFileNameFromCampagneName(enoParameters));

	}

	private static void nonNullContextCheck(Context context) {
		if (context == null)
			throw new EnoParametersException("No context defined in Eno parameters file given.");
	}

	private static void nonNullModeCheck(Mode mode) {
		if (mode == null)
			throw new EnoParametersException("No mode defined in Eno parameters file given.");
	}

	private static void logModeAndContext(Context context, Mode mode) {
		log.info("Context={}, Mode={} defined in parameters file", context, mode);
	}

}
