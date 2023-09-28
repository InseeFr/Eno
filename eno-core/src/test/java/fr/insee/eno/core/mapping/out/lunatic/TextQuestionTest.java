package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextQuestionTest {

    @Test
    void shortText_lunaticComponentType() {
        // With the current implementation, this can only be tested starting at the questionnaire level.
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        TextQuestion enoTextQuestion = new TextQuestion();
        enoTextQuestion.setLengthType(TextQuestion.LengthType.SHORT);
        enoQuestionnaire.getSingleResponseQuestions().add(enoTextQuestion);
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        //
        assertEquals(ComponentTypeEnum.INPUT, lunaticQuestionnaire.getComponents().get(0).getComponentType());
    }

    @Test
    void largeText_lunaticComponentType() {
        // With the current implementation, this can only be tested starting at the questionnaire level.
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        TextQuestion enoTextQuestion = new TextQuestion();
        enoTextQuestion.setLengthType(TextQuestion.LengthType.LONG);
        enoQuestionnaire.getSingleResponseQuestions().add(enoTextQuestion);
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        //
        assertEquals(ComponentTypeEnum.TEXTAREA, lunaticQuestionnaire.getComponents().get(0).getComponentType());
    }

}
