package fr.insee.eno.core.serialize;

import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.VariableType;
import fr.insee.lunatic.model.flat.VariableTypeEnum;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class LunaticSerializerVariablesTest {

    @Test
    void serializeScalarVariable() throws JSONException {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        VariableType variableType = new VariableType();
        variableType.setName("FOO_VARIABLE");
        variableType.setVariableType(VariableTypeEnum.COLLECTED);
        lunaticQuestionnaire.getVariables().add(variableType);
        //
        String result = LunaticSerializer.serializeToJson(lunaticQuestionnaire);
        //
        String expectedJson = """
                {
                  "variables": [
                    {
                      "variableType": "COLLECTED",
                      "name": "FOO_VARIABLE",
                      "values": {
                        "PREVIOUS": null,
                        "COLLECTED": null,
                        "FORCED": null,
                        "EDITED": null,
                        "INPUTED": null
                      }
                    }
                  ]
                }""";
        JSONAssert.assertEquals(expectedJson, result, JSONCompareMode.STRICT);
    }

}
