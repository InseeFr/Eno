package fr.insee.eno.ws.service;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.DDIParsingException;
import fr.insee.eno.core.exceptions.LunaticSerializationException;
import fr.insee.eno.core.parameter.EnoParameters;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;

@Service
public class DDIToLunaticService {

    public Mono<String> transform(InputStream ddiInputStream, EnoParameters parameterInputStream) {
        try {
            return Mono.just(DDIToLunatic.transform(ddiInputStream, parameterInputStream));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

}
