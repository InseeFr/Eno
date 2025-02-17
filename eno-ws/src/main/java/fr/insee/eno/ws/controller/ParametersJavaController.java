package fr.insee.eno.ws.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.ws.controller.utils.ResponseUtils;
import fr.insee.eno.ws.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Parameters (Eno Java)")
@RestController()
@RequestMapping("/parameters/java")
@SuppressWarnings("unused")
public class ParametersJavaController {

    /** Enum to be used in parameters of the web-service,
     * since the core Format enum doesn't distinguish in/out. */
    public enum OutFormat {LUNATIC} // For now only the Lunatic out format is supported by Eno Java

    private final ParameterService parameterService;

    public ParametersJavaController(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @Operation(
            summary = "Get parameters file for Eno Java services.",
            description = "Returns a `json` parameters file with standard values, in function of context and mode, " +
                    "for the concerned out format, to be used in _Eno Java_ services that require a parameters file.")
    @GetMapping(value = "{context}/{outFormat}/{mode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> getJavaParameters(
            @PathVariable EnoParameters.Context context,
            @PathVariable OutFormat outFormat,
            @PathVariable(name = "mode") EnoParameters.ModeParameter modeParameter) throws JsonProcessingException {
        return ResponseUtils.okFromFileDto(parameterService.defaultParameters(context, modeParameter, outFormat));
    }

}
