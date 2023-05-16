package fr.insee.eno.treatments;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.eno.treatments.dto.EnoSuggesterType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class SuggesterDeserializer {

    public List<EnoSuggesterType> deserializeSuggesters(InputStream suggestersStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(suggestersStream, new TypeReference<List<EnoSuggesterType>>(){});
    }
}
