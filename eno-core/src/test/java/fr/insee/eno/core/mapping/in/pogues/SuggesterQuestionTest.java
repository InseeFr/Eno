package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.SuggesterQuestion;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SuggesterQuestionTest {

    @Test
    void arbitraryResponse_integrationTest() throws PoguesDeserializationException {
        //
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream(
                        "integration/pogues/pogues-suggester-arbitrary.json"));
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        new PoguesMapper().mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        SuggesterQuestion suggesterQuestion1 = assertInstanceOf(SuggesterQuestion.class,
                enoQuestionnaire.getSingleResponseQuestions().get(0));
        SuggesterQuestion suggesterQuestion2 = assertInstanceOf(SuggesterQuestion.class,
                enoQuestionnaire.getSingleResponseQuestions().get(1));
        assertEquals("COUNTRY", suggesterQuestion1.getResponse().getVariableName());
        assertNull(suggesterQuestion1.getArbitraryResponse());
        assertEquals("ACTIVITY", suggesterQuestion2.getResponse().getVariableName());
        assertEquals("ACTIVITY_ARBITRARY", suggesterQuestion2.getArbitraryResponse().getResponse().getVariableName());
    }
}
