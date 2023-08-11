package fr.insee.eno.ws.service;

import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.parameter.EnoParameters;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.InputStream;

@Service
public class ParameterService {

    public Mono<EnoParameters> parse(InputStream parametersInputStream) {
        try {
            return Mono.just(EnoParameters.parse(parametersInputStream));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<String> defaultParams() {
        try {
            return Mono.just(EnoParameters.serialize(EnoParameters.defaultValues()));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<String> defaultParams(EnoParameters.Context context, Format format, ModeParameter modeParameter) {
        try {
            return Mono.just(EnoParameters.serialize(EnoParameters.of(context, format, modeParameter)));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

}
