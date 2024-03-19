package fr.insee.eno.treatments.dto;

import fr.insee.lunatic.model.flat.SuggesterQueryParser;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EnoSuggesterQueryParserTest {

    @Mock
    private EnoSuggesterQueryParserParams params;

    @Test
    void whenConvertingToLunaticMappingIsCorrect() {
        EnoSuggesterQueryParser enoParser = new EnoSuggesterQueryParser("type", params);
        SuggesterQueryParser parser = EnoSuggesterQueryParser.toLunaticModel(enoParser);
        assertEquals(parser.getType(), enoParser.getType());
    }

    @Test
    void whenConvertingToLunaticMappingIfNullParameterReturnNull() {
        assertNull(EnoSuggesterQueryParser.toLunaticModel(null));
    }
}
