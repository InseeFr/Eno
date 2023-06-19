package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.model.lunatic.LunaticResizingLoopVariable;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairWiseVariable;
import fr.insee.eno.core.output.LunaticSerializer;
import fr.insee.lunatic.model.flat.Questionnaire;

import javax.json.*;
import java.io.StringReader;
import java.util.List;

public class JsonResizingConverter {
    private JsonResizingConverter() {
        throw new IllegalArgumentException("Utility class");
    }

    public static String convertResizingToJsonLunatic(Questionnaire lunaticQuestionnaire) throws LunaticSerializationException {
        String lunaticJson = LunaticSerializer.serializeToJson(lunaticQuestionnaire);
        JsonReader jsonReader = Json.createReader(new StringReader(lunaticJson));
        JsonObject questionnaire = jsonReader.readObject();

        List<Object> resizingVariables = lunaticQuestionnaire.getResizing().getAny();
        JsonObjectBuilder resizingsJson = Json.createObjectBuilder();

        for(Object variableObject: resizingVariables) {
            if(variableObject instanceof LunaticResizingPairWiseVariable variable) {
                resizingsJson.add(variable.getName(), buildPairwiseResizingVariable(variable));
            }

            if(variableObject instanceof LunaticResizingLoopVariable variable) {
                resizingsJson.add(variable.getName(), buildResizingLoopVariable(variable));
            }
        }
        questionnaire.put("resizing", resizingsJson.build());
        jsonReader.close();
        return questionnaire.toString();
    }

    public static JsonObject buildPairwiseResizingVariable(LunaticResizingPairWiseVariable variable) {
        JsonArrayBuilder linksVariablesBuilder = Json.createArrayBuilder();
        variable.getLinksVariables().forEach(linksVariablesBuilder::add);

        JsonArrayBuilder sizeForLinksVariablesBuilder = Json.createArrayBuilder();
        variable.getSizeForLinksVariables().forEach(sizeForLinksVariablesBuilder::add);

        return Json.createObjectBuilder()
                .add("linksVariables", linksVariablesBuilder.build())
                .add("sizeForLinksVariables", sizeForLinksVariablesBuilder.build())
                .build();
    }

    public static JsonObject buildResizingLoopVariable(LunaticResizingLoopVariable variable) {
        JsonArrayBuilder variablesBuilder = Json.createArrayBuilder();
        variable.getVariables().forEach(variablesBuilder::add);

        return Json.createObjectBuilder()
                .add("size", variable.getSize())
                .add("variables",variablesBuilder.build())
                .build();
    }
}
