package fr.insee.eno.ws.controller.utils;

import fr.insee.eno.ws.exception.EnoControllerException;
import fr.insee.eno.ws.legacy.parameters.OutFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

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

    public ResponseEntity<byte[]> sendPostRequestByte(URI uri, MultipartBodyBuilder multipartBodyBuilder) {
        byte[] result = webClient.post()
                .uri(uri)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .retrieve()
                .bodyToMono(byte[].class)
                .block();

        // Récupération du header Content-Disposition
        String contentDisposition = Objects.requireNonNull(webClient.head()
                        .uri(uri)
                        .retrieve()
                        .toEntity(String.class)
                        .block())
                .getHeaders()
                .getFirst(HttpHeaders.CONTENT_DISPOSITION);

        // Extraction du filename
        String fileName = HeadersUtils.extractFileName(contentDisposition);

        // Création des headers avec le filename extrait
        HttpHeaders headers = HeadersUtils.with(fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(result);
    }

    public ResponseEntity<String> sendPostRequest(URI uri) {
        String result = webClient.post()
                .uri(uri)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                .block();
        return ResponseEntity.ok().body(result);
    }

    public static void addMultipartToBody(MultipartBodyBuilder multipartBodyBuilder, MultipartFile multipartFile,
                                          String partName) throws EnoControllerException {
        try {
            multipartBodyBuilder.part(partName, byteArrayResourceWithFileName(multipartFile.getBytes(), multipartFile.getOriginalFilename()));
        } catch (IOException e) {
            throw new EnoControllerException(
                    "Unable to access content of given file " + multipartFile.getOriginalFilename());
        }
    }

    public static void addStringToMultipartBody(
            MultipartBodyBuilder multipartBodyBuilder, String content, String fileName, String partName) {
        multipartBodyBuilder.part(partName, byteArrayResourceWithFileName(content.getBytes(), fileName));
    }

    private static ByteArrayResource byteArrayResourceWithFileName(byte[] bytes, String fileName) {
        // Ugly but I didn't find anything better
        return new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return fileName;
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
