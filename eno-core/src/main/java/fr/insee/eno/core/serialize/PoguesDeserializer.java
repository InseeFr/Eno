package fr.insee.eno.core.serialize;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.pogues.conversion.JSONDeserializer;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
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
     * Deserializes the Pogues json file from given input stream.
     * @param poguesInputStream Input stream of a Pogues json questionnaire.
     * @return A Pogues-Model questionnaire.
     * @throws PoguesDeserializationException if deserialization fails.
     */
    public static Questionnaire deserialize(InputStream poguesInputStream) throws PoguesDeserializationException {
        JSONDeserializer poguesDeserializer = new JSONDeserializer();
        log.info("Deserializing Pogues json questionnaire from input stream given.");
        try {
            Questionnaire poguesQuestionnaire = poguesDeserializer.deserialize(poguesInputStream);
            log.info("Successfully deserialized Pogues questionnaire from input stream.");
            return poguesQuestionnaire;
        } catch (JAXBException e) {
            throw new PoguesDeserializationException("Unable to parse Pogues file  from input stream given.", e);
        }
    }

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
            Questionnaire poguesQuestionnaire = poguesDeserializer.deserialize(poguesFileUrl.openStream());
            log.info("Successfully deserialized Pogues questionnaire from URL {}.", poguesFileUrl);
            return poguesQuestionnaire;
        } catch (JAXBException e) {
            throw new PoguesDeserializationException("Unable to parse Pogues file from URL " + poguesFileUrl, e);
        } catch (IOException e) {
            throw new PoguesDeserializationException("IO exception occurred with file: " + poguesFileUrl, e);
        }
    }

}
