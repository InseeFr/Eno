package fr.insee.eno.ws.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration { // TODO: prod config using spring.profiles.active?

    @Bean
    public OpenAPI customOpenAPI() {
        String enoVersion = "(todo) 3.x"; //TODO: get eno & Lunatic version in code
        String lunaticModelVersion = "(todo) 2.3.x";
        return new OpenAPI()
                .info(new Info()
                        .title("Eno Web Service")
                        .description(
                                "<h2>Generator using :</h2>" +
                                        "<style>" +
                                        "  .cell{" +
                                        "    border: black 2px solid; " +
                                        "    text-align: center; " +
                                        "    font-weight: bold; " +
                                        "    font-size: 1.5em;} " +
                                        "  .version{color:darkred}" +
                                        "</style>" +
                                        "<table style=\"width:40%\">" +
                                        "  <tr>" +
                                        "    <td class=\"cell\">Eno version</td>" +
                                        "    <td class=\"cell version\">"+enoVersion+"</td>" +
                                        "  </tr>" +
                                        "  <tr>" +
                                        "    <td class=\"cell\">Lunatic Model version</td>" +
                                        "    <td class=\"cell version\">"+lunaticModelVersion+"</td>" +
                                        "  </tr>" +
                                        "</table>")
                        .version("(todo) Eno-WS v2.x here") //TODO
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                );
    }

}
