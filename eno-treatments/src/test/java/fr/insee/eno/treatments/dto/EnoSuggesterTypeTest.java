package fr.insee.eno.treatments.dto;

import fr.insee.lunatic.model.flat.SuggesterType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnoSuggesterTypeTest {

    @Mock
    private EnoSuggesterOrder order;

    @Mock
    private EnoSuggesterQueryParser queryParser;

    @Mock
    private List<String> responseNames;

    @Test
    void whenConvertingToLunaticMappingIsCorrect() {
        EnoSuggesterField enoField1 = new EnoSuggesterField("name1", List.of("rule1", "rule2"), "French", BigInteger.ONE, true, null);
        EnoSuggesterField enoField2 = new EnoSuggesterField("name2", List.of("soft", "rule2"), "French", BigInteger.ONE, true, null);
        List<EnoSuggesterField> enoFields = List.of(enoField1, enoField2);

        EnoSuggesterType enoType = new EnoSuggesterType(responseNames, "name", enoFields,
                BigInteger.ONE, List.of("stopWord1", "stopWord2"), order, queryParser, "url", "version");
        SuggesterType type = EnoSuggesterType.toLunaticModel(enoType);

        assertEquals(type.getName(), enoType.getName());
        assertEquals(type.getMax(), enoType.getMax());
        assertTrue(type.getStopWords().contains("stopWord1"));
        assertTrue(type.getStopWords().contains("stopWord2"));
        assertEquals(type.getUrl(), enoType.getUrl());
        assertEquals(type.getVersion(), enoType.getVersion());
    }

    @Test
    void whenConvertingToLunaticMappingIfNullParameterReturnNull() {
        assertNull(EnoSuggesterType.toLunaticModel(null));
    }

    @Test
    void whenConvertingToLunaticMappingIfNullParameterListReturnEmptyList() {
        assertEquals(0, EnoSuggesterType.toLunaticModelList(null).size());
    }
}
