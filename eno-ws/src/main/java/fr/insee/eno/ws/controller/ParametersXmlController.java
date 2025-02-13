package fr.insee.eno.ws.controller;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.ws.controller.utils.ResponseUtils;
import fr.insee.eno.ws.legacy.parameters.OutFormat;
import fr.insee.eno.ws.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name="Parameters (Eno Xml)")
@Controller
@RequestMapping("/parameters/xml")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class ParametersXmlController {

	private final ParameterService parameterService;

	@Operation(
			summary = "Get all default Eno Xml parameters.",
			description= "Return the default parameters file for Eno Xml. This file cannot be used directly: " +
					"you have to fill the `Pipeline` section according to the desired transformation.")
	@GetMapping(value="all", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> getAllXmlParameters() {
		return ResponseUtils.okFromFileDto(parameterService.getAllLegacyParameters());
	}

	@Operation(
			summary = "Get parameters file for Eno Xml services.",
			description = "Returns a `xml` parameters file with standard values, in function of context and mode, " +
					"for the concerned out format, to be used in _Eno Xml_ services that require a parameters file.")
	@GetMapping(value="{context}/{outFormat}", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> getXmlParameters(
			@PathVariable Context context,
			@PathVariable OutFormat outFormat,
			@RequestParam(value="Mode",required=false) EnoParameters.ModeParameter mode) {
		return ResponseUtils.okFromFileDto(parameterService.getLegacyParameters(context, mode, outFormat));
	}

}
