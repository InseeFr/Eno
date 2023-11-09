package fr.insee.eno.treatments;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.treatments.dto.SpecificTreatments;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class LunaticSuggesterProcessingTest {

    @Test
    void suggesterTest() throws DDIParsingException {
        //
        Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                this.getClass().getClassLoader().getResourceAsStream("suggester/ddi-lgl1kmol.xml"),
                EnoParameters.of(EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
        //
        SpecificTreatmentsDeserializer treatmentsDeserializer = new SpecificTreatmentsDeserializer();
        SpecificTreatments treatmentsInput = treatmentsDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream("suggester/suggester.json"));
        LunaticSuggesterProcessing suggesterProcessing = new LunaticSuggesterProcessing(treatmentsInput.suggesters());
        suggesterProcessing.apply(lunaticQuestionnaire);
        //
        assertFalse(lunaticQuestionnaire.getSuggesters().isEmpty());
    }

}
