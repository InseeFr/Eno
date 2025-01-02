package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.exceptions.business.IllegalPoguesElementException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.pogues.model.FlowLogicEnum;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PoguesCheckFilterModeTest {

    @Test
    void validValue() {
        //
        Questionnaire poguesQuestionnaire = new Questionnaire();
        poguesQuestionnaire.setFlowLogic(FlowLogicEnum.FILTER);
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        PoguesCheckFilterMode processing = new PoguesCheckFilterMode();
        assertDoesNotThrow(() -> processing.apply(enoQuestionnaire));
    }

    @Test
    void invalidValue_shouldThrow() {
        //
        Questionnaire poguesQuestionnaire = new Questionnaire();
        poguesQuestionnaire.setFlowLogic(FlowLogicEnum.REDIRECTION);
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        PoguesCheckFilterMode processing = new PoguesCheckFilterMode();
        assertThrows(IllegalPoguesElementException.class, () -> processing.apply(enoQuestionnaire));
    }

}
