package fr.insee.eno.ws.controller.utils;

import fr.insee.eno.legacy.parameters.OutFormat;
import fr.insee.eno.ws.exception.EnoControllerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

/**
 * Class to factorize code in Eno Xml controllers' (which consist in redirection to the legacy Xml web-service)
 * methods.
 */
@Component
public class EnoXmlControllerUtils {

    private final String baseUrl;
    private final WebClient webClient;
    public UriComponentsBuilder newUriBuilder() {
        return UriComponentsBuilder.fromHttpUrl(baseUrl);
    }

    public EnoXmlControllerUtils(@Value("${eno.legacy.ws.url}") String baseUrl,
                                 WebClient webClient) {
        // Base url is not passed to the web client instance since each controller can pass a new URI object.
        this.baseUrl = baseUrl;
        this.webClient = webClient;
    }

    public ResponseEntity<String> sendGetRequest(URI uri, String outFilename) {
        String responseBody = webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                .block();
        return ResponseEntity.ok()
                .headers(HeadersUtils.with(outFilename))
                .body(responseBody);
    }

    public ResponseEntity<String> sendPostRequest(URI uri, MultipartBodyBuilder multipartBodyBuilder, String outFilename) {
        String result = webClient.post()
                .uri(uri)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                .block();
        return ResponseEntity.ok()
                .headers(HeadersUtils.with(outFilename))
                .body(result);
    }

    public static void addMultipartToBody(MultipartBodyBuilder multipartBodyBuilder, MultipartFile multipartFile,
                                          String partName) throws EnoControllerException {
        try {
            multipartBodyBuilder.part(partName, multipartFileToByteArray(multipartFile));
        } catch (IOException e) {
            throw new EnoControllerException(
                    "Unable to access content of given file " + multipartFile.getOriginalFilename());
        }
    }

    private static ByteArrayResource multipartFileToByteArray(MultipartFile multipartFile) throws IOException {
        // Ugly but I didn't find anything better
        return new ByteArrayResource(multipartFile.getBytes()) {
            @Override
            public String getFilename() {
                return multipartFile.getOriginalFilename();
            }
        };
    }

    public static String questionnaireFilename(OutFormat outFormat, boolean multiModel) {
        if(multiModel) return "questionnaires.zip";
        return switch (outFormat){
            case FO -> "questionnaire.fo";
            case FODT -> "questionnaire.fodt";
            case DDI -> "ddi-questionnaire.xml";
            case XFORMS -> "questionnaire.xhtml";
        };
    }

}
