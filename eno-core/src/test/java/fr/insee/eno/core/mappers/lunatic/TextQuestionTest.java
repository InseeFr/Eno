package fr.insee.eno.core.mappers.lunatic;

import fr.insee.eno.core.mappers.MapperTestUtils;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextQuestionTest {

    private final MapperTestUtils testUtils = new MapperTestUtils();

    @Test
    void shortText_lunaticComponentType() {
        // With the current implementation, this can only be tested starting at the questionnaire level.
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        TextQuestion enoTextQuestion = new TextQuestion();
        enoTextQuestion.setMaxLength(BigInteger.valueOf(249));
        enoQuestionnaire.getSingleResponseQuestions().add(enoTextQuestion);
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        testUtils.mapLunaticProperty(enoQuestionnaire, lunaticQuestionnaire, "singleResponseQuestions");
        //
        assertEquals(ComponentTypeEnum.INPUT, lunaticQuestionnaire.getComponents().get(0).getComponentType());
    }

    @Test
    void largeText_lunaticComponentType() {
        // With the current implementation, this can only be tested starting at the questionnaire level.
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        TextQuestion enoTextQuestion = new TextQuestion();
        enoTextQuestion.setMaxLength(BigInteger.valueOf(250));
        enoQuestionnaire.getSingleResponseQuestions().add(enoTextQuestion);
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        testUtils.mapLunaticProperty(enoQuestionnaire, lunaticQuestionnaire, "singleResponseQuestions");
        //
        assertEquals(ComponentTypeEnum.TEXTAREA, lunaticQuestionnaire.getComponents().get(0).getComponentType());
    }

}
