package fr.insee.eno.treatments;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
                classLoader.getResourceAsStream("specific-treatments.json"))
                .get("suggesters");
        //
        Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
        //
        assertTrue(errors.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "suggester-examples/suggesters-example.json",
            "suggester-examples/suggesters-example-1.json",
            "suggester-examples/suggesters-example-2.json",
            "suggester-examples/suggesters-example-3.json",
            "suggester-examples/suggesters-example-4.json",
    })
    void suggestersSchemaTest(String relativeFilePath) throws IOException {
        //
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema jsonSchema = factory.getSchema(
                classLoader.getResourceAsStream("schema.suggesters.json"));
        JsonNode jsonNode = mapper.readTree(
                classLoader.getResourceAsStream(relativeFilePath));
        //
        Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
        //
        assertTrue(errors.isEmpty());
    }

    @Test
    void treatmentsSchema_suggestersTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema jsonSchema = factory.getSchema(
                classLoader.getResourceAsStream("schema.suggesters.json"));
        JsonNode jsonNode = mapper.readTree(
                classLoader.getResourceAsStream("suggester-treatment/suggesters.json"))
                .get("suggesters");
        //
        Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
        //
        assertTrue(errors.isEmpty());
    }

}
