package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoguesToLunaticTest {

    @Test
    void simplestCase() throws PoguesDeserializationException {
        //
        String jsonPogues = "{\"id\": \"foo-id\"}";
        //
        Questionnaire lunaticQuestionnaire = new PoguesToLunatic().transform(
                new ByteArrayInputStream(jsonPogues.getBytes()),
                EnoParameters.of(EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.PROCESS, Format.LUNATIC));
        //
        assertEquals("foo-id", lunaticQuestionnaire.getId());
    }

    @ParameterizedTest
    @CsvSource({
            "integration/pogues/pogues-simple.json,lmyoceix",
            "functional/pogues/pogues-l20g2ba7.json,l20g2ba7",
    })
    void testIdMapping(String relativePath, String expectedId) throws PoguesDeserializationException {
        //
        Questionnaire lunaticQuestionnaire = new PoguesToLunatic().transform(
                this.getClass().getClassLoader().getResourceAsStream(relativePath),
                EnoParameters.of(EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.PROCESS, Format.LUNATIC));
        //
        assertEquals(expectedId, lunaticQuestionnaire.getId());
    }

}
