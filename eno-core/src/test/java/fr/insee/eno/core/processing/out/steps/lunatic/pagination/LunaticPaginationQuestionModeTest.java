package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticPaginationQuestionModeTest {

    @Test
    void loopPaginatedByOccurrence() {
        // Given
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Sequence sequence = new Sequence(); sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        InputNumber inputNumber = new InputNumber();
        Loop loop = new Loop(); loop.setComponentType(ComponentTypeEnum.LOOP);
        loop.setLines(new LinesLoop());
        loop.setIsPaginatedByIterations(true);
        Subsequence subsequence = new Subsequence(); subsequence.setComponentType(ComponentTypeEnum.SUBSEQUENCE);
        Input input1 = new Input();
        Input input2 = new Input();
        loop.getComponents().add(subsequence);
        loop.getComponents().add(input1);
        loop.getComponents().add(input2);
        lunaticQuestionnaire.getComponents().add(sequence);
        lunaticQuestionnaire.getComponents().add(inputNumber);
        lunaticQuestionnaire.getComponents().add(loop);
        Sequence sequence2 = new Sequence(); sequence2.setComponentType(ComponentTypeEnum.SEQUENCE);
        lunaticQuestionnaire.getComponents().add(sequence2);

        // When
        new LunaticPaginationQuestionMode().apply(lunaticQuestionnaire);

        // Not the direct purpose of this test, but it's related
        assertTrue(loop.getIsPaginatedByIterations());

        // Then
        assertEquals("1", lunaticQuestionnaire.getComponents().get(0).getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(1).getPage());
        assertEquals("3", lunaticQuestionnaire.getComponents().get(2).getPage());
        assertEquals("3.1", loop.getComponents().get(0).getPage());
        assertEquals("3.1", loop.getComponents().get(1).getPage());
        assertEquals("3.1", loop.getComponents().get(2).getPage());
        assertEquals("1", loop.getMaxPage());
        assertEquals("4", lunaticQuestionnaire.getComponents().get(3).getPage());
    }

}
