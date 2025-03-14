package fr.insee.eno.ws.service;

import fr.insee.eno.ws.dto.FileDto;
import fr.insee.eno.ws.exception.EnoRedirectionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class EnoXmlClient {

    private final String baseUrl;
    private final WebClient webClient;
    public UriComponentsBuilder newUriBuilder() {
        return UriComponentsBuilder.fromUriString(baseUrl);
    }

    public EnoXmlClient(
            @Value("${eno.legacy.ws.url}") String baseUrl,
            WebClient webClient) {
        // Base url is not passed to the web client instance since each controller can pass a new URI object.
        this.baseUrl = baseUrl;
        this.webClient = webClient;
    }

    public FileDto sendGetRequest(URI uri) {
        return webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .exchangeToMono(clientResponse -> {
                    FileDto fileDto = FileDto.builder()
                            .name(clientResponse.headers().asHttpHeaders().getContentDisposition().getFilename())
                            .content(clientResponse.bodyToMono(byte[].class).block())
                            .build();
                    clientResponse.bodyToMono(String.class);
                    return Mono.just(fileDto);
                })
                .block();
    }

    public FileDto sendPostRequest(URI uri, MultipartBodyBuilder multipartBodyBuilder) {
        FileDto result =  webClient.post()
                .uri(uri)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is4xxClientError())
                        return Mono.error(new EnoRedirectionException(
                                clientResponse.bodyToMono(String.class).block(),
                                clientResponse.statusCode()));
                    if (clientResponse.statusCode().is5xxServerError())
                        return Mono.error(new EnoRedirectionException(
                                "Server error: " + clientResponse.bodyToMono(String.class).block(),
                                clientResponse.statusCode()));
                    FileDto fileDto = FileDto.builder()
                            .name(clientResponse.headers().asHttpHeaders().getContentDisposition().getFilename())
                            .content(clientResponse.bodyToMono(byte[].class).block())
                            .build();
                    return Mono.just(fileDto);
                })
                .block();
        if (result == null)
            throw new EnoRedirectionException("null result from Eno Xml call.");
        return result;
    }

}
