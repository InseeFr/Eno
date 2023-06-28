package fr.insee.eno.ws.service;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.treatments.LunaticPostProcessings;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.InputStream;

@Service
public class DDIToLunaticService {

    public Mono<String> transformToJson(InputStream ddiInputStream, EnoParameters parameterInputStream, LunaticPostProcessings lunaticPostProcessings) {
        try {
            return Mono.just(DDIToLunatic.transformToJson(ddiInputStream, parameterInputStream, lunaticPostProcessings));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<String> transformToJson(InputStream ddiInputStream, EnoParameters parameterInputStream) {
        try {
            return Mono.just(DDIToLunatic.transformToJson(ddiInputStream, parameterInputStream));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

}
