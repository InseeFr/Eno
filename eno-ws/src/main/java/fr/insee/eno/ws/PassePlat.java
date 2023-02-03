package fr.insee.eno.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;

@Component
public class PassePlat {

    @Autowired
    private WebClient webClient;

    public Mono<Void> passePlatGet(ServerHttpRequest serverRequest, ServerHttpResponse response) {
        return response.writeWith(this.webClient.get()
                .uri(serverRequest.getURI().getPath())
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
                            response.getHeaders().put(key.replace(":", ""), strings)); //TODO: >_<
                    return r.bodyToFlux(DataBuffer.class);
                }));
    }

    public Mono<Void> passePlatPost(ServerHttpRequest request, ServerHttpResponse response) {
        return response.writeWith(this.webClient.post()
                .uri(request.getURI().getPath())
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
                    response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    response.getHeaders().addAll(r.headers().asHttpHeaders());
                    return r.bodyToFlux(DataBuffer.class);
                }));
    }

}
