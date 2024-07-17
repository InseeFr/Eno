package fr.insee.eno.ws.controller;

import fr.insee.eno.legacy.parameters.Context;
import fr.insee.eno.legacy.parameters.Mode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

@Tag(name="Parameters (Eno Xml)")
@Controller
@RequestMapping("/parameters/xml")
@SuppressWarnings("unused")
public class ParametersXmlController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParametersXmlController.class);

	private final WebClient webClient;

	public ParametersXmlController(WebClient webClient) {
		this.webClient = webClient;
	}

	@Operation(
			summary = "Get all default Eno Xml parameters.",
			description= "Return the default parameters file for Eno Xml. This file cannot be used directly: " +
					"you have to fill the `Pipeline` section according to the desired transformation.")
	@GetMapping(value="all", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<String> getAllXmlParameters() {
		String responseBody = webClient.get()
				.uri("parameters/xml/all")
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
				.block();
		return ResponseEntity.ok(responseBody);
	}

	@Operation(
			summary = "Get parameters file for Eno Xml services.",
			description = "Returns a `xml` parameters file with standard values, in function of context and mode, " +
					"for the concerned out format, to be used in _Eno Xml_ services that require a parameters file.")
	@GetMapping(value="{context}/{outFormat}", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<String> getXmlParameters(
			@PathVariable Context context,
			@PathVariable fr.insee.eno.legacy.parameters.OutFormat outFormat,
			@RequestParam(value="Mode",required=false) Mode mode) {
		String responseBody = webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("parameters/xml/{context}/{outFormat}")
						.queryParam("Mode", mode)
						.build(context, outFormat))
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
				.block();
		return ResponseEntity.ok(responseBody);
	}

}
