package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.exceptions.business.IllegalPoguesElementException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.pogues.model.FormulasLanguageEnum;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PoguesCheckExpressionLanguageTest {

    @Test
    void validValue() {
        //
        Questionnaire poguesQuestionnaire = new Questionnaire();
        poguesQuestionnaire.setFormulasLanguage(FormulasLanguageEnum.VTL);
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        PoguesCheckExpressionLanguage processing = new PoguesCheckExpressionLanguage();
        assertDoesNotThrow(() -> processing.apply(enoQuestionnaire));
    }

    @Test
    void invalidValue_shouldThrow() {
        //
        Questionnaire poguesQuestionnaire = new Questionnaire();
        poguesQuestionnaire.setFormulasLanguage(FormulasLanguageEnum.XPATH);
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        PoguesCheckExpressionLanguage processing = new PoguesCheckExpressionLanguage();
        assertThrows(IllegalPoguesElementException.class, () -> processing.apply(enoQuestionnaire));
    }

}
