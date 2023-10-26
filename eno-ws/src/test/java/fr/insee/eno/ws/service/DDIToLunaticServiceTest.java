package fr.insee.eno.ws.service;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.treatments.LunaticRegroupementProcessing;
import fr.insee.eno.treatments.LunaticSuggesterProcessing;
import fr.insee.eno.treatments.SpecificTreatmentsDeserializer;
import fr.insee.eno.treatments.dto.SpecificTreatments;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DDIToLunaticServiceTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "lna9ksqx", // loops with "except"
            "lmyjrqbb", // loops without "except"
            "ll27mb7f", // dynamic labels
            "ll28it6e", // dynamic labels without loops
            "lk6x162e", // filters
            "kzfezgxb", // numeric controls
            "l9o7l439", // all components
            "ldodefpq", // pairwise links
            "ljps8p2l", // dynamic tables
            "lb3ei722", // occurrence filtering
            "l7j0wwqx", // linked loops
            "simpsonsvtl", // Simpsons questionnaire
            "kx0a2hn8", // loops
            "kzguw1v7", // non-numeric controls
            "kanye31s_1", // declarations
    })
    void nonRegression_DefaultCAWI(String questionnaireId) {
        //
        DDIToLunaticService ddiToLunaticService = new DDIToLunaticService();
        String result = ddiToLunaticService.transformToJson(
                        this.getClass().getClassLoader().getResourceAsStream("non-regression/ddi-"+questionnaireId+".xml"),
                        EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC))
                .block();
        //
        assertNotNull(result);
    }

    @Test
    @Disabled("to be fixed")
    void nonRegression_suggesterProcessing() {
        //
        SpecificTreatmentsDeserializer deserializer = new SpecificTreatmentsDeserializer();
        SpecificTreatments postProcessingInput = deserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream(
                        "non-regression/suggester-processing/suggester-input.json"));
        LunaticPostProcessing lunaticPostProcessing = new LunaticPostProcessing();
        lunaticPostProcessing.addPostProcessing(new LunaticSuggesterProcessing(postProcessingInput.suggesters()));

        //
        DDIToLunaticService ddiToLunaticService = new DDIToLunaticService();
        String result = ddiToLunaticService.transformToJson(
                        this.getClass().getClassLoader().getResourceAsStream("non-regression/suggester-processing/ddi-l7j0wwqx.xml"),
                        EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC),
                        lunaticPostProcessing)
                .block();

        //
        assertNotNull(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "group-input.json",
            "group-input-outside-loop.json"
    })
    void nonRegression_groupProcessing(String groupProcessingFileName) {
        //
        SpecificTreatmentsDeserializer deserializer = new SpecificTreatmentsDeserializer();
        SpecificTreatments postProcessingInput = deserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream(
                        "non-regression/group-processing/" + groupProcessingFileName));
        LunaticPostProcessing lunaticPostProcessing = new LunaticPostProcessing();
        lunaticPostProcessing.addPostProcessing(new LunaticRegroupementProcessing(postProcessingInput.regroupements()));

        //
        DDIToLunaticService ddiToLunaticService = new DDIToLunaticService();
        String result = ddiToLunaticService.transformToJson(
                        this.getClass().getClassLoader().getResourceAsStream("non-regression/group-processing/ddi-lhpz68wp.xml"),
                        EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC))
                .block();

        //
        assertNotNull(result);
    }

}
