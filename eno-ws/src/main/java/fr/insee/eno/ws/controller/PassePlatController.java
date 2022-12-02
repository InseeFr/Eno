package fr.insee.eno.ws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class PassePlatController {

    @Autowired
    private WebClient webClient;

    @GetMapping("/v2/**")
    public Mono<ResponseEntity<Flux<DataBuffer>>> passePlat(ServerHttpRequest serverRequest){
        return this.webClient.get()
                .uri(serverRequest.getURI().getPath())
                .headers(httpHeaders -> {
                    httpHeaders.clear();
                    httpHeaders.addAll(serverRequest.getHeaders());
                })
                .retrieve()//exchange() : to access to the full server response
                .toEntityFlux(DataBuffer.class);
    }

}
