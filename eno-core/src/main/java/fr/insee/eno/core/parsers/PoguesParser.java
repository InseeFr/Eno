package fr.insee.eno.core.parsers;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.pogues.conversion.JSONDeserializer;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

@Slf4j
public class PoguesParser {

    private PoguesParser() {}

    public static Questionnaire parse(URL poguesFileUrl) throws PoguesDeserializationException, URISyntaxException {
        JSONDeserializer poguesDeserializer = new JSONDeserializer();
        log.info("Parsing Pogues files from URL " + poguesFileUrl);
        try {
            Questionnaire poguesQuestionnaire = poguesDeserializer.deserialize(
                    Path.of(poguesFileUrl.toURI()).toString());
            log.info("Successfully parsed DDI from URL " + poguesFileUrl);
            return poguesQuestionnaire;
        } catch (JAXBException e) {
            throw new PoguesDeserializationException("Unable to parse Pogues file from URL " + poguesFileUrl, e);
        }
    }

}
