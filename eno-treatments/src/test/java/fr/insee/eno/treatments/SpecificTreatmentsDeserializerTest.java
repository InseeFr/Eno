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
        EnoSuggesterType suggester = suggesters.getFirst();
        assertEquals("L_PCS_HOMMES-1-3-0", suggester.name());
        assertEquals("plop", suggester.url());
        assertEquals(BigInteger.ONE, suggester.version());
        assertTrue(suggester.responseNames().contains("PCS"));
        assertEquals(BigInteger.TWO.longValue(), suggester.max().longValue());

        assertEquals(23, suggester.stopWords().size());
        assertTrue(suggester.stopWords().contains("a"));
        assertTrue(suggester.stopWords().contains("les"));
        assertTrue(suggester.stopWords().contains("du"));

        assertEquals(2, suggester.fields().size());

        EnoSuggesterField field = suggester.fields().getFirst();
        assertEquals("French", field.language());
        assertEquals("label", field.name());
        assertEquals(BigInteger.TWO.longValue(), field.min().longValue());
        assertTrue(field.stemmer());
        assertEquals(2, field.synonyms().size());
        assertEquals(1, field.rules().size());
        assertEquals("field", suggester.order().field());
        assertEquals("ascending", suggester.order().type());
        assertEquals("tokenized", suggester.queryParser().type());

        EnoSuggesterQueryParserParams params = suggester.queryParser().params();
        assertEquals("pattern", params.pattern());
        assertEquals("French", params.language());
        assertEquals(BigInteger.ONE.longValue(), params.min().longValue());

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
