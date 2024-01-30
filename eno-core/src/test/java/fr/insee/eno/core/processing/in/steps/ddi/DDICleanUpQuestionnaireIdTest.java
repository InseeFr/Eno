package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DDICleanUpQuestionnaireIdTest {

    @Test
    void questionnaireWithNullId_shouldThrowException() {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        assertThrows(Exception.class, () -> new DDICleanUpQuestionnaireId().apply(enoQuestionnaire));
    }

    @Test
    void questionnaireWithRegularId_shouldNotBeModified() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        enoQuestionnaire.setId("abcde1234");
        //
        new DDICleanUpQuestionnaireId().apply(enoQuestionnaire);
        //
        assertEquals("abcde1234", enoQuestionnaire.getId());
    }

    @Test
    void questionnaireWithInseePrefix_shouldBeModified() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        enoQuestionnaire.setId("INSEE-abcde1234");
        //
        new DDICleanUpQuestionnaireId().apply(enoQuestionnaire);
        //
        assertEquals("abcde1234", enoQuestionnaire.getId());
    }

}
