package fr.insee.eno.treatments;

import fr.insee.eno.treatments.dto.Regroupement;
import fr.insee.eno.treatments.dto.Regroupements;
import fr.insee.eno.treatments.dto.SpecificTreatments;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SpecificTreatmentsDeserializerTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

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
