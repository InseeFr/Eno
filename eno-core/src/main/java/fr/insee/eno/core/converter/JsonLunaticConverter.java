package fr.insee.eno.core.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
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

            MissingType missingType = lunaticQuestionnaire.getMissingBlock();
            if(missingType != null && !missingType.getAny().isEmpty()) {
                questionnaireNode.set("missingBlock", createMissingBlocks(lunaticQuestionnaire, mapper));
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