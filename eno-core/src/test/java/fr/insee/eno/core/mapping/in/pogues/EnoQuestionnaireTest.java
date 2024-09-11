package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnoQuestionnaireTest {

    @Test
    void mapIdFromPogues() {
        //
        Questionnaire poguesQuestionnaire = new Questionnaire();
        poguesQuestionnaire.setId("foo-questionnaire-id");
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        PoguesMapper poguesMapper = new PoguesMapper();
        poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        assertEquals("foo-questionnaire-id", enoQuestionnaire.getId());
    }

}
