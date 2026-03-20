package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.cleaning.CleaningVariableEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Integration test for the "unique choice with variable options" feature. */
class UniqueChoiceQuestionVariableCleaningIT {

    @Test
    @DisplayName("Lunatic cleaning with UCQs with variable options inside table")
    void integrationTest() throws ParsingException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        // Given
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        String poguesResource = "integration/pogues/pogues-ucq-variable-options.json";
        String ddiResource = "integration/ddi/ddi-ucq-variable-options.xml";

        // When
        Questionnaire lunaticQuestionnaire = PoguesDDIToLunatic.fromInputStreams(
                        classLoader.getResourceAsStream(poguesResource),
                        classLoader.getResourceAsStream(ddiResource))
                .transform(enoParameters);

        // Then
        CleaningVariableEntry cleaningOfName = lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("NAME");

        // QCU variable inside Table
        assertThat(cleaningOfName.getCleanedVariableNames()).contains("STATIC_TABLE111");
        // QCU variable inside DynamicTable (RosterForLoop)
        assertThat(cleaningOfName.getCleanedVariableNames()).contains("DYNAMIC_TABLE1"); // radio
        assertThat(cleaningOfName.getCleanedVariableNames()).contains("DYNAMIC_TABLE2"); // dropdown

    }

}
