package fr.insee.eno.ws.controller;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.ws.controller.utils.EnoXmlControllerUtils;
import fr.insee.eno.ws.legacy.parameters.OutFormat;
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

import java.net.URI;

@Tag(name="Parameters (Eno Xml)")
@Controller
@RequestMapping("/parameters/xml")
@SuppressWarnings("unused")
public class ParametersXmlController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParametersXmlController.class);
	private final EnoXmlControllerUtils xmlControllerUtils;

	public ParametersXmlController(EnoXmlControllerUtils xmlControllerUtils) {
		this.xmlControllerUtils = xmlControllerUtils;
	}

	@Operation(
			summary = "Get all default Eno Xml parameters.",
			description= "Return the default parameters file for Eno Xml. This file cannot be used directly: " +
					"you have to fill the `Pipeline` section according to the desired transformation.")
	@GetMapping(value="all", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<String> getAllXmlParameters() {
		URI uri = xmlControllerUtils.newUriBuilder().path("parameters/xml/all").build().toUri();
		return xmlControllerUtils.sendGetRequest(uri, "eno-parameters-ALL.xml");
	}

	@Operation(
			summary = "Get parameters file for Eno Xml services.",
			description = "Returns a `xml` parameters file with standard values, in function of context and mode, " +
					"for the concerned out format, to be used in _Eno Xml_ services that require a parameters file.")
	@GetMapping(value="{context}/{outFormat}", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<String> getXmlParameters(
			@PathVariable Context context,
			@PathVariable OutFormat outFormat,
			@RequestParam(value="Mode",required=false) EnoParameters.ModeParameter mode) {
		URI uri = xmlControllerUtils.newUriBuilder()
				.path("parameters/xml/{context}/{outFormat}")
				.queryParam("Mode", mode)
				.build(context, outFormat);
		return xmlControllerUtils.sendGetRequest(uri, enoXmlParametersFilename(context, mode, outFormat));
	}

	private String enoXmlParametersFilename(Context context, EnoParameters.ModeParameter mode, OutFormat outFormat) {
		String contextSuffix = "-" + context;
		String modeSuffix = mode != null ? "-" + mode : "";
		String outFormatSuffix = "-" + outFormat;
		return "eno-parameters" + contextSuffix + modeSuffix + outFormatSuffix + ".xml";
	}

}
