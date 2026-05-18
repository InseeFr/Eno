package fr.insee.eno.core.processing.out.steps.lunatic.cleaning;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.cleaning.CleaningVariableEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Integration test for the "cleaning" of option responses in suggester */
class SuggesterOptionResponsesVariableCleaningIT {

    @Test
    @DisplayName("Lunatic cleaning with optionResponses in suggester wrapped by filter")
    void integrationTest() throws ParsingException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        // Given
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        String poguesResource = "integration/pogues/pogues-suggester-options.json";
        String ddiResource = "integration/ddi/ddi-suggester-options.xml";

        // When
        Questionnaire lunaticQuestionnaire = PoguesDDIToLunatic.fromInputStreams(
                        classLoader.getResourceAsStream(poguesResource),
                        classLoader.getResourceAsStream(ddiResource))
                .transform(enoParameters);

        // Then
        CleaningVariableEntry cleaningOfName = lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("CITY_OF_BIRTH");

        assertNotNull(cleaningOfName);
        // assert that suggester collected variable is cleaned
        assertThat(cleaningOfName.getCleanedVariableNames()).contains("CURRENT_CITY");
        // assert that suggester option responses variable is cleaned
        assertThat(cleaningOfName.getCleanedVariableNames()).contains("CURRENT_CITY_LABEL");
    }

}
