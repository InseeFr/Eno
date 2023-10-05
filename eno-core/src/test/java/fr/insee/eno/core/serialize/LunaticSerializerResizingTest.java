package fr.insee.eno.core.serialize;

import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.model.lunatic.LunaticResizingEntry;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairwiseEntry;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.ResizingType;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.List;
import java.util.Set;

class LunaticSerializerResizingTest {
    private Questionnaire lunaticQuestionnaire;

    @BeforeEach
    void init() {
        lunaticQuestionnaire = new Questionnaire();
        ResizingType resizingType = new ResizingType();
        lunaticQuestionnaire.setResizing(resizingType);

        List<Object> data = resizingType.getAny();
        data.add(new LunaticResizingEntry("loopVariable", "count(NB)", Set.of("NB")));
        data.add(new LunaticResizingEntry("loopVariable1", "count(NB1,PRENOM1)", Set.of("NB1", "PRENOM1")));
        data.add(new LunaticResizingPairwiseEntry("pairwise", List.of("count(NBP)", "count(PRENOMP)"), List.of("NBP", "PRENOMP")));
        data.add(new LunaticResizingPairwiseEntry("pairwise1", List.of("count(NBP1)", "count(PRENOMP1)"), List.of("NBP1", "PRENOMP1")));
    }

    @Test
    void whenTransformingToJsonResizingIsCorrect() throws LunaticSerializationException, JSONException {
        String questionnaireJson = LunaticSerializer.serializeToJson(lunaticQuestionnaire);

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
