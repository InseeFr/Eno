package fr.insee.eno.treatments.dto;

import fr.insee.lunatic.model.flat.SuggesterQueryParserParams;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EnoSuggesterQueryParserParamsTest {

    @Test
    void whenConvertingToLunaticMappingIsCorrect() {
        EnoSuggesterQueryParserParams enoParams = new EnoSuggesterQueryParserParams("French", BigInteger.ONE, "pattern");
        SuggesterQueryParserParams params = EnoSuggesterQueryParserParams.toLunaticModel(enoParams);
        assertEquals(params.getLanguage(), enoParams.getLanguage());
        assertEquals(params.getMin(), enoParams.getMin());
        assertEquals(params.getPattern(), enoParams.getPattern());
    }

    @Test
    void whenConvertingToLunaticMappingIfNullParameterReturnNull() {
        assertNull(EnoSuggesterQueryParserParams.toLunaticModel(null));
    }
}
