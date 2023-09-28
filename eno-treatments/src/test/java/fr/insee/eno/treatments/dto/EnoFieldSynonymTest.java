package fr.insee.eno.treatments.dto;

import fr.insee.lunatic.model.flat.FieldSynonym;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnoFieldSynonymTest {

    @Test
    void whenConvertingToLunaticMappingIsCorrect() {
        EnoFieldSynonym field = new EnoFieldSynonym("source", List.of("target1", "target2"));
        FieldSynonym fieldLunatic = EnoFieldSynonym.toLunaticModel(field);
        assertEquals(fieldLunatic.getSource(), field.getSource());
        assertTrue(fieldLunatic.getTarget().contains("target1"));
        assertTrue(fieldLunatic.getTarget().contains("target2"));
    }

    @Test
    void whenConvertingToLunaticMappingIfNullParameterReturnNull() {
        assertNull(EnoFieldSynonym.toLunaticModel(null));
    }

    @Test
    void whenConvertingToLunaticMappingIfNullParameterListReturnEmptyList() {
        assertEquals(0, EnoFieldSynonym.toLunaticModelList(null).size());
    }

    @Test
    void whenMultipleSynonymsCheckEachSynonymIdIsTransformed() {
        EnoFieldSynonym enoField1 = new EnoFieldSynonym("source1", List.of("target1", "target2"));
        EnoFieldSynonym enoField2 = new EnoFieldSynonym("source2", List.of("target1", "target2"));
        List<EnoFieldSynonym> enoFields = List.of(enoField1, enoField2);
        List<FieldSynonym> fields = EnoFieldSynonym.toLunaticModelList(enoFields);

        assertEquals(2, fields.size());
        assertEquals("source1", fields.get(0).getSource());
        assertEquals("source2", fields.get(1).getSource());
    }
}
