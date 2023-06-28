package fr.insee.eno.core.parsers;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PoguesParserTest {

    @Test
    void poguesParserTest() throws URISyntaxException, PoguesDeserializationException {
        //
        URL testPoguesFileUrl = this.getClass().getClassLoader().getResource("end-to-end/pogues/pogues-l20g2ba7.json");
        assert testPoguesFileUrl != null;
        Questionnaire poguesQuestionnaire = PoguesParser.parse(testPoguesFileUrl);

        //
        assertNotNull(poguesQuestionnaire);
        assertEquals("l20g2ba7", poguesQuestionnaire.getId());
    }
    
}
