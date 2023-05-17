package fr.insee.eno.treatments;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonSchemaValidationTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    @Test
    void givenValidJsonInput_whenValidating_thenNoErrors() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema jsonSchema = factory.getSchema(
                classLoader.getResourceAsStream("schema.suggesters.json"));
        JsonNode jsonNode = mapper.readTree(
                classLoader.getResourceAsStream("suggesters.json"));
        Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);

        for(ValidationMessage error : errors) {
            System.out.println(error.toString());
        }
        assertTrue(errors.isEmpty());
    }
}
