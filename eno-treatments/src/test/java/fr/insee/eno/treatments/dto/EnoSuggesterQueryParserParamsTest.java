package fr.insee.eno.treatments.dto;

import fr.insee.lunatic.model.flat.SuggesterQueryParserParams;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EnoSuggesterQueryParserParamsTest {

    @Test
    void whenConvertingToLunaticMappingIsCorrect() {
        EnoSuggesterQueryParserParams enoParams = new EnoSuggesterQueryParserParams(
                "French", BigInteger.ONE, "pattern", false);
        SuggesterQueryParserParams params = EnoSuggesterQueryParserParams.toLunaticModel(enoParams);
        assertEquals(params.getLanguage(), enoParams.language());
        assertEquals(params.getMin(), enoParams.min());
        assertEquals(params.getPattern(), enoParams.pattern());
    }

    @Test
    void whenConvertingToLunaticMappingIfNullParameterReturnNull() {
        assertNull(EnoSuggesterQueryParserParams.toLunaticModel(null));
    }
}
