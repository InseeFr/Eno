package fr.insee.eno.ws.controller;

import fr.insee.eno.legacy.parameters.Context;
import fr.insee.eno.legacy.parameters.OutFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.http.HttpClient;

@Tag(name="Parameters")
@Controller
@RequestMapping("/v2/parameter")
public class ParametersController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParametersController.class);
	@Autowired
	private WebClient webClient;

	@Operation(
			summary = "Get all default out format parameters.",
			description = "It returns the default parameters file without Pipeline which is overloaded. " +
					"This file don't be used directly : you have to fill Pipeline.")
	@GetMapping(value="default", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public Mono<ResponseEntity<Flux<DataBuffer>>> getDefaultParam(ServerHttpRequest serverRequest){

		return this.webClient.get()
				.uri(serverRequest.getURI().getPath())
				.headers(httpHeaders -> {
					httpHeaders.clear();
					httpHeaders.addAll(serverRequest.getHeaders());
				})
				.retrieve()//exchange() : to access to the full server response
				.toEntityFlux(DataBuffer.class);
	}

	@Operation(
			summary="Get default xml parameters file for the given context according to the outFormat",
			description="It returns parameters used by default according to the study unit and the outFormat.")
	@GetMapping(value="{context}/default", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public Mono<ResponseEntity<Flux<DataBuffer>>> getDefaultOutParam(
			@PathVariable Context context,
			@RequestParam OutFormat outFormat) throws Exception {
		return null;
	}

}