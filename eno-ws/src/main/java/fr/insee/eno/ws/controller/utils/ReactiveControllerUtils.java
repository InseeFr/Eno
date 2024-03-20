package fr.insee.eno.ws.controller.utils;

import fr.insee.eno.core.exceptions.business.EnoParametersException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.treatments.LunaticRegroupementProcessing;
import fr.insee.eno.treatments.LunaticSuggesterSpecificTreatment;
import fr.insee.eno.treatments.SpecificTreatmentsDeserializer;
import fr.insee.eno.treatments.dto.EnoSuggesterType;
import fr.insee.eno.treatments.dto.Regroupement;
import fr.insee.eno.treatments.dto.SpecificTreatments;
import fr.insee.eno.treatments.exceptions.SpecificTreatmentsDeserializationException;
import fr.insee.eno.treatments.exceptions.SpecificTreatmentsValidationException;
import fr.insee.eno.ws.service.DDIToLunaticService;
import fr.insee.eno.ws.service.ParameterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.SequenceInputStream;
import java.util.List;

/** Class to factorize code in Eno Java controllers' methods. */
@Component
@Slf4j
public class ReactiveControllerUtils {

    public static final String LUNATIC_JSON_FILE_NAME = "lunatic-form.json";

    private final DDIToLunaticService ddiToLunaticService;
    private final ParameterService parameterService;

    public ReactiveControllerUtils(DDIToLunaticService ddiToLunaticService, ParameterService parameterService) {
        this.ddiToLunaticService = ddiToLunaticService;
        this.parameterService = parameterService;
    }

    // TODO: replace Mono<FilePart> parametersFile with EnoParameters argument (automatic deserialization)
    // TODO: implement API friendly endpoints that return json/xml instead of octet stream

    public Mono<EnoParameters> readEnoJavaParametersFile(Mono<FilePart> parametersFile) {
        return parametersFile
                .flatMap(this::validateEnoJavaParametersFileName)
                .flatMap(filePart -> filePart.content()
                        .map(dataBuffer -> dataBuffer.asInputStream(true))
                        .reduce(SequenceInputStream::new))
                .flatMap(parameterService::parse);
    }

    private Mono<FilePart> validateEnoJavaParametersFileName(FilePart filePart) {
        if (! filePart.filename().endsWith(".json"))
            return Mono.error(new EnoParametersException("Eno Java parameters file name must end with '.json'."));
        return Mono.just(filePart);
    }

    public Mono<ResponseEntity<String>> ddiToLunaticJson(Mono<FilePart> ddiFile, Mono<FilePart> parametersFile) {
        return readEnoJavaParametersFile(parametersFile)
                .flatMap(enoParameters -> ddiToLunaticJson(ddiFile, enoParameters));
    }

    public Mono<ResponseEntity<String>> ddiToLunaticJson(Mono<FilePart> ddiFile, EnoParameters enoParameters) {
        return ddiFile.flatMap(filePart -> filePart.content()
                        .map(dataBuffer -> dataBuffer.asInputStream(true))
                        .reduce(SequenceInputStream::new))
                .flatMap(inputStream -> ddiToLunaticService.transformToJson(inputStream, enoParameters))
                .map(result -> ResponseEntity
                        .ok()
                        .cacheControl(CacheControl.noCache())
                        .headers(HeadersUtils.with(LUNATIC_JSON_FILE_NAME))
                        .body(result));
    }

    /**
     *
     * @param ddiFile DDI xml file
     * @param enoParameters eno parameters
     * @param lunaticPostProcessings additional lunatic post processings
     * @return json lunatic response
     */
    public Mono<ResponseEntity<String>> ddiToLunaticJson(Mono<FilePart> ddiFile, EnoParameters enoParameters, Mono<LunaticPostProcessing> lunaticPostProcessings) {

        return ddiFile.flatMap(filePart -> filePart.content()
                        .map(dataBuffer -> dataBuffer.asInputStream(true))
                        .reduce(SequenceInputStream::new))
                .flatMap(inputStream ->
                    lunaticPostProcessings.flatMap(lunaticProcessings -> ddiToLunaticService.transformToJson(inputStream, enoParameters, lunaticProcessings)))
                .map(result -> ResponseEntity
                        .ok()
                        .cacheControl(CacheControl.noCache())
                        .headers(HeadersUtils.with(LUNATIC_JSON_FILE_NAME))
                        .body(result));
    }

    /**
     *
     * @param specificTreatment json specific treatment file
     * @return a lunatic post processing for this treatment
     */
    public Mono<LunaticPostProcessing> generateLunaticPostProcessings(Mono<Part> specificTreatment) {
        LunaticPostProcessing lunaticPostProcessings = new LunaticPostProcessing();

        return specificTreatment
                /*
                   This workaround (next filter) is used to make swagger works when empty value is checked for this input file on the endpoint
                   - there is no way to disallow empty checkbox value at this moment on swagger (though openAPI support configuring this)
                   - when empty value, spring boot considers the input as a DefaultFormField and not a file part, causing exceptions
                   if trying to cast to filepart
                   :-\
                 */
                .filter(FilePart.class::isInstance)
                .flatMap(specificTreatmentPart -> specificTreatmentPart.content()
                        .map(dataBuffer -> dataBuffer.asInputStream(true))
                        .reduce(SequenceInputStream::new))
                .flatMap(specificTreatmentStream -> {
                    try {
                        SpecificTreatmentsDeserializer deserializer = new SpecificTreatmentsDeserializer();
                        SpecificTreatments treatments = deserializer.deserialize(specificTreatmentStream);
                        List<EnoSuggesterType> suggesters = treatments.suggesters();
                        if(suggesters != null && !suggesters.isEmpty()) {
                            lunaticPostProcessings.addPostProcessing(new LunaticSuggesterSpecificTreatment(suggesters));
                        }

                        List<Regroupement> regroupements = treatments.regroupements();
                        if(regroupements != null && !regroupements.isEmpty()) {
                            lunaticPostProcessings.addPostProcessing(new LunaticRegroupementProcessing(regroupements));
                        }
                        return Mono.just(lunaticPostProcessings);
                    } catch (SpecificTreatmentsDeserializationException | SpecificTreatmentsValidationException ex) {
                        return Mono.error(ex);
                    }
                })
                .switchIfEmpty(Mono.just(lunaticPostProcessings));
    }
}
