package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Integration test for the */
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UniqueChoiceQuestionVariableIT {

    //Questionnaire lunaticQuestionnaire;

    //@BeforeAll

    @Test
    @DisplayName("Pogues+DDI to Lunatic, UCQs with variable options.")
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
        assertNotNull(lunaticQuestionnaire);
    }

}
