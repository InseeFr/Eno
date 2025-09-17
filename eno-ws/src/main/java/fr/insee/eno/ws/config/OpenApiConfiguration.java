package fr.insee.eno.ws.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;

@Configuration
public class OpenApiConfiguration {

	@Value("${fr.insee.enows.api.scheme}")
	private String apiScheme;
	
	@Value("${fr.insee.enows.api.host}")
	private String apiHost;
		
	@Value("${fr.insee.enows.enocore.version}")
	private String enoVersion;
	
	@Value("${fr.insee.enows.lunatic.model.version}")
	private String lunaticModelVersion;
	
	@Value("${fr.insee.enows.version}")
	private String projectVersion;

	@Autowired
	private BuildProperties buildProperties;

	@Bean
	protected OpenAPI noAuthOpenAPI() {
		return new OpenAPI().info(
			new Info()
					.title(buildProperties.getName())
					.description(String.format("""
                                        <h2>Generator using :</h2>
                                        <div><b>Eno version : </b><i>%s</i></div>
                                        <div><b>Lunatic-Model version : </b><i>%s</i></div>
                                        """,enoVersion,lunaticModelVersion))
					.version(buildProperties.getVersion())
	); }

	public OpenApiConfiguration(MappingJackson2HttpMessageConverter converter) {
		var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
		supportedMediaTypes.add(new MediaType("application", "octet-stream"));
		converter.setSupportedMediaTypes(supportedMediaTypes);
	}
}
