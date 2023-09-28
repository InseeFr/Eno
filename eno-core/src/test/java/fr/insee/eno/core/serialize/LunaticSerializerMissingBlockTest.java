package fr.insee.eno.core.serialize;

import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.model.lunatic.MissingBlock;
import fr.insee.lunatic.model.flat.MissingType;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.List;

class LunaticSerializerMissingBlockTest {
    private Questionnaire lunaticQuestionnaire;


    @BeforeEach
    void init() {
        lunaticQuestionnaire = new Questionnaire();
        MissingType missingType = new MissingType();
        lunaticQuestionnaire.setMissingBlock(missingType);

        List<Object> data = missingType.getAny();
        data.add(new MissingBlock("SOME_QUESTION_MISSING", List.of("SOME_QUESTION")));
        data.add(new MissingBlock("SOME_QUESTION", List.of("SOME_QUESTION_MISSING")));
        data.add(new MissingBlock("SOME_TABLE_MISSING", List.of("TABLE_VAR1", "TABLE_VAR2", "TABLE_VAR3")));
        data.add(new MissingBlock("TABLE_VAR1", List.of("SOME_TABLE_MISSING")));
        data.add(new MissingBlock("TABLE_VAR2", List.of("SOME_TABLE_MISSING")));
        data.add(new MissingBlock("TABLE_VAR3", List.of("SOME_TABLE_MISSING")));
    }

    @Test
    void whenTransformingToJsonMissingBlockIsCorrect() throws LunaticSerializationException, JSONException {
        String questionnaireJson = LunaticSerializer.serializeToJson(lunaticQuestionnaire);

        String expectedJson = """
            {
               "missingBlock":{
                  "SOME_QUESTION_MISSING":[
                     "SOME_QUESTION"
                  ],
                  "SOME_QUESTION":[
                     "SOME_QUESTION_MISSING"
                  ],
                  "SOME_TABLE_MISSING":[
                     "TABLE_VAR1",
                     "TABLE_VAR2",
                     "TABLE_VAR3"
                  ],
                  "TABLE_VAR1":[
                     "SOME_TABLE_MISSING"
                  ],
                  "TABLE_VAR2":[
                     "SOME_TABLE_MISSING"
                  ],
                  "TABLE_VAR3":[
                     "SOME_TABLE_MISSING"
                  ]
               }
            }""";

        JSONAssert.assertEquals(expectedJson,questionnaireJson, JSONCompareMode.STRICT);
    }
}
