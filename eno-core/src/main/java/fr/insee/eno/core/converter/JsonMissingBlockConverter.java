package fr.insee.eno.core.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.model.lunatic.MissingBlock;
import fr.insee.eno.core.output.LunaticSerializer;
import fr.insee.lunatic.model.flat.MissingType;
import fr.insee.lunatic.model.flat.Questionnaire;

public class JsonMissingBlockConverter {

    private final ObjectMapper mapper;
    public JsonMissingBlockConverter() {
        this.mapper = new ObjectMapper();
    }

    /**
     * Integrate in lunatic questionnaire the correct format for missing blocks
     * @param lunaticQuestionnaire lunatic questionnaire
     * @return json lunatic with missing blocks included
     * @throws LunaticSerializationException exception raised when serializing lunatic questionnaire
     */
    public String convert(Questionnaire lunaticQuestionnaire) throws LunaticSerializationException {
        try {
            String lunaticJson = LunaticSerializer.serializeToJson(lunaticQuestionnaire);
            ObjectNode questionnaireNode = (ObjectNode) mapper.readTree(lunaticJson);

            MissingType missingType = lunaticQuestionnaire.getMissingBlock();
            if(missingType != null && !missingType.getAny().isEmpty()) {
                questionnaireNode.set("missingBlock", createMissingBlocks(lunaticQuestionnaire));
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
    private ObjectNode createMissingBlocks(Questionnaire lunaticQuestionnaire) {
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
