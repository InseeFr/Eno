package fr.insee.eno.ws.service;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.PoguesToLunatic;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.ws.dto.FileDto;
import fr.insee.eno.ws.exception.PoguesToLunaticException;
import fr.insee.lunatic.model.flat.Questionnaire;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoguesToLunaticService extends LunaticGenerationService {

    private final PoguesToDDIService poguesToDDIService;

    /** Flag that determines if the service should use the direct Eno Java Pogues to Lunatic transformation.
     * If not, the service uses Eno Xml for Pogues to Lunatic, then uses the Eno Java DDI to Lunatic transformation.
     * */
    @Value("${eno.direct.pogues.lunatic}")
    private Boolean directPoguesToLunatic;

    @Override
    Questionnaire mainTransformation(InputStream poguesInputStream, EnoParameters enoParameters) throws Exception {
        // If Eno Java direct Pogues to Lunatic is enabled
        if (Boolean.TRUE.equals(directPoguesToLunatic)) {
            log.warn("Direct Pogues to Lunatic transformation is in work in progress state.");
            return PoguesToLunatic.fromInputStream(poguesInputStream).transform(enoParameters);
        }

        // Otherwise
        // The workflow is as follows:
        // - Eno Xml is called to generate the DDI from the Pogues questionnaire
        // - Eno Java uses both the given Pogues questionnaire and the generated DDI to generate the Lunatic questionnaire
        // Reminder: some features are described in Pogues but not in DDI.
        // So while Pogues mapping may be incomplete, we read the information for these features in the Pogues questionnaire.

        // Doing a string copy of the Pogues input since it will be consumed twice.
        String poguesContent = new String(poguesInputStream.readAllBytes());
        FileDto ddiFileDto = poguesToDDIService.transform(new ByteArrayInputStream(poguesContent.getBytes()));
        InputStream ddiStream = new ByteArrayInputStream(ddiFileDto.getContent());
        return PoguesDDIToLunatic
                .fromInputStreams(new ByteArrayInputStream(poguesContent.getBytes()), ddiStream)
                .transform(enoParameters);
    }

    @Override
    void handleException(Exception e) {
        throw new PoguesToLunaticException(e);
    }

}
