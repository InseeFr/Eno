package fr.insee.eno.ws.controller;

import fr.insee.eno.parameters.Context;
import fr.insee.eno.parameters.Mode;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.ws.controller.utils.ResponseUtils;
import fr.insee.eno.ws.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;

@Tag(name="Parameters")
@RestController
@RequestMapping("/parameters/xml")
@Slf4j
public class ParametersController {

	private final ParameterService parameterService;

    public ParametersController(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @Operation(
			summary="Get all default out format parameters.", 
			description="It returns the default parameters file without Pipeline which is overloaded. This file don't be used directly : you have to fill Pipeline.")
	@GetMapping(value="all", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<StreamingResponseBody> getAllParameters() throws Exception {

		log.info("Get request for parameters file with all params.");


		InputStream paramsInputStream = parameterService.getDefaultParametersIS();

		return ResponseUtils.generateResponseFromInputStream(paramsInputStream, "default-params.xml");
	}

	@Operation(
			summary="Get default xml parameters file for the given context according to the outFormat",
			description="It returns parameters used by default according to the study unit and the outFormat.")
	@GetMapping(value="{context}/{outFormat}", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<StreamingResponseBody> getParameters(
			@PathVariable Context context,
			@PathVariable OutFormat outFormat,
			@RequestParam(value="Mode",required=false) Mode mode) throws Exception {

		log.info("Get request for parameters file with context {}, mode {}, out format {}.",
				context, mode, outFormat);

		InputStream fileParam = switch (outFormat) {
            case XFORMS -> parameterService.getDefaultCustomParametersFile(context, OutFormat.XFORMS, mode);
            case FO -> parameterService.getDefaultCustomParametersFile(context, OutFormat.FO, mode);
            case LUNATIC_XML -> parameterService.getDefaultCustomParametersFile(context, OutFormat.LUNATIC_XML, mode);
            case DDI -> parameterService.getDefaultCustomParametersFile(Context.DEFAULT, OutFormat.DDI, mode);
            case FODT -> parameterService.getDefaultCustomParametersFile(context, OutFormat.FODT, mode);
        };

		return ResponseUtils.generateResponseFromInputStream(fileParam, parametersFileName(context, mode, outFormat));
	}

	public static String parametersFileName(Context context, Mode mode, OutFormat outFormat) {
		return "eno-parameters-" + context + "-" + (mode != null ? mode + "-" : "") + outFormat + ".xml";
	}

}
