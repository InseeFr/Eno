package fr.insee.eno.ws.controller;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Tag(name="Integration of questionnaire")
@Controller
@RequestMapping("/v2/integration-business")
public class IntegrationController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GenerationController.class);

	@Operation(
			summary="Integration of business questionnaire according to params, metadata and specificTreatment (business default pipeline is used).",
			description="It generates a questionnaire for intregation with default business pipeline  : using the parameters file (required), metadata file (optional) and the specificTreatment file (optional). To use it, you have to upload all necessary files."
			)
	@PostMapping(value= {"ddi-2-xforms"}, produces=MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<ResponseEntity<Flux<DataBuffer>>> generateXforms(
			@RequestPart(value="in",required=true) MultipartFile in, 
			@RequestPart(value="params",required=true) MultipartFile params,
			@RequestPart(value="metadata",required=true) MultipartFile metadata,
			@RequestPart(value="specificTreatment",required=false) MultipartFile specificTreatment) throws Exception {

		return null;
	}
	
	
	@Operation(
			summary="Integration of questionnaire according to params, metadata and specificTreatment.",
			description="It generates a questionnaire for intregation with default pipeline  : using the parameters file (required), metadata file (optional) and the specificTreatment file (optional). To use it, you have to upload all necessary files."
			)
	@PostMapping(value= {"ddi-2-fo"}, produces=MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<ResponseEntity<Flux<DataBuffer>>> generateFo(
			@RequestPart(value="in",required=true) MultipartFile in, 
			@RequestPart(value="params",required=true) MultipartFile params,
			@RequestPart(value="metadata",required=true) MultipartFile metadata,
			@RequestPart(value="specificTreatment",required=false) MultipartFile specificTreatment) throws Exception {

		return null;
	}


}
