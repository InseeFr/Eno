package fr.insee.eno.ws.controller;

import fr.insee.eno.core.exceptions.DDIParsingException;
import fr.insee.eno.core.exceptions.LunaticSerializationException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.ws.service.DDIToLunaticService;
import fr.insee.eno.ws.service.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
public class DDIToLunaticController {

    @Autowired
    ParameterService parameterService;

    @Autowired
    DDIToLunaticService ddiToLunaticService;

    // TODO: use request param and define post/put endpoint to allow user to give DDI and parameters files
    @GetMapping(value = "ddi-to-lunatic", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    //@PostMapping(value = "ddi-to-lunatic", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    //@PutMapping(value = "ddi-to-lunatic", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> ddiToLunatic(
            //@RequestParam("ddi") MultipartFile ddiFile,
            //@RequestParam("parameter") MultipartFile parametersFile,
            )
            throws IOException, DDIParsingException, LunaticSerializationException {

        EnoParameters enoParameters = new EnoParameters(); //parameterService.parse(parametersFile.getInputStream());
        String result = ddiToLunaticService.transform(this.getClass().getClassLoader().getResourceAsStream("l20g2ba7.xml")/*ddiFile.getInputStream()*/, enoParameters);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hello.txt");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return Mono.just(ResponseEntity
                .ok().cacheControl(CacheControl.noCache())
                .headers(headers)
                .body(result));
    }

}
