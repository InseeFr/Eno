package fr.insee.eno.ws.service;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.ws.dto.FileDto;
import fr.insee.eno.ws.legacy.parameters.CaptureEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class DDIToFOService {

    private final EnoXmlClient enoXmlClient;

    public FileDto transform(
            MultipartBodyBuilder multipartBodyBuilder, EnoParameters.Context context,
            Integer numberOfColumns, CaptureEnum captureMode, boolean multiModel) {
        URI uri = enoXmlClient.newUriBuilder()
                .path("/questionnaire/{context}/fo")
                .queryParam("Format-column", numberOfColumns)
                .queryParam("Capture", captureMode)
                .queryParam("multi-model", multiModel)
                .build(context);
        return enoXmlClient.sendPostRequest(uri, multipartBodyBuilder);
    }

    public FileDto transformWithCustomParams(MultipartBodyBuilder multipartBodyBuilder) {
        URI uri = enoXmlClient.newUriBuilder().path("questionnaire/ddi-2-fo").build().toUri();
        return enoXmlClient.sendPostRequest(uri, multipartBodyBuilder);
    }

}
