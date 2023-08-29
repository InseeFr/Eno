package fr.insee.eno.core.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.model.lunatic.CleaningConcernedVariable;
import fr.insee.eno.core.model.lunatic.CleaningVariable;
import fr.insee.eno.core.model.lunatic.MissingBlock;
import fr.insee.eno.core.output.LunaticSerializer;
import fr.insee.lunatic.model.flat.CleaningType;
import fr.insee.lunatic.model.flat.MissingType;
import fr.insee.lunatic.model.flat.Questionnaire;

public class JsonLunaticConverter {

    private JsonLunaticConverter() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Integrate in lunatic questionnaire the correct format for cleaning/missing blocks
     * @param lunaticQuestionnaire lunatic questionnaire
     * @return json lunatic with cleaning/missing blocks included
     * @throws LunaticSerializationException exception raised when serializing lunatic questionnaire
     */
    public static String convert(Questionnaire lunaticQuestionnaire) throws LunaticSerializationException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String lunaticJson = LunaticSerializer.serializeToJson(lunaticQuestionnaire);
            ObjectNode questionnaireNode = (ObjectNode) mapper.readTree(lunaticJson);

            CleaningType cleaningType = lunaticQuestionnaire.getCleaning();
            if(cleaningType != null && !cleaningType.getAny().isEmpty()) {
                questionnaireNode.set("cleaning", createCleaningObject(lunaticQuestionnaire, mapper));
            }
            MissingType missingType = lunaticQuestionnaire.getMissingBlock();
            if(missingType != null && !missingType.getAny().isEmpty()) {
                questionnaireNode.set("missingBlock", createMissingBlocks(lunaticQuestionnaire, mapper));
            }
import fr.insee.eno.core.model.lunatic.LunaticResizingLoopVariable;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairWiseVariable;
import fr.insee.eno.core.output.LunaticSerializer;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.ResizingType;

import java.util.List;

public class JsonLunaticConverter {
    private JsonLunaticConverter() {
        throw new IllegalArgumentException("Utility class");
    }

    /**
     * convert a questionnaire to json string with resize variables included
     * @param lunaticQuestionnaire lunatic questionnaire
     * @return json string of the questionnaire with resize included
     * @throws LunaticSerializationException serialization exception
     */
    public static String convert(Questionnaire lunaticQuestionnaire) throws LunaticSerializationException {
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
     * Create missing blocks json node
     * @param lunaticQuestionnaire lunatic questionnaire
     * @return the json node containing all missing blocks in correct format
     */
    private static ObjectNode createMissingBlocks(Questionnaire lunaticQuestionnaire, ObjectMapper mapper) {
        ObjectNode missingBlocksJson = mapper.createObjectNode();
        lunaticQuestionnaire.getMissingBlock().getAny().stream()
                .map(MissingBlock.class::cast)
                .forEach(missingBlock -> {
                    ArrayNode namesArray = JsonNodeFactory.instance.arrayNode();
                    missingBlock.getNames().forEach(namesArray::add);
                    missingBlocksJson.set(missingBlock.getMissingName(), namesArray);
                });
        return missingBlocksJson;
    }

    /**
     * Create cleaning json node
     * @param lunaticQuestionnaire lunatic questionnaire
     * @return the json node containing the cleaning object in correct format
     */
    private static ObjectNode createCleaningObject(Questionnaire lunaticQuestionnaire, ObjectMapper mapper) {
        ObjectNode cleaningBlocksNode = mapper.createObjectNode();
        lunaticQuestionnaire.getCleaning().getAny().stream()
                .map(CleaningVariable.class::cast)
                .forEach(cleaningVariable -> {
                    ObjectNode concernedVariablesNode = JsonNodeFactory.instance.objectNode();
                    for(CleaningConcernedVariable concernedVariable : cleaningVariable.getConcernedVariables()) {
                        ValueNode filterNode = JsonNodeFactory.instance.textNode(concernedVariable.getFilter());
                        concernedVariablesNode.set(concernedVariable.getName(), filterNode);
                    }
                    cleaningBlocksNode.set(cleaningVariable.getName(), concernedVariablesNode);
                });
        return cleaningBlocksNode;
    }
}
     * Build variable for pairwise resize
     * @param variable variable to resize
     * @return variable node to resize
     */
    private static JsonNode buildPairwiseResizingVariable(LunaticResizingPairWiseVariable variable) {
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
    private static JsonNode buildResizingLoopVariable(LunaticResizingLoopVariable variable) {
        ArrayNode variablesArray = JsonNodeFactory.instance.arrayNode();
        variable.getVariables().forEach(variablesArray::add);

        ObjectNode loopVariableNode = JsonNodeFactory.instance.objectNode();

        ValueNode sizeNodeValue = JsonNodeFactory.instance.textNode(variable.getSize());
        loopVariableNode.set("size",  sizeNodeValue);
        loopVariableNode.set("variables", variablesArray);

        return loopVariableNode;
    }
}
