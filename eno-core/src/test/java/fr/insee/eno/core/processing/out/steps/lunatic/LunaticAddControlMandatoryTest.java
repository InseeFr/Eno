package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.lunatic.model.flat.Input;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LunaticAddControlMandatoryTest {

    private Questionnaire lunaticQuestionnaire;

    @BeforeEach
    void createLunaticQuestionnaire() {
        lunaticQuestionnaire = new Questionnaire();
    }

    @Test
    @DisplayName("Short text response component.")
    void shortText() {
        Input lunaticInput = new Input();
        lunaticInput.setId("input-id");
    }

    class IntegrationTest {

    }

}
