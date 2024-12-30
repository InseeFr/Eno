package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.SuggesterField;
import fr.insee.lunatic.model.flat.SuggesterQueryParser;
import fr.insee.lunatic.model.flat.SuggesterType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LunaticSuggesterConfigurationTest {

    private final Map<String, SuggesterType> suggesters = new HashMap<>();

    @BeforeAll
    void suggestersIntegrationTest() throws DDIParsingException {
        //
        Questionnaire lunaticQuestionnaire = new DDIToLunatic().transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-suggester.xml"),
                EnoParameters.of(EnoParameters.Context.BUSINESS, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
        //
        lunaticQuestionnaire.getSuggesters().forEach(suggesterType -> suggesters.put(suggesterType.getName(), suggesterType));
    }

    @Test
    void countSuggesters() {
        assertEquals(8, suggesters.size());
    }

    @Test
    void suggesterField() {
        SuggesterType suggesterType = suggesters.get("L_ACTIVITES-1-0-0");
        assertEquals(1, suggesterType.getFields().size());
        SuggesterField suggesterField = suggesterType.getFields().getFirst();
        assertEquals("label", suggesterField.getName());
        assertEquals(List.of("[\\w]+"), suggesterField.getRules().getPatterns());
        assertEquals(BigInteger.valueOf(3), suggesterField.getMin());
        assertEquals("French", suggesterField.getLanguage());
        assertEquals(false, suggesterField.getStemmer());
        assertEquals(19, suggesterField.getSynonyms().size());
        assertEquals(List.of("CONSULTING"), suggesterField.getSynonyms().get("conseil"));
        assertEquals(5, suggesterField.getSynonyms().get("accueil").size());
    }

    @Test
    void suggesterWithTwoFields(){
        SuggesterType suggesterType = suggesters.get("L_DEPNAIS-1-1-0");
        assertEquals(2, suggesterType.getFields().size());
        assertEquals("label", suggesterType.getFields().get(0).getName());
        assertEquals("id", suggesterType.getFields().get(1).getName());
        assertEquals("soft", suggesterType.getFields().get(0).getRules().getRule());
        assertEquals("soft", suggesterType.getFields().get(1).getRules().getRule());
    }

    @Test
    void suggesterQueryParser() {
        SuggesterType suggesterType = suggesters.get("L_ACTIVITES-1-0-0");
        SuggesterQueryParser queryParser = suggesterType.getQueryParser();
        assertEquals("tokenized", queryParser.getType());
        assertEquals("French", queryParser.getParams().getLanguage());
        assertEquals(BigInteger.valueOf(3), queryParser.getParams().getMin());
        assertEquals("[\\w.]+", queryParser.getParams().getPattern());
        assertEquals(false, queryParser.getParams().getStemmer());
    }

    @Test
    void suggesterStopWords() {
        List<String> stopWords = suggesters.get("L_ACTIVITES-1-0-0").getStopWords();
        assertEquals(23, stopWords.size());
        assertTrue(stopWords.containsAll(Set.of("le", "la", "les")));
    }

}
