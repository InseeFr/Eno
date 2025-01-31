package fr.insee.eno.treatments;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.treatments.dto.SpecificTreatments;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Question;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.Roundabout;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LunaticSuggesterSpecificTreatmentTest {

    @Test
    void suggesterTest() throws DDIParsingException {
        //
        Questionnaire lunaticQuestionnaire = DDIToLunatic.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream("suggester-treatment/ddi-lgl1kmol.xml"))
                .transform(EnoParameters.of(Context.HOUSEHOLD, ModeParameter.CAWI, Format.LUNATIC));
        //
        SpecificTreatmentsDeserializer treatmentsDeserializer = new SpecificTreatmentsDeserializer();
        SpecificTreatments treatmentsInput = treatmentsDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream("suggester-treatment/suggesters.json"));
        LunaticSuggesterSpecificTreatment suggesterProcessing = new LunaticSuggesterSpecificTreatment(treatmentsInput.suggesters());
        suggesterProcessing.apply(lunaticQuestionnaire);
        //
        assertFalse(lunaticQuestionnaire.getSuggesters().isEmpty());
    }

    @Test
    void suggesterTest_lunaticV3() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(Context.HOUSEHOLD, ModeParameter.CAWI, Format.LUNATIC);
        enoParameters.getLunaticParameters().setDsfr(true);
        Questionnaire lunaticQuestionnaire = DDIToLunatic.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream("suggester-treatment/ddi-lgl1kmol.xml"))
                .transform(enoParameters);
        //
        SpecificTreatmentsDeserializer treatmentsDeserializer = new SpecificTreatmentsDeserializer();
        SpecificTreatments treatmentsInput = treatmentsDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream("suggester-treatment/suggesters.json"));
        LunaticSuggesterSpecificTreatment suggesterProcessing = new LunaticSuggesterSpecificTreatment(treatmentsInput.suggesters());
        suggesterProcessing.apply(lunaticQuestionnaire);
        //
        assertFalse(lunaticQuestionnaire.getSuggesters().isEmpty());
    }

    /** Integration test with a questionnaire that has a roundabout, and a suggester specific treatment on a question
     * within the roundabout. */
    @Test
    void suggesterTest_lunaticV3_roundabout() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(Context.HOUSEHOLD, ModeParameter.CAWI, Format.LUNATIC);
        enoParameters.getLunaticParameters().setDsfr(true);
        Questionnaire lunaticQuestionnaire = DDIToLunatic.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream("suggester-treatment/ddi-m0p3wmjl.xml"))
                .transform(enoParameters);
        //
        SpecificTreatmentsDeserializer treatmentsDeserializer = new SpecificTreatmentsDeserializer();
        SpecificTreatments treatmentsInput = treatmentsDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream("suggester-treatment/suggester-in-roundabout.json"));
        LunaticSuggesterSpecificTreatment suggesterProcessing = new LunaticSuggesterSpecificTreatment(treatmentsInput.suggesters());
        suggesterProcessing.apply(lunaticQuestionnaire);
        //
        assertFalse(lunaticQuestionnaire.getSuggesters().isEmpty());
        Roundabout roundabout = (Roundabout) lunaticQuestionnaire.getComponents().get(1);
        assertEquals(ComponentTypeEnum.SUGGESTER,
                ((Question) roundabout.getComponents().get(1)).getComponents().getFirst().getComponentType());
    }

}
