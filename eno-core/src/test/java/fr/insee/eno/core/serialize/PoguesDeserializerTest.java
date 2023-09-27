package fr.insee.eno.core.serialize;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class PoguesDeserializerTest {

    @Test
    void deserialize_simpleQuestionnaire() throws PoguesDeserializationException, URISyntaxException {
        //
        URL testPoguesFileUrl = this.getClass().getClassLoader().getResource(
                "integration/pogues/pogues-simple.json");
        assert testPoguesFileUrl != null;
        //
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(testPoguesFileUrl);
        //
        assertNotNull(poguesQuestionnaire);
        assertFalse(poguesQuestionnaire.getChild().isEmpty());
    }

    @Test
    void deserialize_largeQuestionnaire() throws URISyntaxException, PoguesDeserializationException {
        //
        URL testPoguesFileUrl = this.getClass().getClassLoader().getResource(
                "end-to-end/pogues/pogues-l20g2ba7.json");
        assert testPoguesFileUrl != null;
        //
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(testPoguesFileUrl);
        //
        assertNotNull(poguesQuestionnaire);
        assertEquals("l20g2ba7", poguesQuestionnaire.getId());
    }
    
}
