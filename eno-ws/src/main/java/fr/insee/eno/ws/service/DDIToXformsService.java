package fr.insee.eno.ws.service;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.ws.dto.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class DDIToXformsService {

    private final EnoXmlClient enoXmlClient;

    public FileDto transform(MultipartBodyBuilder multipartBodyBuilder, EnoParameters.Context context, boolean multiModel) {
        URI uri = enoXmlClient.newUriBuilder()
                .path("/questionnaire/{context}/xforms")
                .queryParam("multi-model", multiModel)
                .build(context);
        return enoXmlClient.sendPostRequest(uri, multipartBodyBuilder);
    }

    public FileDto transformWithCustomParams(MultipartBodyBuilder multipartBodyBuilder) {
        URI uri = enoXmlClient.newUriBuilder().path("questionnaire/ddi-2-xforms").build().toUri();
        return enoXmlClient.sendPostRequest(uri, multipartBodyBuilder);
    }

}
