package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UniqueChoiceQuestionTest {

    @Test
    void radio_lunaticComponentType() {
        // With the current implementation, this can only be tested starting at the questionnaire level.
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        UniqueChoiceQuestion enoUniqueChoiceQuestion = new UniqueChoiceQuestion();
        enoUniqueChoiceQuestion.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.RADIO);
        enoQuestionnaire.getSingleResponseQuestions().add(enoUniqueChoiceQuestion);
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        //
        assertEquals(ComponentTypeEnum.RADIO, lunaticQuestionnaire.getComponents().getFirst().getComponentType());
    }

    @Test
    void checkbox_lunaticComponentType() {
        // With the current implementation, this can only be tested starting at the questionnaire level.
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        UniqueChoiceQuestion enoUniqueChoiceQuestion = new UniqueChoiceQuestion();
        enoUniqueChoiceQuestion.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.CHECKBOX);
        enoQuestionnaire.getSingleResponseQuestions().add(enoUniqueChoiceQuestion);
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        //
        assertEquals(ComponentTypeEnum.CHECKBOX_ONE, lunaticQuestionnaire.getComponents().getFirst().getComponentType());
    }

    @Test
    void dropdown_lunaticComponentType() {
        // With the current implementation, this can only be tested starting at the questionnaire level.
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        UniqueChoiceQuestion enoUniqueChoiceQuestion = new UniqueChoiceQuestion();
        enoUniqueChoiceQuestion.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.DROPDOWN);
        enoQuestionnaire.getSingleResponseQuestions().add(enoUniqueChoiceQuestion);
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        //
        assertEquals(ComponentTypeEnum.DROPDOWN, lunaticQuestionnaire.getComponents().getFirst().getComponentType());
    }

    @Test
    void dynamic_ucq_sets_option_source_dropdown() {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        UniqueChoiceQuestion enoUniqueChoiceQuestion = new UniqueChoiceQuestion();
        enoUniqueChoiceQuestion.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.DROPDOWN);
        enoUniqueChoiceQuestion.setOptionSource("LOOP_VAR");
        enoQuestionnaire.getSingleResponseQuestions().add(enoUniqueChoiceQuestion);
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        Dropdown component = (Dropdown) lunaticQuestionnaire.getComponents().getFirst();
        //
        assertInstanceOf(Dropdown.class, lunaticQuestionnaire.getComponents().getFirst());
        assertEquals("LOOP_VAR", component.getOptionSource());
    }

    @Test
    void dynamic_ucq_sets_option_source_radio() {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        UniqueChoiceQuestion enoUniqueChoiceQuestion = new UniqueChoiceQuestion();
        enoUniqueChoiceQuestion.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.RADIO);
        enoUniqueChoiceQuestion.setOptionSource("LOOP_VAR");
        enoQuestionnaire.getSingleResponseQuestions().add(enoUniqueChoiceQuestion);
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        Radio component = (Radio) lunaticQuestionnaire.getComponents().getFirst();
        //
        assertInstanceOf(Radio.class, lunaticQuestionnaire.getComponents().getFirst());
        assertEquals("LOOP_VAR", component.getOptionSource());
    }

    @Test
    void dynamic_ucq_sets_option_source_checkboxOne() {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        UniqueChoiceQuestion enoUniqueChoiceQuestion = new UniqueChoiceQuestion();
        enoUniqueChoiceQuestion.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.CHECKBOX);
        enoUniqueChoiceQuestion.setOptionSource("LOOP_VAR");
        enoQuestionnaire.getSingleResponseQuestions().add(enoUniqueChoiceQuestion);
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        CheckboxOne component = (CheckboxOne) lunaticQuestionnaire.getComponents().getFirst();
        //
        assertInstanceOf(CheckboxOne.class, lunaticQuestionnaire.getComponents().getFirst());
        assertEquals("LOOP_VAR", component.getOptionSource());
    }

    @Test
    void dynamic_ucq_has_no_static_options() {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        UniqueChoiceQuestion enoUniqueChoiceQuestion = new UniqueChoiceQuestion();
        enoUniqueChoiceQuestion.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.RADIO);
        enoUniqueChoiceQuestion.setOptionSource("LOOP_VAR");
        enoQuestionnaire.getSingleResponseQuestions().add(enoUniqueChoiceQuestion);
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        Radio radio = (Radio) lunaticQuestionnaire.getComponents().getFirst();
        //
        assertEquals(0, radio.getOptions().size());
    }

    @Test
    void dynamic_ucq_sets_option_filter() {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        UniqueChoiceQuestion enoUniqueChoiceQuestion = new UniqueChoiceQuestion();
        enoUniqueChoiceQuestion.setDisplayFormat(UniqueChoiceQuestion.DisplayFormat.DROPDOWN);
        enoUniqueChoiceQuestion.setOptionSource("LOOP_VAR");
        CalculatedExpression filter = new CalculatedExpression();
        filter.setValue("AGE >= 18");
        enoUniqueChoiceQuestion.setOptionFilter(filter);
        enoQuestionnaire.getSingleResponseQuestions().add(enoUniqueChoiceQuestion);
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        Dropdown dropdown = (Dropdown) lunaticQuestionnaire.getComponents().getFirst();
        //
        assertEquals("AGE >= 18", dropdown.getOptionFilter().getValue());
    }
}
