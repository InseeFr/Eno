package fr.insee.eno.ws.service;

import fr.insee.eno.ws.dto.FileDto;
import fr.insee.eno.ws.exception.PoguesToLunaticException;
import fr.insee.pogues.conversion.JSONToXMLTranslator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static fr.insee.eno.ws.controller.utils.ControllerUtils.addStringToMultipartBody;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoguesToDDIService {

    private final EnoXmlClient enoXmlClient;

    private String poguesJsonToXml(String poguesJson) {
        try {
            JSONToXMLTranslator jsonToXmlTranslator = new JSONToXMLTranslator();
            return jsonToXmlTranslator.translate(poguesJson);
        } catch (Exception e) {
            log.error("Pogues json to xml conversion failed.");
            throw new PoguesToLunaticException(e);
        }
    }

    public FileDto convertPoguesFileToXml(MultipartFile poguesFile) throws IOException {
        return FileDto.builder()
                .name("pogues.xml")
                .content(poguesJsonToXml(new String(poguesFile.getBytes())).getBytes())
                .build();
    }

    public FileDto transform(MultipartFile poguesFile) throws IOException {
        String poguesXml = poguesJsonToXml(new String(poguesFile.getBytes()));
        return sendPoguesXmlToDDIRequest(poguesXml);
    }
    public FileDto transform(InputStream poguesStream) throws IOException {
        String poguesXml = poguesJsonToXml(new String(poguesStream.readAllBytes()));
        return sendPoguesXmlToDDIRequest(poguesXml);
    }

    public FileDto transformFromXml(MultipartFile poguesXmlFile) throws IOException {
        String poguesXmlContent = new String(poguesXmlFile.getBytes());
        return sendPoguesXmlToDDIRequest(poguesXmlContent);
    }

    private FileDto sendPoguesXmlToDDIRequest(String poguesXmlContent) {
        // Attach Pogues xml content in a multipart
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        addStringToMultipartBody(multipartBodyBuilder, poguesXmlContent, "pogues.xml", "in");
        // Send request to the legacy pogues xml to ddi endpoint
        URI uri = enoXmlClient.newUriBuilder().path("questionnaire/poguesxml-2-ddi").build().toUri();
        return enoXmlClient.sendPostRequest(uri, multipartBodyBuilder);
    }

}
