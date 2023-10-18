package fr.insee.eno.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;

/** Client to redirect requests send to Eno "java" web-service to the legacy Eno web-service. */
@Component
public class PassThrough {

    private final WebClient webClient;

    private final Integer timeout;

    public PassThrough(WebClient webClient, @Value("${eno.webclient.timeout}") Optional<Integer> timeout) {
        this.webClient = webClient;
        if(timeout.isEmpty()) {
            throw new IllegalArgumentException("Timeout is not configured for webclient");
        }
        this.timeout = timeout.get();
    }

    // TODO: better request headers managements

    public Mono<Void> passePlatGet(ServerHttpRequest serverRequest, ServerHttpResponse response) {
        return response.writeWith(this.webClient.get()
                .uri(builder -> builder.path(serverRequest.getURI().getPath())
                        .queryParams(serverRequest.getQueryParams())
                        .build())
                .headers(httpHeaders -> {
                    httpHeaders.clear();
                    serverRequest.getHeaders().forEach((key, strings) -> {
                        if (!"Host".equals(key)) httpHeaders.put(key, strings);
                    });
                })
                .exchangeToFlux(r -> {
                    response.setStatusCode(r.statusCode());
                    response.getHeaders().clear();
                    r.headers().asHttpHeaders().forEach((key, strings) ->
                            response.getHeaders().put(key.replace(":", ""), strings));
                    return r.bodyToFlux(DataBuffer.class);
                })
                .doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release)
                .timeout(Duration.ofSeconds(timeout)));
    }

    public Mono<Void> passePlatPost(ServerHttpRequest request, ServerHttpResponse response) {
        return response.writeWith(
                this.webClient.post()
                        .uri(builder -> builder.path(request.getURI().getPath())
                                .queryParams(request.getQueryParams())
                                .build())
                        .body(fromPublisher(request.getBody(), DataBuffer.class))
                        .headers(httpHeaders -> {
                            httpHeaders.clear();
                            request.getHeaders().forEach((key, strings) -> {
                                if (!"Host".equals(key)) httpHeaders.put(key, strings);
                            });
                        })
                        .exchangeToFlux(r -> {
                            response.setStatusCode(r.statusCode());
                            response.getHeaders().clear();
                            r.headers().asHttpHeaders().forEach((key, strings) ->
                                    response.getHeaders().put(key.replace(":", ""), strings));
                            return r.bodyToFlux(DataBuffer.class);
                        })
                        .doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release)
                        .timeout(Duration.ofSeconds(timeout)));
    }

}
