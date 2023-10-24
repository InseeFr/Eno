package fr.insee.eno.ws.service;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.serialize.LunaticSerializer;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.InputStream;

@Service
@PropertySource("classpath:/version.properties")
public class DDIToLunaticService {

    @Value("${version.eno}")
    String enoVersion;
    @Value("${version.lunatic.model}")
    String lunaticModelVersion;

    public Mono<String> transformToJson(InputStream ddiInputStream, EnoParameters enoParameters, LunaticPostProcessing lunaticPostProcessings) {
        try {
            Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(ddiInputStream, enoParameters);
            lunaticQuestionnaire.setEnoCoreVersion(enoVersion);
            lunaticQuestionnaire.setLunaticModelVersion(lunaticModelVersion);
            lunaticPostProcessings.apply(lunaticQuestionnaire);
            return Mono.just(LunaticSerializer.serializeToJson(lunaticQuestionnaire));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<String> transformToJson(InputStream ddiInputStream, EnoParameters enoParameters) {
        try {
            Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(ddiInputStream, enoParameters);
            lunaticQuestionnaire.setEnoCoreVersion(enoVersion);
            lunaticQuestionnaire.setLunaticModelVersion(lunaticModelVersion);
            return Mono.just(LunaticSerializer.serializeToJson(lunaticQuestionnaire));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

}
