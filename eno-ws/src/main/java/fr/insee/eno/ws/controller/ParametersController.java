package fr.insee.eno.ws.controller;

import fr.insee.eno.legacy.parameters.Context;
import fr.insee.eno.legacy.parameters.OutFormat;
import fr.insee.eno.ws.PassePlat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name="Parameters")
@Controller
@RequestMapping("/parameter")
public class ParametersController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParametersController.class);

	@Autowired
	private PassePlat passePlat;

	@Operation(
			summary="Get all default out format parameters.",
			description="It returns the default parameters file without Pipeline which is overloaded. This file don't be used directly : you have to fill Pipeline.")
	@GetMapping(value="default", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public Mono<Void> getDefaultParam(ServerHttpRequest serverRequest, ServerHttpResponse serverHttpResponse){
		return passePlat.passePlatGet(serverRequest, serverHttpResponse);
	}

	@Operation(
			summary="Get default xml parameters file for the given context according to the outFormat",
			description="It returns parameters used by default according to the studyunit and the outFormat.")
	@GetMapping(value="{context}/default", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public Mono<ResponseEntity<Flux<DataBuffer>>> getDefaultOutParam(
			@PathVariable Context context,
			@RequestParam OutFormat outFormat) throws Exception {
		return null;
	}

}