package fr.insee.eno.treatments;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import fr.insee.eno.treatments.dto.SpecificTreatments;
import fr.insee.eno.treatments.exceptions.SpecificTreatmentsDeserializationException;
import fr.insee.eno.treatments.exceptions.SpecificTreatmentsValidationException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Slf4j
public class SpecificTreatmentsDeserializer {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    /**
     * Deserialize a json stream input to an specific treatments object
     * @param treatmentsStream stream input of specific treatments
     * @return the specifics treatments corresponding to the input parameter
     */
    public SpecificTreatments deserialize(InputStream treatmentsStream) {
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema jsonSchema = factory.getSchema(
                classLoader.getResourceAsStream("schema.treatments.json"));

        try {
            JsonNode jsonTreatments = mapper.readTree(treatmentsStream);
            Set<ValidationMessage> errors = jsonSchema.validate(jsonTreatments);

            if(!errors.isEmpty()) {
                StringBuilder messageBuilder = new StringBuilder();
                for(ValidationMessage errorMessage : errors) {
                    messageBuilder.append(errorMessage.getMessage());
                    messageBuilder.append("\n");
                }
                throw new SpecificTreatmentsValidationException(messageBuilder.toString());
            }

            ObjectReader reader = mapper.readerFor(SpecificTreatments.class);
            return reader.readValue(jsonTreatments);
        } catch(IOException ex) {
            throw new SpecificTreatmentsDeserializationException(ex.getMessage());
        }
    }
}
