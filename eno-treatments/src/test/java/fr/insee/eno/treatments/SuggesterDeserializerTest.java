package fr.insee.eno.treatments;

import fr.insee.eno.treatments.dto.EnoSuggesterField;
import fr.insee.eno.treatments.dto.EnoSuggesterQueryParserParams;
import fr.insee.eno.treatments.dto.EnoSuggesterType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SuggesterDeserializerTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    @Test
    void deserializeSuggesterTest() throws IOException {
        InputStream suggestersInputStream = classLoader.getResourceAsStream("suggesters.json");

        SuggesterDeserializer converter = new SuggesterDeserializer();

        List<EnoSuggesterType> suggesters = converter.deserializeSuggesters(suggestersInputStream);

        assertNotNull(suggesters);
        assertEquals(2, suggesters.size());
        EnoSuggesterType suggester = suggesters.get(0);
        assertEquals("L_PCS_HOMMES-1-3-0", suggester.getName());
        assertEquals("plop", suggester.getUrl());
        assertEquals("1", suggester.getVersion());
        assertTrue(suggester.getResponseNames().contains("jfaz9kv9"));
        assertEquals(BigInteger.ONE.longValue(), suggester.getMax().longValue());

        assertEquals(23, suggester.getStopWords().size());
        assertTrue(suggester.getStopWords().contains("a"));
        assertTrue(suggester.getStopWords().contains("les"));
        assertTrue(suggester.getStopWords().contains("du"));

        assertEquals(2, suggester.getFields().size());

        EnoSuggesterField field = suggester.getFields().get(0);
        assertEquals("French", field.getLanguage());
        assertEquals("label", field.getName());
        assertEquals(BigInteger.TWO.longValue(), field.getMin().longValue());
        assertTrue(field.getStemmer());
        assertEquals(2, field.getSynonyms().size());
        assertEquals(2, field.getRules().size());
        assertEquals("field", suggester.getOrder().getField());
        assertEquals("type", suggester.getOrder().getType());
        assertEquals("tokenized", suggester.getQueryParser().getType());

        EnoSuggesterQueryParserParams params = suggester.getQueryParser().getParams();
        assertEquals("pattern", params.getPattern());
        assertEquals("French", params.getLanguage());
        assertEquals(BigInteger.ONE.longValue(), params.getMin().longValue());

    }
}
