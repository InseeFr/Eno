package fr.insee.eno.ws.controller;

import fr.insee.eno.legacy.parameters.Context;
import fr.insee.eno.legacy.parameters.Mode;
import fr.insee.eno.legacy.parameters.OutFormat;
import fr.insee.eno.ws.PassThrough;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Tag(name="Parameters")
@Controller
@RequestMapping("/parameter")
@SuppressWarnings("unused")
public class ParametersController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParametersController.class);

	private final PassThrough passePlat;

	public ParametersController(PassThrough passePlat) {
		this.passePlat = passePlat;
	}

	@Operation(
			summary = "Get all default out format parameters.",
			description= "Return the default parameters file. This file should not be used directly: " +
					"you have to fill the `Pipeline` section according to the desired transformation.")
	@GetMapping(value="default", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public Mono<Void> getDefaultParam(ServerHttpRequest request, ServerHttpResponse response) {
		return passePlat.passePlatGet(request, response);
	}

	@Operation(
			summary = "Get default parameters file according to context and out format.",
			description = "Return parameters used by default in context and out format given.")
	@GetMapping(value="{context}/{outFormat}/default", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public Mono<Void> getDefaultOutParam(
			@PathVariable Context context,
			@PathVariable OutFormat outFormat,
			@RequestParam(value="Mode",required=false) Mode mode,
			ServerHttpRequest request, ServerHttpResponse response) {
		return passePlat.passePlatGet(request, response);
	}

}
