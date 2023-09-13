package fr.insee.eno.core.serialize;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PoguesDeserializerTest {

    @Test
    void poguesParserTest() throws URISyntaxException, PoguesDeserializationException {
        //
        URL testPoguesFileUrl = this.getClass().getClassLoader().getResource("end-to-end/pogues/pogues-l20g2ba7.json");
        assert testPoguesFileUrl != null;
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(testPoguesFileUrl);

        //
        assertNotNull(poguesQuestionnaire);
        assertEquals("l20g2ba7", poguesQuestionnaire.getId());
    }
    
}
