package fr.insee.eno.treatments.dto;

import fr.insee.lunatic.model.flat.SuggesterField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EnoSuggesterFieldTest {

    @Mock
    private Map<String, List<String>> synonyms;

    @Test
    void whenConvertingToLunaticCheckMappingIsCorrect() {
        List<String> rules = List.of("rule1", "rule2");
        EnoSuggesterField enoField = new EnoSuggesterField("name", rules, "French", BigInteger.ONE, true, synonyms);
        SuggesterField field = EnoSuggesterField.toLunaticModel(enoField);
        assertEquals(field.getName(), enoField.getName());
        assertTrue(field.getRules().getPatterns().contains("rule1"));
        assertTrue(field.getRules().getPatterns().contains("rule2"));
        assertEquals(field.getLanguage(), enoField.getLanguage());
        assertEquals(field.getMin(), enoField.getMin());
        assertEquals(field.getStemmer(), enoField.getStemmer());
    }

    @ParameterizedTest
    @MethodSource("generateRules")
    void whenOtherRulesCheckMappingRulesProperty(List<String> rules) {
        EnoSuggesterField enoField = new EnoSuggesterField("name", rules, "French", BigInteger.ONE, true, synonyms);
        SuggesterField field = EnoSuggesterField.toLunaticModel(enoField);
        for(String rule : rules) {
            assertTrue(field.getRules().getPatterns().contains(rule));
        }
    }

    static Stream<Arguments> generateRules() {
        return Stream.of(
                Arguments.of(List.of("rule1", "rule2")),
                Arguments.of(List.of("rule1")),
                Arguments.of(List.of("rule1", "rule2", "rule3"))
        );
    }

    @Test
    void whenMultipleFieldsCheckEachFieldIsTransformed() {
        EnoSuggesterField enoField1 = new EnoSuggesterField("name1", List.of("rule1", "rule2"), "French", BigInteger.ONE, true, synonyms);
        EnoSuggesterField enoField2 = new EnoSuggesterField("name2", List.of("soft", "rule2"), "French", BigInteger.ONE, true, synonyms);
        List<EnoSuggesterField> enoFields = List.of(enoField1, enoField2);
        List<SuggesterField> fields = EnoSuggesterField.toLunaticModelList(enoFields);

        assertEquals(2, fields.size());
        assertEquals("name1", fields.get(0).getName());
        assertEquals("name2", fields.get(1).getName());
    }

    @Test
    void whenConvertingToLunaticMappingIfNullParameterReturnNull() {
        assertNull(EnoSuggesterField.toLunaticModel(null));
    }

    @Test
    void whenConvertingToLunaticMappingIfNullParameterListReturnEmptyList() {
        assertEquals(0, EnoSuggesterField.toLunaticModelList(null).size());
    }
}
