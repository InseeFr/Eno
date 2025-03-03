package fr.insee.eno.core;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.exceptions.business.UnauthorizedHeaderException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.LunaticSerializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.pogues.model.Questionnaire;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PoguesDDIToLunaticTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    @ParameterizedTest
    @ValueSource(strings = {
            "kx0a2hn8",
            "l7j0wwqx",
            "l20g2ba7",
            "ldodefpq",
            "lhpz68wp",
            "li49zxju",
            "ljr4jm9a",
            //"lx4qzdty", to do: throw an exception about having several roundabouts (forbidden, business rule)
    })
    void nonRegressionTest(String id) throws ParsingException, JSONException {

        // Given Pogues & DDI inputs and Eno parameters
        // (for questionnaires that does not contain features described in Pogues and not in DDI)
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(classLoader.getResourceAsStream(
                "functional/ddi/ddi-" + id + ".xml"));
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(classLoader.getResourceAsStream(
                "functional/pogues/pogues-" + id + ".json"));
        EnoParameters enoParameters = EnoParameters.of(Context.DEFAULT, ModeParameter.PROCESS, Format.LUNATIC);

        // When mapping from DDI and from Pogues + DDI
        fr.insee.lunatic.model.flat.Questionnaire fromDDI = DDIToLunatic
                .fromObject(ddiQuestionnaire).transform(enoParameters);
        fr.insee.lunatic.model.flat.Questionnaire fromPoguesDDI = PoguesDDIToLunatic
                .fromObjects(poguesQuestionnaire, ddiQuestionnaire).transform(enoParameters);
        // generating date could differ by a few seconds
        fromDDI.setGeneratingDate(null);
        fromPoguesDDI.setGeneratingDate(null);
        //
        String serializedFromDDI = LunaticSerializer.serializeToJson(fromDDI);
        String serializedFromPoguesDDI = LunaticSerializer.serializeToJson(fromPoguesDDI);

        // Then the resulting Lunatic questionnaire should be identical
        JSONAssert.assertEquals(serializedFromDDI, serializedFromPoguesDDI, JSONCompareMode.STRICT);
    }

    @Test
    void questionnaireWithInvalidContent_shouldThrow() throws ParsingException {

        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(classLoader.getResourceAsStream(
                "functional/ddi/ddi-l8x6fhtd.xml"));
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(classLoader.getResourceAsStream(
                "functional/pogues/pogues-l8x6fhtd.json"));
        EnoParameters enoParameters = EnoParameters.of(Context.DEFAULT, ModeParameter.PROCESS, Format.LUNATIC);

        DDIToLunatic ddiToLunatic = DDIToLunatic.fromObject(ddiQuestionnaire);
        assertThrows(UnauthorizedHeaderException.class, () -> ddiToLunatic.transform(enoParameters));
        PoguesDDIToLunatic poguesDDIToLunatic = PoguesDDIToLunatic.fromObjects(poguesQuestionnaire, ddiQuestionnaire);
        assertThrows(UnauthorizedHeaderException.class, () -> poguesDDIToLunatic.transform(enoParameters));
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
            "other-specify",
            "pairwise",
            "resizing",
            "roundabout",
            "roundabout-controls",
            "roundabout-except",
            "roundabout-subsequence",
            "simple",
            "subsequences",
            "suggester",
            "suggester-options",
            "suggester-options-table",
            "table-custom-header",
            "tooltips",
            "variables"
    })
    void nonRegressionTest2(String classifier) throws ParsingException, JSONException {

        // Given Pogues & DDI inputs and Eno parameters
        // (for questionnaires that does not contain features described in Pogues and not in DDI)
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(classLoader.getResourceAsStream(
                "integration/ddi/ddi-" + classifier + ".xml"));
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(classLoader.getResourceAsStream(
                "integration/pogues/pogues-" + classifier + ".json"));
        EnoParameters enoParameters = EnoParameters.of(Context.DEFAULT, ModeParameter.PROCESS, Format.LUNATIC);

        // When mapping from DDI and from Pogues + DDI
        fr.insee.lunatic.model.flat.Questionnaire fromDDI = DDIToLunatic
                .fromObject(ddiQuestionnaire).transform(enoParameters);
        fr.insee.lunatic.model.flat.Questionnaire fromPoguesDDI = PoguesDDIToLunatic
                .fromObjects(poguesQuestionnaire, ddiQuestionnaire).transform(enoParameters);
        // generating date could differ by a few seconds
        fromDDI.setGeneratingDate(null);
        fromPoguesDDI.setGeneratingDate(null);
        //
        String serializedFromDDI = LunaticSerializer.serializeToJson(fromDDI);
        String serializedFromPoguesDDI = LunaticSerializer.serializeToJson(fromPoguesDDI);

        // Then the resulting Lunatic questionnaire should be identical
        JSONAssert.assertEquals(serializedFromDDI, serializedFromPoguesDDI, JSONCompareMode.STRICT);
    }

}
