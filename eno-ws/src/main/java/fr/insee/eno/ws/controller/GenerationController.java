package fr.insee.eno.ws.controller;

import fr.insee.eno.legacy.model.BrowsingSuggest;
import fr.insee.eno.legacy.model.DDIVersion;
import fr.insee.eno.legacy.parameters.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name="Generation of questionnaire")
@Controller
@RequestMapping("/v2/questionnaire")
public class GenerationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GenerationController.class);


	@Operation(
			summary="Generation of questionnaire according to params, metadata and specificTreatment.",
			description="It generates a questionnaire : using the parameters file (required), metadata file (optional) and the specificTreatment file (optional). To use it, you have to upload all necessary files."
			)
	@PostMapping(value="in-2-out", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<ResponseEntity<Flux<DataBuffer>>> generate(
			@RequestPart(value="in",required=true) MultipartFile in, 
			@RequestPart(value="params",required=true) MultipartFile params,
			@RequestPart(value="metadata",required=false) MultipartFile metadata,
			@RequestPart(value="specificTreatment",required=false) MultipartFile specificTreatment,
			@RequestPart(value="mapping",required=false) MultipartFile mapping,
			
			@RequestParam(value="multi-model",required=false,defaultValue="false") boolean multiModel) throws Exception {

		return null;
	}


	@Operation(
			summary="Generation of fo questionnaire according to the given fo parameters.",
			description="It generates a fo questionnaire from a ddi questionnaire using the fo parameters given."
			)
	@PostMapping(value="ddi-2-fo", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<ResponseEntity<Flux<DataBuffer>>> generateFOQuestionnaire(

			// Files
			@RequestPart(value="in",required=true) MultipartFile in,
			@RequestPart(value="specificTreatment",required=false) MultipartFile specificTreatment,
						
			@RequestParam(value="DDIVersion",required=false,defaultValue="DDI_33") DDIVersion ddiVersion,
			@RequestParam(value="multi-model",required=false,defaultValue="false") boolean multiModel,
			
			@RequestParam Context context,

			@RequestParam(value="ResponseTimeQuestion") boolean EndQuestionResponseTime,
			@RequestParam(value="CommentQuestion") boolean EndQuestionCommentQuestion,

			
			@RequestParam(value="Format-orientation") Orientation orientation,
			@RequestParam(value="Format-column",defaultValue="1") int nbColumn,
			@RequestParam(value="AccompanyingMail") AccompanyingMail accompanyingMail,
			@RequestParam(value="PageBreakBetween") Level pageBreakBetween, 
			@RequestParam(value="Capture") CaptureEnum capture,
			@RequestParam(value="Browsing") BrowsingSuggest browsingSuggest
			) throws Exception {
		return null;
	}


	@Operation(
			summary="Generation of xforms questionnaire according to the given xforms parameters, metadata and specificTreatment.",
			description="It generates a xforms questionnaire from a ddi questionnaire using the xforms parameters given. For css parameters, sperate style sheet by ','"
			)
	@PostMapping(value="ddi-2-xforms", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<ResponseEntity<Flux<DataBuffer>>> generateXformsQuestionnaire(

			// Files
			@RequestPart(value="in",required=true) MultipartFile in,			
			@RequestPart(value="metadata",required=false) MultipartFile metadata,
			@RequestPart(value="specificTreatment",required=false) MultipartFile specificTreatment,
						
			@RequestParam(value="DDIVersion",required=true,defaultValue="DDI_33") DDIVersion ddiVersion,			
			@RequestParam(value="multi-model",required=false,defaultValue="false") boolean multiModel,

			@RequestParam Context context,


			@RequestParam(value="IdentificationQuestion") boolean IdentificationQuestion,
			@RequestParam(value="ResponseTimeQuestion") boolean EndQuestionResponseTime,
			@RequestParam(value="CommentQuestion") boolean EndQuestionCommentQuestion,

			@RequestParam(value="NumericExample") boolean numericExample,
			@RequestParam(value="Deblocage", defaultValue="false") boolean deblocage,
			@RequestParam(value="Satisfaction", defaultValue="false") boolean satisfaction,
			@RequestParam(value="LengthOfLongTable", defaultValue="7") int lengthOfLongTable, 
			@RequestParam(value="DecimalSeparator") DecimalSeparator decimalSeparator,
			@RequestParam(value="css", required=false) String css,
			@RequestParam(value="Browsing") BrowsingSuggest browsingSuggest
			) throws Exception {

		return null;
	}

	@Operation(
			summary="Generation of lunatic-json questionnaire according to the given js parameters and specificTreatment.",
			description="It generates a lunatic-json (flat) questionnaire from a ddi questionnaire using the js parameters given."
			)
	@PostMapping(value="ddi-2-lunatic-json", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<ResponseEntity<Flux<DataBuffer>>> generateJSQuestionnaire(

			// Files
			@RequestPart(value="in",required=true) MultipartFile in,
			@RequestPart(value="specificTreatment",required=false) MultipartFile specificTreatment,
			
			@RequestParam(value="DDIVersion",required=true,defaultValue="DDI_33") DDIVersion ddiVersion,

			@RequestParam Context context,
			
			@RequestParam(value="IdentificationQuestion") boolean IdentificationQuestion,
			@RequestParam(value="ResponseTimeQuestion") boolean EndQuestionResponseTime,
			@RequestParam(value="CommentQuestion") boolean EndQuestionCommentQuestion,
			
			@RequestParam(value="filterDescription", defaultValue="false") boolean filterDescription,
			@RequestParam(value="Browsing") BrowsingSuggest browsingSuggest
			) throws Exception {return null;
	}
	
	@Operation(
			summary="Generation of ddi questionnaire from pogues-xml questionnaire.",
			description="It generates a ddi questionnaire from a pogues-xml questionnaire. You can choose if the tranformation uses markdown to xhtml post processor."
			)
	@PostMapping(value="poguesxml-2-ddi", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<ResponseEntity<Flux<DataBuffer>>> generateDDIQuestionnaire(

			// Files
			@RequestPart(value="in",required=true) MultipartFile in,
			@RequestParam(value="mw-2-xhtml",required=true,defaultValue="true") boolean mw2xhtml) throws Exception {

		return null;
	}
	
	
	


	@Operation(
			summary="Generation of the specifications of the questionnaire according .",
			description="It generates a \".fodt\" questionnaire from a ddi questionnaire."
			)
	@PostMapping(value="ddi-2-fodt", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<ResponseEntity<Flux<DataBuffer>>> generateODTQuestionnaire(
			@RequestPart(value="in",required=true) MultipartFile in,
			@RequestParam(value="Browsing") BrowsingSuggest browsingSuggest
			) throws Exception {

		return null;
	}


}