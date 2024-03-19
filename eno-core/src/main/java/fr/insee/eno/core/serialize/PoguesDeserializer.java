package fr.insee.eno.core.serialize;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.pogues.conversion.JSONDeserializer;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Wrapper class for Pogues-Model deserializer.
 */
@Slf4j
public class PoguesDeserializer {

    private PoguesDeserializer() {}

    /**
     * Deserializes the Pogues json file from given URL.
     * @param poguesFileUrl Url of a Pogues json questionnaire file.
     * @return A Pogues-Model questionnaire.
     * @throws PoguesDeserializationException if deserialization fails.
     * @throws URISyntaxException if the given URL cannot be converted to an URI.
     */
    public static Questionnaire deserialize(URL poguesFileUrl) throws PoguesDeserializationException, URISyntaxException {
        JSONDeserializer poguesDeserializer = new JSONDeserializer();
        log.info("Deserializing Pogues json questionnaire from URL {}.", poguesFileUrl);
        try {
            Questionnaire poguesQuestionnaire = poguesDeserializer.deserialize(
                    Path.of(poguesFileUrl.toURI()).toString());
            log.info("Successfully deserialized Pogues questionnaire from URL {}.", poguesFileUrl);
            return poguesQuestionnaire;
        } catch (JAXBException e) {
            throw new PoguesDeserializationException("Unable to parse Pogues file from URL " + poguesFileUrl, e);
        }
    }

}
