package fr.insee.eno.ws.controller.redirect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.InetSocketAddress;

@Slf4j
@RestController
@RequestMapping("/parameter")
public class ParameterController {

    // TODO: doesn't work
    @GetMapping(value="default", produces= MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<ServerHttpResponse>> getDefaultParam() throws Exception {

        String proxyHost = "proxy-rie.http.insee.fr";
        int proxyPort = 8080;
        HttpClient httpClient = HttpClient.create().proxy(proxy ->
                proxy.type(ProxyProvider.Proxy.HTTP).address(new InetSocketAddress(proxyHost, proxyPort)));
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        // http://eno-ws-sandbox.dev.insee.io/parameter/default

        String url = "http://eno-ws-sandbox.dev.insee.io";
        WebClient webClient = WebClient.builder()
                .clientConnector(connector)
                .baseUrl(url)
                .build();

        return webClient.get().uri("parameter/default").retrieve().toEntity(ServerHttpResponse.class);
    }

}
