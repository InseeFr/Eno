package fr.insee.eno.ws.controller;

import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.ws.controller.utils.V3ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Mono;

@Tag(name = "Integration of questionnaire")
@Controller
@RequestMapping("/integration-household")
@Slf4j
public class IntegrationHouseholdController {

    private final V3ControllerUtils controllerUtils;

    public IntegrationHouseholdController(V3ControllerUtils controllerUtils) {
        this.controllerUtils = controllerUtils;
    }

    @Operation(
            summary = "[V3] Integration of questionnaire according to mode, parameters, and specificTreatment.",
            description= "**This endpoint uses Eno v3**. " +
                    "Generate a Lunatic questionnaire for integration, using the collection mode given, " +
                    "the parameters file (required), and the specificTreatment file (optional).")
    @PostMapping(value = "ddi-2-lunatic-json/{mode}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> generateLunaticJson(
            @PathVariable Mode mode,
            @RequestPart(value="in") Mono<FilePart> ddiFile,
            @RequestPart(value="params") Mono<FilePart> parametersFile,
            @Parameter(name = "specificTreatment",
                    schema = @Schema(type="string", format="binary"))
            @RequestPart(value="specificTreatment", required=false) Mono<Part> specificTreatment) {

        /*
           specificTreatment parameter is a part instead of a FilePart. This workaround is used to make swagger work
           when empty value is checked for this input file on the endpoint.
           When empty value is checked, swagger send no content-type nor filename for this multipart file. In this case,
           Spring considers having a DefaultFormField object instead of FilePart and exceptions is thrown
           There is no way at this moment to disable the allow empty value when filed is not required.
         */
        Mono<LunaticPostProcessing> lunaticPostProcessings = controllerUtils.generateLunaticPostProcessings(specificTreatment);

        return controllerUtils.readParametersFile(parametersFile)
                .flatMap(enoParameters -> {
                    // Context
                    if (enoParameters.getContext() == null)
                        enoParameters.setContext(EnoParameters.Context.HOUSEHOLD);
                    if (! validateHouseholdParameters(enoParameters))
                        return Mono.error(new IllegalArgumentException(
                                "Invalid household parameters. Context is 'BUSINESS' in parameters given."));
                    // Mode
                    enoParameters.getSelectedModes().clear();
                    enoParameters.getSelectedModes().add(mode);
                    return Mono.just(enoParameters);
                })
                .flatMap(enoParameters -> controllerUtils.ddiToLunaticJson(ddiFile, enoParameters, lunaticPostProcessings));
    }

    private boolean validateHouseholdParameters(EnoParameters enoParameters) {
        return !EnoParameters.Context.BUSINESS.equals(enoParameters.getContext());
    }

}
