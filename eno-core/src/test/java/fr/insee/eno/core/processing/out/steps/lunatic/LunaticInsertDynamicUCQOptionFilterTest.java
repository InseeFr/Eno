package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.Radio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LunaticInsertDynamicUCQOptionFilterTest {

    @Test
    void injects_option_filter_to_radio() {
        UniqueChoiceQuestion ucq = new UniqueChoiceQuestion();
        ucq.setId("Q1");
        ucq.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.RADIO);
        ucq.setOptionSource("LOOP_VAR");
        CalculatedExpression filter = new CalculatedExpression();
        filter.setValue("AGE >= 18");
        ucq.setOptionFilter(filter);

        EnoQuestionnaire eno = new EnoQuestionnaire();
        eno.getSingleResponseQuestions().add(ucq);

        Questionnaire lunatic = new Questionnaire();
        Radio radio = new Radio();
        radio.setId("Q1");
        lunatic.getComponents().add(radio);

        new LunaticInsertDynamicUCQOptionFilter(eno).apply(lunatic);

        assertNotNull(radio.getOptionFilter());
        assertEquals("AGE >= 18", radio.getOptionFilter().getValue());
    }

    @Test
    void does_nothing_if_option_source_or_filter_is_null() {
        UniqueChoiceQuestion ucq = new UniqueChoiceQuestion();
        ucq.setId("Q2");
        ucq.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.RADIO);
        ucq.setOptionSource(null);
        ucq.setOptionFilter(null);

        EnoQuestionnaire eno = new EnoQuestionnaire();
        eno.getSingleResponseQuestions().add(ucq);

        Questionnaire lunatic = new Questionnaire();
        Radio radio = new Radio();
        radio.setId("Q2");
        lunatic.getComponents().add(radio);

        new LunaticInsertDynamicUCQOptionFilter(eno).apply(lunatic);

        assertNull(radio.getOptionFilter());
    }

    @Test
    void throws_if_component_not_found() {
        UniqueChoiceQuestion ucq = new UniqueChoiceQuestion();
        ucq.setId("Q3");
        ucq.setOptionSource("LOOP_VAR");
        CalculatedExpression filter = new CalculatedExpression();
        filter.setValue("AGE >= 18");
        ucq.setOptionFilter(filter);

        EnoQuestionnaire eno = new EnoQuestionnaire();
        eno.getSingleResponseQuestions().add(ucq);

        Questionnaire lunatic = new Questionnaire(); // Q3 missing

        LunaticInsertDynamicUCQOptionFilter step = new LunaticInsertDynamicUCQOptionFilter(eno);

        assertThrows(MappingException.class,
                () -> step.apply(lunatic)
        );
    }
}