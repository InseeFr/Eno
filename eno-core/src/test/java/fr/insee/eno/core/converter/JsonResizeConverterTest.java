package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.model.lunatic.LunaticResizingLoopVariable;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairWiseVariable;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.ResizingType;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.List;

class JsonResizeConverterTest {
    private Questionnaire lunaticQuestionnaire;

    @BeforeEach
    void init() {
        lunaticQuestionnaire = new Questionnaire();
        ResizingType resizingType = new ResizingType();
        lunaticQuestionnaire.setResizing(resizingType);

        List<Object> data = resizingType.getAny();
        data.add(new LunaticResizingLoopVariable("loopVariable", "count(NB)", List.of("NB")));
        data.add(new LunaticResizingLoopVariable("loopVariable1", "count(NB1,PRENOM1)", List.of("NB1", "PRENOM1")));
        data.add(new LunaticResizingPairWiseVariable("pairwise", List.of("count(NBP)", "count(PRENOMP)"), List.of("NBP", "PRENOMP")));
        data.add(new LunaticResizingPairWiseVariable("pairwise1", List.of("count(NBP1)", "count(PRENOMP1)"), List.of("NBP1", "PRENOMP1")));
    }

    @Test
    void whenTransformingToJsonResizingIsCorrect() throws LunaticSerializationException, JSONException {
        String questionnaireJson = JsonLunaticConverter.convert(lunaticQuestionnaire);

        String expectedJson = """
        {
          "resizing": {
            "loopVariable": { "size": "count(NB)", "variables": ["NB"] },
            "loopVariable1": {
              "size": "count(NB1,PRENOM1)",
              "variables": ["NB1", "PRENOM1"]
            },
            "pairwise": {
              "linksVariables": ["NBP", "PRENOMP"],
              "sizeForLinksVariables": ["count(NBP)", "count(PRENOMP)"]
            },
            "pairwise1": {
              "linksVariables": ["NBP1", "PRENOMP1"],
              "sizeForLinksVariables": ["count(NBP1)", "count(PRENOMP1)"]
            }
          }
        }     
        """;
        JSONAssert.assertEquals(expectedJson, questionnaireJson, JSONCompareMode.STRICT);
    }
}
