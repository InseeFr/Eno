package fr.insee.eno.core.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.model.lunatic.*;
import fr.insee.lunatic.conversion.JsonSerializer;
import fr.insee.lunatic.exception.SerializationException;
import fr.insee.lunatic.model.flat.CleaningType;
import fr.insee.lunatic.model.flat.MissingType;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.ResizingType;

import java.util.List;

public class LunaticSerializer {
    private LunaticSerializer() {
        throw new IllegalArgumentException("Utility class");
    }

    /**
     * Serialize the given questionnaire in the json format.
     * @param lunaticQuestionnaire Lunatic questionnaire object.
     * @return Questionnaire serialized as json string.
     * @throws LunaticSerializationException if serialization fails.
     */
    public static String serializeToJson(Questionnaire lunaticQuestionnaire) throws LunaticSerializationException {
        String lunaticJson = rawSerialization(lunaticQuestionnaire);
        return extendedSerialization(lunaticJson, lunaticQuestionnaire);
    }

    /** Return the json string produced by Lunatic-Model json serializer. */
    private static String rawSerialization(Questionnaire lunaticQuestionnaire) {
        JsonSerializer jsonSerializer = new JsonSerializer();
        try {
            return jsonSerializer.serialize(lunaticQuestionnaire);
        } catch (SerializationException e) {
            throw new LunaticSerializationException("Lunatic questionnaire given cannot be serialized.", e);
        }
    }

    /* The following part should be supported by Lunatic-Model: */

    /**
     * convert a questionnaire to json string with resize variables included
     * @param lunaticQuestionnaire lunatic questionnaire
     * @return json string of the questionnaire with resize included
     * @throws LunaticSerializationException serialization exception
     */
    public static String extendedSerialization(String lunaticJson, Questionnaire lunaticQuestionnaire)
            throws LunaticSerializationException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectNode questionnaireNode = (ObjectNode) mapper.readTree(lunaticJson);

            CleaningType cleaningType = lunaticQuestionnaire.getCleaning();
            if(cleaningType != null && !cleaningType.getAny().isEmpty()) {
                questionnaireNode.set("cleaning", createCleaningObject(lunaticQuestionnaire, mapper));
            }

            MissingType missingType = lunaticQuestionnaire.getMissingBlock();
            if(missingType != null && !missingType.getAny().isEmpty()) {
                questionnaireNode.set("missingBlock", createMissingBlocks(lunaticQuestionnaire, mapper));
            }

            ResizingType resizingType = lunaticQuestionnaire.getResizing();
            if(resizingType != null && !resizingType.getAny().isEmpty()) {
                questionnaireNode.set("resizing", createResizingObject(lunaticQuestionnaire, mapper));
            }
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

    /**
     * Create resizing node for lunatic questionnaire
     * @param lunaticQuestionnaire questionnaire lunatic
     * @param mapper object mapper
     * @return the resizing node
     */
    private static ObjectNode createResizingObject(Questionnaire lunaticQuestionnaire, ObjectMapper mapper) {
        List<Object> resizingList = lunaticQuestionnaire.getResizing().getAny();

        ObjectNode jsonResizingNode = mapper.createObjectNode();
        for(Object resizingObject: resizingList) {
            if(resizingObject instanceof LunaticResizingPairwiseEntry variable) {
                jsonResizingNode.set(variable.getName(), resizingPairwiseEntryToJsonNode(variable));
            }
            if(resizingObject instanceof LunaticResizingEntry variable) {
                jsonResizingNode.set(variable.getName(), resizingEntryToJsonNode(variable));
            }
        }
        return jsonResizingNode;
    }

    /**
     * Build entry for resizing entry (concerns loops and dynamic tables).
     * @param resizingEntry Resizing entry object.
     * @return Resizing entry as a json node.
     */
    private static JsonNode resizingEntryToJsonNode(LunaticResizingEntry resizingEntry) {
        ArrayNode variablesArray = JsonNodeFactory.instance.arrayNode();
        resizingEntry.getVariables().forEach(variablesArray::add);

        ObjectNode loopVariableNode = JsonNodeFactory.instance.objectNode();

        ValueNode sizeNodeValue = JsonNodeFactory.instance.textNode(resizingEntry.getSize());
        loopVariableNode.set("size",  sizeNodeValue);
        loopVariableNode.set("variables", variablesArray);

        return loopVariableNode;
    }

    /**
     * Build variable for pairwise resizing entry.
     * @param resizingPairwiseEntry Pairwise-case resizing entry object.
     * @return Resizing entry as a json node.
     */
    private static JsonNode resizingPairwiseEntryToJsonNode(LunaticResizingPairwiseEntry resizingPairwiseEntry) {
        ArrayNode linksVariablesBuilder = JsonNodeFactory.instance.arrayNode();
        resizingPairwiseEntry.getLinksVariables().forEach(linksVariablesBuilder::add);

        ArrayNode sizeForLinksVariablesBuilder = JsonNodeFactory.instance.arrayNode();
        resizingPairwiseEntry.getSizeForLinksVariables().forEach(sizeForLinksVariablesBuilder::add);

        ObjectNode pairwiseVariableNode = JsonNodeFactory.instance.objectNode();

        pairwiseVariableNode.set("linksVariables", linksVariablesBuilder);
        pairwiseVariableNode.set("sizeForLinksVariables", sizeForLinksVariablesBuilder);

        return pairwiseVariableNode;
    }

}
