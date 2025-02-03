package fr.insee.eno.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.pogues.model.Questionnaire;
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class PoguesDDIToEnoTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "kx0a2hn8",
            "kzy5kbtl",
            "l5v3spn0",
            "l7j0wwqx",
            "l8x6fhtd",
            "l20g2ba7",
            "ldodefpq",
            "lhpz68wp",
            "li49zxju",
            "ljr4jm9a",
            "lmyjrqbb",
            "lqnje8yr",
            "lx4qzdty",
    })
    void nonRegressionTest(String id) throws ParsingException, JsonProcessingException, JSONException {

        // Given Pogues & DDI inputs and Eno parameters
        // (for questionnaires that does not contain features described in Pogues and not in DDI)
        ClassLoader classLoader = this.getClass().getClassLoader();
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(classLoader.getResourceAsStream(
                "functional/ddi/ddi-" + id + ".xml"));
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(classLoader.getResourceAsStream(
                "functional/pogues/pogues-" + id + ".json"));
        EnoParameters enoParameters = EnoParameters.of(Context.DEFAULT, ModeParameter.PROCESS, Format.LUNATIC);

        // When mapping from DDI and from Pogues + DDI
        EnoQuestionnaire fromDDI = DDIToEno.fromObject(ddiQuestionnaire)
                .transform(enoParameters);
        EnoQuestionnaire fromPoguesDDI = PoguesDDIToEno.fromObjects(poguesQuestionnaire, ddiQuestionnaire)
                .transform(enoParameters);
        removePoguesSpecificProperties(fromPoguesDDI);

        // Then the resulting Eno questionnaire should be identical
        String serialized1 = new ObjectMapper().writeValueAsString(fromDDI);
        String serialized2 = new ObjectMapper().writeValueAsString(fromPoguesDDI);
        JSONAssert.assertEquals(serialized1, serialized2, JSONCompareMode.STRICT);
    }

    /**
     * Removes the properties that are specific to Pogues to ease the testing of
     * "Pogues + DDI" vs. "DDI only".
     * @param enoQuestionnaire Eno questionnaire mapped from Pogues + DDI
     */
    private static void removePoguesSpecificProperties(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.setAgency(null);
        enoQuestionnaire.setExpressionLanguage(null);
        enoQuestionnaire.setFilterMode(null);
    }
}
