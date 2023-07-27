package fr.insee.eno.ws.controller;

import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.legacy.parameters.*;
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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Generation of questionnaire")
@Controller
@RequestMapping("/questionnaire")
@Slf4j
public class GenerationController {

	private final V3ControllerUtils controllerUtils;
	private final PassePlat passePlat;

	public GenerationController(V3ControllerUtils controllerUtils, PassePlat passePlat) {
		this.controllerUtils = controllerUtils;
		this.passePlat = passePlat;
	}

	@Operation(
			summary = "Generation of questionnaire according to parameters.",
			description = "Generate a questionnaire using the parameters file (required), metadata file (optional) " +
					"and the specificTreatment file (optional). To use it, you have to upload all necessary files.")
	@PostMapping(value = "in-2-out",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@SuppressWarnings("unused")
	public Mono<Void> generate(
			@RequestPart(value="in") Mono<FilePart> in,
			@RequestPart(value="params") Mono<FilePart> params,
			@RequestPart(value="metadata", required=false) Mono<FilePart> metadata,
			@RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
			@RequestPart(value="mapping", required=false) Mono<FilePart> mapping,
			@RequestParam(value="multi-model", required=false, defaultValue="false") boolean multiModel,
			ServerHttpRequest request, ServerHttpResponse response) {
		return passePlat.passePlatPost(request, response);
	}

	@Operation(
			summary = "Generation of XSL-FO questionnaire.",
			description = "Generate a XSL-FO questionnaire from a DDI questionnaire using the FO parameters given.")
	@PostMapping(value = "ddi-2-fo",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@SuppressWarnings("unused")
	public Mono<Void> generateFOQuestionnaire(
			@RequestPart(value="in") Mono<FilePart> in,
			@RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
			@RequestParam(value="multi-model", required=false, defaultValue="false") boolean multiModel,
			@RequestParam Context context,
			@RequestParam(value="ResponseTimeQuestion") boolean endQuestionResponseTime,
			@RequestParam(value="CommentQuestion") boolean endQuestionCommentQuestion,
			@RequestParam(value="Format-orientation") Orientation orientation,
			@RequestParam(value="Format-column", defaultValue="1") int nbColumn,
			@RequestParam(value="AccompanyingMail") AccompanyingMail accompanyingMail,
			@RequestParam(value="PageBreakBetween") Level pageBreakBetween,
			@RequestParam(value="Capture") CaptureEnum capture,
			@RequestParam(value="QuestNum") BrowsingEnum questNum,
			@RequestParam(value="SeqNum") boolean seqNum,
			@RequestParam(value="PreQuestSymbol") boolean preQuestSymbol,
			ServerHttpRequest request, ServerHttpResponse response) {
		return passePlat.passePlatPost(request, response);
	}

	@Operation(
			summary="Generation of Xforms questionnaire.",
			description="Generate a Xforms questionnaire from a DDI questionnaire using the Xforms parameters given. " +
					"For css parameters, separate style sheet by ','")
	@PostMapping(value = "ddi-2-xforms",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@SuppressWarnings("unused")
	public Mono<Void> generateXformsQuestionnaire(
			@RequestPart(value="in") Mono<FilePart> in,
			@RequestPart(value="metadata", required=false) Mono<FilePart> metadata,
			@RequestPart(value="specificTreatment", required=false) Mono<FilePart> specificTreatment,
			@RequestParam(value="multi-model", required=false, defaultValue="false") boolean multiModel,
			@RequestParam(value="context") Context context,
			@RequestParam(value="IdentificationQuestion") boolean identificationQuestion,
			@RequestParam(value="ResponseTimeQuestion") boolean endQuestionResponseTime,
			@RequestParam(value="CommentQuestion") boolean endQuestionCommentQuestion,
			@RequestParam(value="NumericExample") boolean numericExample,
			@RequestParam(value="Deblocage", defaultValue="false") boolean deblocage,
			@RequestParam(value="Satisfaction", defaultValue="false") boolean satisfaction,
			@RequestParam(value="LengthOfLongTable", defaultValue="7") int lengthOfLongTable,
			@RequestParam(value="DecimalSeparator") DecimalSeparator decimalSeparator,
			@RequestParam(value="css", required=false) String css,
			@RequestParam(value="QuestNum") BrowsingEnum questNum,
			@RequestParam(value="SeqNum") boolean seqNum,
			@RequestParam(value="PreQuestSymbol") boolean preQuestSymbol,
			ServerHttpRequest request, ServerHttpResponse response) {
		return passePlat.passePlatPost(request, response);
	}

	@Operation(
			summary = "[V3] Generation of Lunatic json questionnaire.",
			description = "**This endpoint uses Eno v3**. " +
					"Generate a Lunatic json (flat) questionnaire from a DDI questionnaire " +
					"using the parameters given.")
	@PostMapping(value = "ddi-2-lunatic-json/{mode}",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<ResponseEntity<String>> generateLunaticJsonQuestionnaire(
			@RequestPart(value="in") Mono<FilePart> ddiFile,
			@Parameter(name = "specificTreatment",
					schema = @Schema(type="string", format="binary"))
			@RequestPart(value="specificTreatment", required=false) Mono<Part> specificTreatment,
			@PathVariable Mode mode,
			@RequestParam(value="context") EnoParameters.Context context,
			@RequestParam(value="IdentificationQuestion", required=false) boolean identificationQuestion,
			@RequestParam(value="ResponseTimeQuestion", required=false) boolean endQuestionResponseTime,
			@RequestParam(value="CommentQuestion", required=false) boolean endQuestionCommentQuestion,
			@RequestParam(value="parsingXpathVTL", required=false) boolean parsingXpathVTL,
			@RequestParam(value="filterDescription", defaultValue="false") boolean filterDescription,
			@RequestParam(value="control", defaultValue="false") boolean control,
			@RequestParam(value="missingVar", defaultValue="false") boolean missingVar,
			@RequestParam(value="AddFilterResult") boolean addFilterResult,
			@RequestParam(value="QuestNum") BrowsingEnum questNum,
			@RequestParam(value="SeqNum") boolean seqNum,
			@RequestParam(value="PreQuestSymbol") boolean preQuestSymbol,
			@RequestParam(value="Pagination", required=false, defaultValue="NONE") Pagination pagination,
			@RequestParam(value="includeUnusedCalculatedVariables") boolean unusedVars) {

		/*
           specificTreatment parameter is a part instead of a FilePart. This workaround is used to make swagger work
           when empty value is checked for this input file on the endpoint.
           When empty value is checked, swagger send no content-type nor filename for this multipart file. In this case,
           Spring considers having a DefaultFormField object instead of FilePart and exceptions is thrown
           There is no way at this moment to disable the allow empty value when filed is not required.
         */
		Mono<LunaticPostProcessing> lunaticPostProcessings = controllerUtils.generateLunaticPostProcessings(specificTreatment);

		//
		EnoParameters parameters = new EnoParameters(context, Format.LUNATIC);
		parameters.getSelectedModes().clear();
		parameters.getSelectedModes().add(mode);
		parameters.setIdentificationQuestion(identificationQuestion);
		parameters.setResponseTimeQuestion(endQuestionResponseTime);
		parameters.setIdentificationQuestion(endQuestionCommentQuestion);
		if (parsingXpathVTL)
			log.info("Parsing XpathVTL parameter is ignored.");
		parameters.setFilterDescription(filterDescription);
		if (filterDescription)
			log.info("'Filter description' feature is not supported yet.");
		parameters.setMissingVariables(missingVar);
		if (missingVar)
			log.info("'MISSING' variables is not implemented yet.");
		parameters.setFilterResult(addFilterResult);
		if (addFilterResult)
			log.info("'FILTER_RESULT' variables is not supported yet.");
		parameters.setControls(control);
		if (control)
			log.info("Generated format controls is not supported yet.");
		switch (questNum) {
			case ALL -> parameters.setQuestionNumberingMode(EnoParameters.QuestionNumberingMode.ALL);
			case MODULE -> parameters.setQuestionNumberingMode(EnoParameters.QuestionNumberingMode.SEQUENCE);
			case NO_NUMBER -> parameters.setQuestionNumberingMode(EnoParameters.QuestionNumberingMode.NONE);
		}
		parameters.setSequenceNumbering(seqNum);
		parameters.setArrowCharInQuestions(preQuestSymbol);
		switch (pagination) {
			case NONE -> parameters.setLunaticPaginationMode(EnoParameters.LunaticPaginationMode.NONE);
			case SEQUENCE -> parameters.setLunaticPaginationMode(EnoParameters.LunaticPaginationMode.SEQUENCE);
			case SUBSEQUENCE -> {
				parameters.setLunaticPaginationMode(EnoParameters.LunaticPaginationMode.NONE);
				log.info("Lunatic 'SUBSEQUENCE' pagination is not supported. Pagination has been set to 'NONE'.");
			}
			case QUESTION -> parameters.setLunaticPaginationMode(EnoParameters.LunaticPaginationMode.QUESTION);
		}
		parameters.setUnusedVariables(unusedVars);
		log.info("'Unused variables' feature is not implemented in Eno v3.");
		//
		return controllerUtils.ddiToLunaticJson(ddiFile, parameters, lunaticPostProcessings);
	}

	@Operation(
			summary = "Generation of DDI questionnaire from Pogues xml questionnaire.",
			description = "Generate a DDI questionnaire from a Pogues xml questionnaire.")
	@PostMapping(value="poguesxml-2-ddi",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@SuppressWarnings("unused")
	public Mono<Void> generateDDIQuestionnaire(
			@RequestPart(value="in") Mono<FilePart> in,
			ServerHttpRequest request, ServerHttpResponse response) {
		return passePlat.passePlatPost(request, response);
	}

	@Operation(
			summary = "Generation of the specifications of the questionnaire.",
			description = "Generates a FODT (Open Document file) questionnaire from a DDI questionnaire.")
	@PostMapping(value="ddi-2-fodt",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@SuppressWarnings("unused")
	public Mono<Void> generateODTQuestionnaire(
			@RequestPart(value="in") Mono<FilePart> in,
			@RequestParam(value="QuestNum") BrowsingEnum questNum,
			@RequestParam(value="SeqNum") boolean seqNum,
			@RequestParam(value="PreQuestSymbol") boolean preQuestSymbol,
			ServerHttpRequest request, ServerHttpResponse response) {
		return passePlat.passePlatPost(request, response);
	}

}