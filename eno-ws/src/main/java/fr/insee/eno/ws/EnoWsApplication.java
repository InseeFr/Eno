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



	@Configuration
	public class WebConfig implements WebFluxConfigurer{
		public void configureViewResolvers(ViewResolverRegistry registry) {
			registry.viewResolver(new UrlBasedViewResolver());
		}
	}


}
