package fr.insee.eno.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.view.UrlBasedViewResolver;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;

@SpringBootApplication
public class EnoWsApplication {

	public static void main(String[] args) {
		// Remove unauthorized header names for jdk httpclient :
		System.setProperty("jdk.httpclient.allowRestrictedHeaders", "host,connection");
		SpringApplication.run(EnoWsApplication.class, args);
	}

	@Bean
	public WebClient webClient(@Value("${test.url}") String baseUrl, WebClient.Builder builder) {
		return builder.baseUrl(baseUrl)
				// bug in jetty for DNS resolution in fucking Insee VPN ? => use jdk http client connector
				.clientConnector(new JdkClientHttpConnector())
				.build();
	}

	//@Bean
	public WebClient webClientWithProxy(@Value("${test.url}") String baseUrl,
										@Value("${test.proxy.port}") String proxyHost,
										@Value("${test.proxy.port}") int proxyPort,
										WebClient.Builder builder) {
		HttpClient httpClient = HttpClient.newBuilder()
				.proxy(ProxySelector.of(new InetSocketAddress(proxyHost,proxyPort)))
				.build();
		JdkClientHttpConnector connector = new JdkClientHttpConnector(httpClient);

		return builder.baseUrl(baseUrl)
				.clientConnector(new JdkClientHttpConnector())
				.clientConnector(connector)
				.build();
	}

	@Configuration
	public class WebConfig implements WebFluxConfigurer{
		public void configureViewResolvers(ViewResolverRegistry registry) {
			registry.viewResolver(new UrlBasedViewResolver());
		}
	}

}
