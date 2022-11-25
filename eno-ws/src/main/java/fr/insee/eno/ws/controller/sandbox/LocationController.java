package fr.insee.eno.ws.controller.sandbox;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.InetSocketAddress;

/** Foo controller to test call of an external WS */
@RestController
@RequestMapping("/dawan-ws")
public class LocationController {

    @GetMapping(value = {"", "/"})
    public Mono<ResponseEntity<String>> callDawanWS() throws Exception {

        // Proxy
        String proxyHost = "proxy-rie.http.insee.fr";
        int proxyPort = 8080;
        HttpClient httpClient = HttpClient.create().proxy(proxy ->
                proxy.type(ProxyProvider.Proxy.HTTP).address(new InetSocketAddress(proxyHost, proxyPort)));
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        String url = "https://dawan.org";
        WebClient webClient = WebClient.builder()
                .clientConnector(connector)
                .baseUrl(url)
                .build();

        return webClient.get().uri("public/location")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().toEntity(String.class);
    }

}
