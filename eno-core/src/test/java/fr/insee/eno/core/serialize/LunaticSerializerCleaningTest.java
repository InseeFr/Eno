package fr.insee.eno.core.serialize;

import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.model.lunatic.CleaningConcernedVariable;
import fr.insee.eno.core.model.lunatic.CleaningVariable;
import fr.insee.lunatic.model.flat.CleaningType;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.List;

class LunaticSerializerCleaningTest {
    private Questionnaire lunaticQuestionnaire;


    @BeforeEach
    void init() {
        lunaticQuestionnaire = new Questionnaire();
        CleaningType cleaningType = new CleaningType();
        lunaticQuestionnaire.setCleaning(cleaningType);

        CleaningConcernedVariable concernedVariable1 = new CleaningConcernedVariable("CONCERNED_VARIABLE1", "filter1");
        CleaningConcernedVariable concernedVariable2 = new CleaningConcernedVariable("CONCERNED_VARIABLE2", "filter2");
        CleaningConcernedVariable concernedVariable3 = new CleaningConcernedVariable("CONCERNED_VARIABLE3", "filter3");
        CleaningConcernedVariable concernedVariable4 = new CleaningConcernedVariable("CONCERNED_VARIABLE4", "filter4");
        CleaningConcernedVariable concernedVariable5 = new CleaningConcernedVariable("CONCERNED_VARIABLE5", "filter5");
        CleaningConcernedVariable concernedVariable6 = new CleaningConcernedVariable("CONCERNED_VARIABLE6", "filter6");

        List<Object> data = cleaningType.getAny();
        data.add(new CleaningVariable("SOME_VARIABLE1", List.of(concernedVariable1, concernedVariable2)));
        data.add(new CleaningVariable("SOME_VARIABLE2", List.of(concernedVariable3, concernedVariable4)));
        data.add(new CleaningVariable("SOME_VARIABLE3", List.of(concernedVariable5, concernedVariable6)));

        cleaningType.getAny().addAll(data);
    }

    @Test
    void whenTransformingToJsonCleaningBlockIsCorrect() throws LunaticSerializationException, JSONException {
        String questionnaireJson = LunaticSerializer.serializeToJson(lunaticQuestionnaire);
        String expectedJson = """
        {
          "cleaning": {
            "SOME_VARIABLE1": {
              "CONCERNED_VARIABLE1": "filter1",
              "CONCERNED_VARIABLE2": "filter2"
            },
            "SOME_VARIABLE2": {
              "CONCERNED_VARIABLE3": "filter3",
              "CONCERNED_VARIABLE4": "filter4"
            },
            "SOME_VARIABLE3": {
              "CONCERNED_VARIABLE5": "filter5",
              "CONCERNED_VARIABLE6": "filter6"
            }
          }
        }""";

        JSONAssert.assertEquals(expectedJson,questionnaireJson, JSONCompareMode.STRICT);
    }
}