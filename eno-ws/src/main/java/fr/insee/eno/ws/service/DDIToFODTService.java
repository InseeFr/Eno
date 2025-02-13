package fr.insee.eno.ws.service;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.ws.dto.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class DDIToFODTService {

    private final EnoXmlClient enoXmlClient;

    public FileDto transform(MultipartBodyBuilder multipartBodyBuilder, EnoParameters.Context context) {
        URI uri = enoXmlClient.newUriBuilder().path("/questionnaire/{context}/fodt").build(context);
        return enoXmlClient.sendPostRequest(uri, multipartBodyBuilder);
    }

}
