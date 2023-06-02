package fr.insee.eno.treatments;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import fr.insee.eno.treatments.dto.Regroupement;

import java.io.IOException;
import java.io.Serial;
import java.util.List;

/**
 * Deserialize the regroupement object from the specific treatment json file
 */
public class RegroupementDeserializer extends StdDeserializer<Regroupement> {

    @Serial
    private static final long serialVersionUID = 5928430315100640987L;

    public RegroupementDeserializer() {
        this(null);
    }

    public RegroupementDeserializer(Class<RegroupementDeserializer> t) {
        super(t);
    }

    @Override
    public Regroupement deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode regroupementNode = jp.getCodec().readTree(jp);

        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {
        });

        List<String> variables = reader.readValue(regroupementNode);
        return new Regroupement(variables);
    }
}