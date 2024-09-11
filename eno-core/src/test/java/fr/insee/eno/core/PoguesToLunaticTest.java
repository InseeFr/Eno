package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoguesToLunaticTest {

    @Test
    void simplestCase() throws PoguesDeserializationException {
        //
        String jsonPogues = "{\"id\": \"foo-id\"}";
        //
        Questionnaire lunaticQuestionnaire = PoguesToLunatic.transform(
                new ByteArrayInputStream(jsonPogues.getBytes()),
                EnoParameters.of(EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.PROCESS, Format.LUNATIC));
        //
        assertEquals("foo-id", lunaticQuestionnaire.getId());
    }

}
