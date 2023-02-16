package fr.insee.eno.ws.config;

//import fr.insee.ddi.model.DDIMetadata;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:version.properties")
public class OpenApiConfiguration {

    @Value("${eno.legacy.ws.url}")
    private String enoLegacyUrl;
    @Value("${version.eno}")
    private String enoVersion;
    @Value("${version.pogues.model}")
    private String poguesModelVersion;
    @Value("${version.lunatic.model}")
    private String lunaticModelVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Eno Web Service")
                        .description(
                                "<h2>Generator using:</h2>" +
                                        "<style>" +
                                        "  .cell{" +
                                        "    border: black 2px solid; " +
                                        "    text-align: center; " +
                                        "    font-weight: bold; " +
                                        "    font-size: 1.5em;} " +
                                        "  .version{color:darkred}" +
                                        "</style>" +
                                        "<table style=\"width:40%\">" +
                                        descriptionEntry("Eno Java version", enoVersion) +
                                        descriptionEntry("Eno XML web service", htmlLink(enoLegacyUrl)) +
                                        descriptionEntry("DDI version", /*DDIMetadata.MODEL_VERSION*/ "3.3") +
                                        descriptionEntry("Pogues Model version", poguesModelVersion) +
                                        descriptionEntry("Lunatic Model version", lunaticModelVersion) +
                                        "</table>")
                        .version(enoVersion)
                        .license(new License().name("Apache 2.0").url("https://springdoc.org"))
                );
    }

    private static String descriptionEntry(String description, String version) {
        return "<tr>" +
                        "  <td class=\"cell\">"+description+"</td>" +
                        "  <td class=\"cell version\">"+version+"</td>" +
                        "</tr>";
    }
    private static String htmlLink(String url) {
        return "<a href=\""+url+"\">"+url+"</a>";
    }

}
