package fr.insee.eno.treatments;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import fr.insee.eno.treatments.dto.EnoSuggesterType;
import fr.insee.eno.treatments.exceptions.SuggesterDeserializationException;
import fr.insee.eno.treatments.exceptions.SuggesterValidationException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

@Slf4j
public class SuggesterDeserializer {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    /**
     * Deserialize a json stream input to an Eno suggester object
     * @param suggestersStream stream input of suggesters
     * @return an Eno suggester corresponding to the input parameter
     */
    public List<EnoSuggesterType> deserializeSuggesters(InputStream suggestersStream) {
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema jsonSchema = factory.getSchema(
                classLoader.getResourceAsStream("schema.suggesters.json"));

        try {
            JsonNode jsonSuggesters = mapper.readTree(suggestersStream);
            Set<ValidationMessage> errors = jsonSchema.validate(jsonSuggesters);

            if(!errors.isEmpty()) {
                StringBuilder messageBuilder = new StringBuilder();
                for(ValidationMessage errorMessage : errors) {
                    messageBuilder.append(errorMessage.getMessage());
                    messageBuilder.append("\n");
                }
                throw new SuggesterValidationException(messageBuilder.toString());
            }

            ObjectReader reader = mapper.readerFor(new TypeReference<List<EnoSuggesterType>>(){});
            return reader.readValue(jsonSuggesters);
        } catch(IOException ex) {
            throw new SuggesterDeserializationException(ex.getMessage());
        }
    }
}
