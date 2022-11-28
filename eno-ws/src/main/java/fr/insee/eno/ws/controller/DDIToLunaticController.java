package fr.insee.eno.ws.controller;

import fr.insee.eno.ws.service.DDIToLunaticService;
import fr.insee.eno.ws.service.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.SequenceInputStream;

@RestController
public class DDIToLunaticController {

    public final static String LUNATIC_OUT_FILE_NAME = "lunatic-form.json";

    @Autowired
    ParameterService parameterService;

    @Autowired
    DDIToLunaticService ddiToLunaticService;

    @PostMapping(value = "ddi-to-lunatic", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> ddiToLunatic(
            @RequestPart("ddiFile") Mono<FilePart> ddiFile,
            @RequestPart("parameterFile") Mono<FilePart> parametersFile) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+LUNATIC_OUT_FILE_NAME);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return parametersFile.flatMap(filePart -> filePart.content()
                        .map(dataBuffer -> dataBuffer.asInputStream(true))
                        .reduce(SequenceInputStream::new))
                .flatMap(inputStream -> parameterService.parse(inputStream))
                .flatMap(enoParameters ->
                        ddiFile.flatMap(filePart -> filePart.content()
                                        .map(dataBuffer -> dataBuffer.asInputStream(true))
                                        .reduce(SequenceInputStream::new))
                                .flatMap(inputStream -> ddiToLunaticService.transform(inputStream, enoParameters))
                                .map(result -> ResponseEntity
                                        .ok()
                                        .cacheControl(CacheControl.noCache())
                                        .headers(headers)
                                        .body(result)));
    }

}
