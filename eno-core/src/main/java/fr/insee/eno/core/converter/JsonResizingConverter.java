package fr.insee.eno.core.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.model.lunatic.LunaticResizingLoopVariable;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairWiseVariable;
import fr.insee.eno.core.output.LunaticSerializer;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.ResizingType;

import java.util.List;

public class JsonResizingConverter {
    private JsonResizingConverter() {
        throw new IllegalArgumentException("Utility class");
    }

    /**
     * convert a questionnaire to json string with resize variables included
     * @param lunaticQuestionnaire lunatic questionnaire
     * @return json string of the questionnaire with resize included
     * @throws LunaticSerializationException
     */
    public static String convertResizingToJsonLunatic(Questionnaire lunaticQuestionnaire) throws LunaticSerializationException {
        String lunaticJson = LunaticSerializer.serializeToJson(lunaticQuestionnaire);
        ResizingType resizingType = lunaticQuestionnaire.getResizing();
        if(resizingType == null || resizingType.getAny().isEmpty()) {
            return lunaticJson;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode questionnaireNode = (ObjectNode) mapper.readTree(lunaticJson);
            questionnaireNode.get("resizing");
            List<Object> resizingVariables = lunaticQuestionnaire.getResizing().getAny();

            ObjectNode resizingsJson = mapper.createObjectNode();
            for(Object variableObject: resizingVariables) {
                if(variableObject instanceof LunaticResizingPairWiseVariable variable) {
                    resizingsJson.set(variable.getName(), buildPairwiseResizingVariable(variable));
                }

                if(variableObject instanceof LunaticResizingLoopVariable variable) {
                    resizingsJson.set(variable.getName(), buildResizingLoopVariable(variable));
                }
            }
            questionnaireNode.set("resizing", resizingsJson);

            return mapper.writeValueAsString(questionnaireNode);
        } catch(JsonProcessingException ex) {
            throw new LunaticSerializationException(ex.getMessage());
        }
    }

    /**
     * Build variable for pairwise resize
     * @param variable variable to resize
     * @return variable node to resize
     */
    public static JsonNode buildPairwiseResizingVariable(LunaticResizingPairWiseVariable variable) {
        ArrayNode linksVariablesBuilder = JsonNodeFactory.instance.arrayNode();
        variable.getLinksVariables().forEach(linksVariablesBuilder::add);

        ArrayNode sizeForLinksVariablesBuilder = JsonNodeFactory.instance.arrayNode();
        variable.getSizeForLinksVariables().forEach(sizeForLinksVariablesBuilder::add);

        ObjectNode pairwiseVariableNode = JsonNodeFactory.instance.objectNode();

        pairwiseVariableNode.set("linksVariables", linksVariablesBuilder);
        pairwiseVariableNode.set("sizeForLinksVariables", sizeForLinksVariablesBuilder);

        return pairwiseVariableNode;
    }

    /**
     * Build variable for loop resize
     * @param variable variable to resize
     * @return variable node to resize
     */
    public static JsonNode buildResizingLoopVariable(LunaticResizingLoopVariable variable) {
        ArrayNode variablesArray = JsonNodeFactory.instance.arrayNode();
        variable.getVariables().forEach(variablesArray::add);

        ObjectNode loopVariableNode = JsonNodeFactory.instance.objectNode();

        ValueNode sizeNodeValue = JsonNodeFactory.instance.textNode(variable.getSize());
        loopVariableNode.set("size",  sizeNodeValue);
        loopVariableNode.set("variables", variablesArray);

        return loopVariableNode;
    }
}
