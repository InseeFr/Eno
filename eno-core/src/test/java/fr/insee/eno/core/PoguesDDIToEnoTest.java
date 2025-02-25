package fr.insee.eno.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.exceptions.business.IllegalPoguesElementException;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.SuggesterQuestion;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.pogues.model.Questionnaire;
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PoguesDDIToEnoTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    @ParameterizedTest
    @ValueSource(strings = {
            "kx0a2hn8",
            "l7j0wwqx",
            "l8x6fhtd",
            "l20g2ba7",
            "ldodefpq",
            "lhpz68wp",
            "li49zxju",
            "ljr4jm9a",
            "lx4qzdty",
    })
    void nonRegressionTest(String id) throws ParsingException, JsonProcessingException, JSONException {

        // Given Pogues & DDI inputs and Eno parameters
        // (for questionnaires that does not contain features described in Pogues and not in DDI)
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(classLoader.getResourceAsStream(
                "functional/ddi/ddi-" + id + ".xml"));
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(classLoader.getResourceAsStream(
                "functional/pogues/pogues-" + id + ".json"));
        EnoParameters enoParameters = EnoParameters.of(Context.DEFAULT, ModeParameter.PROCESS, Format.LUNATIC);

        // When mapping from DDI and from Pogues + DDI
        EnoQuestionnaire fromDDI = DDIToEno.fromObject(ddiQuestionnaire)
                .transform(enoParameters);
        EnoQuestionnaire fromPoguesDDI = PoguesDDIToEno.fromObjects(poguesQuestionnaire, ddiQuestionnaire)
                .transform(enoParameters);
        removePoguesSpecificProperties(fromPoguesDDI);

        // Then the resulting Eno questionnaire should be identical
        String serialized1 = new ObjectMapper().writeValueAsString(fromDDI);
        String serialized2 = new ObjectMapper().writeValueAsString(fromPoguesDDI);
        JSONAssert.assertEquals(serialized1, serialized2, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "kzy5kbtl",
            "l5v3spn0",
            "lmyjrqbb",
            "lqnje8yr"
    })
    void invalidPoguesQuestionnaires_shouldThrow(String id) throws PoguesDeserializationException {
        //
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(classLoader.getResourceAsStream(
                "functional/pogues/pogues-" + id + ".json"));
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        PoguesMapper poguesMapper = new PoguesMapper();
        MappingException mappingException = assertThrows(MappingException.class, () ->
                poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire));
        assertInstanceOf(IllegalPoguesElementException.class, mappingException.getCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "controls",
            "controls-line",
            "dates-2",
            "declarations",
            "dimensions",
            "durations-2",
            "dynamic-table",
            "dynamic-table-2",
            "dynamic-table-size",
            "dynamic-unit",
            "filters-calculated",
            "filters-extended",
            "filters-nested",
            "filters-simple",
            "labels",
            "loop-except",
            "loops-extended-sequence",
            "loops-extended-subsequence",
            "loops-sequence",
            "loops-subsequence",
            "mcq",
            "no-data-cell",
            //"other-specify",
            "pairwise",
            "resizing",
            "roundabout",
            "roundabout-controls",
            "roundabout-except",
            "roundabout-subsequence",
            "simple",
            "subsequences",
            "suggester",
            "suggester-arbitrary",
            "suggester-options",
            "suggester-options-table",
            "table-custom-header",
            "tooltips",
            "variables"
    })
    void nonRegressionTest2(String classifier) throws ParsingException, JsonProcessingException, JSONException {

        // Given Pogues & DDI inputs and Eno parameters
        // (for questionnaires that does not contain features described in Pogues and not in DDI)
        ClassLoader classLoader = this.getClass().getClassLoader();
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(classLoader.getResourceAsStream(
                "integration/ddi/ddi-" + classifier + ".xml"));
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(classLoader.getResourceAsStream(
                "integration/pogues/pogues-" + classifier + ".json"));
        EnoParameters enoParameters = EnoParameters.of(Context.DEFAULT, ModeParameter.PROCESS, Format.LUNATIC);

        // When mapping from DDI and from Pogues + DDI
        EnoQuestionnaire fromDDI = DDIToEno.fromObject(ddiQuestionnaire)
                .transform(enoParameters);
        EnoQuestionnaire fromPoguesDDI = PoguesDDIToEno.fromObjects(poguesQuestionnaire, ddiQuestionnaire)
                .transform(enoParameters);
        removePoguesSpecificProperties(fromPoguesDDI);

        // Then the resulting Eno questionnaire should be identical
        String serialized1 = new ObjectMapper().writeValueAsString(fromDDI);
        String serialized2 = new ObjectMapper().writeValueAsString(fromPoguesDDI);
        JSONAssert.assertEquals(serialized1, serialized2, JSONCompareMode.STRICT);
    }

    /**
     * Removes the properties that are specific to Pogues to ease the testing of
     * "Pogues + DDI" vs. "DDI only".
     * @param enoQuestionnaire Eno questionnaire mapped from Pogues + DDI
     */
    private void removePoguesSpecificProperties(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.setAgency(null);
        enoQuestionnaire.setExpressionLanguage(null);
        enoQuestionnaire.setFilterMode(null);
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(question -> question.getResponse() != null)
                .forEach(question -> question.getResponse().setVariableReference(null));
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(NumericQuestion.class::isInstance).map(NumericQuestion.class::cast)
                .forEach(numericQuestion -> numericQuestion.setIsUnitDynamic(null));
        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(SimpleMultipleChoiceQuestion.class::isInstance).map(SimpleMultipleChoiceQuestion.class::cast)
                .forEach(question -> {
                    question.getCodeResponses()
                            .forEach(codeResponse -> codeResponse.getResponse().setVariableReference(null));
                    question.getDetailResponses()
                            .forEach(detailResponse -> detailResponse.setPoguesId(null));
                });
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(UniqueChoiceQuestion.class::isInstance).map(UniqueChoiceQuestion.class::cast)
                .forEach(question -> question.getDetailResponses()
                        .forEach(detailResponse -> detailResponse.setPoguesId(null)));
        enoQuestionnaire.getCodeLists().forEach(codeList -> codeList.getCodeItems().forEach(this::removeCodeItemParentValues));
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(SuggesterQuestion.class::isInstance).map(SuggesterQuestion.class::cast)
                .forEach(suggesterQuestion -> suggesterQuestion.setArbitraryResponse(null));
    }
    private void removeCodeItemParentValues(CodeItem codeItem) {
        codeItem.setParentValue(null);
        codeItem.getCodeItems().forEach(this::removeCodeItemParentValues);
    }
}
