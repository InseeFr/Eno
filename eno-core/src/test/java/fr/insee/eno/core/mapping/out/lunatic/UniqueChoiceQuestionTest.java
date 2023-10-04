package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(ComponentTypeEnum.RADIO, lunaticQuestionnaire.getComponents().get(0).getComponentType());
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
        assertEquals(ComponentTypeEnum.CHECKBOX_ONE, lunaticQuestionnaire.getComponents().get(0).getComponentType());
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
        assertEquals(ComponentTypeEnum.DROPDOWN, lunaticQuestionnaire.getComponents().get(0).getComponentType());
    }

}
