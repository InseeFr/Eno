package fr.insee.eno.treatments;

import fr.insee.eno.treatments.dto.*;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SpecificTreatmentsDeserializerTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    @Test
    void deserializeSuggesterTest() {
        InputStream treatmentsInputStream = classLoader.getResourceAsStream("specific-treatments.json");

        SpecificTreatmentsDeserializer converter = new SpecificTreatmentsDeserializer();

        SpecificTreatments treatments = converter.deserialize(treatmentsInputStream);
        List<EnoSuggesterType> suggesters = treatments.suggesters();

        assertNotNull(suggesters);
        assertEquals(2, suggesters.size());
        EnoSuggesterType suggester = suggesters.get(0);
        assertEquals("L_PCS_HOMMES-1-3-0", suggester.getName());
        assertEquals("plop", suggester.getUrl());
        assertEquals("1", suggester.getVersion());
        assertTrue(suggester.getResponseNames().contains("PCS"));
        assertEquals(BigInteger.TWO.longValue(), suggester.getMax().longValue());

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
        assertEquals(1, field.getRules().size());
        assertEquals("field", suggester.getOrder().getField());
        assertEquals("ascending", suggester.getOrder().getType());
        assertEquals("tokenized", suggester.getQueryParser().getType());

        EnoSuggesterQueryParserParams params = suggester.getQueryParser().getParams();
        assertEquals("pattern", params.getPattern());
        assertEquals("French", params.getLanguage());
        assertEquals(BigInteger.ONE.longValue(), params.getMin().longValue());

    }

    @Test
    void deserializeRegroupementsTest() {
        InputStream treatmentsInputStream = classLoader.getResourceAsStream("specific-treatments.json");

        SpecificTreatmentsDeserializer converter = new SpecificTreatmentsDeserializer();

        SpecificTreatments treatments = converter.deserialize(treatmentsInputStream);
        Regroupements regroupements = new Regroupements(treatments.regroupements());

        assertNotNull(regroupements);
        assertEquals(2, regroupements.count());
        Optional<Regroupement> regroupement = regroupements.getRegroupementForVariable("var1");
        assertTrue(regroupement.isPresent());
        assertTrue(regroupement.get().hasVariable("var1"));
        assertTrue(regroupement.get().hasVariable("var2"));

        regroupement = regroupements.getRegroupementForVariable("var3");
        assertTrue(regroupement.isPresent());
        assertTrue(regroupement.get().hasVariable("var3"));
        assertTrue(regroupement.get().hasVariable("var4"));
    }
}
