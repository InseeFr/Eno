package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.eno.core.PoguesDDIToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.parameter.LunaticParameters;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticLoopResolution;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSortComponents;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void loopPaginatedByOccurrences_integrationTest() throws ParsingException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        // Given
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        EnoQuestionnaire enoQuestionnaire = PoguesDDIToEno.fromInputStreams(
                classLoader.getResourceAsStream("integration/pogues/pogues-loop-paginated-occurrence.json"),
                classLoader.getResourceAsStream("integration/ddi/ddi-loop-paginated-occurrence.xml")
        ).transform(enoParameters);
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);

        // When
        new LunaticAddPageNumbers(LunaticParameters.LunaticPaginationMode.QUESTION).apply(lunaticQuestionnaire);

        // Then
        assertEquals("1", lunaticQuestionnaire.getComponents().get(0).getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(1).getPage());

        Loop loop1 = (Loop) lunaticQuestionnaire.getComponents().get(2);
        assertEquals("3", loop1.getPage());
        assertEquals("3.1", loop1.getComponents().get(0).getPage());
        assertEquals("3.1", loop1.getComponents().get(1).getPage());
        assertEquals("3.1", loop1.getComponents().get(2).getPage());
        assertEquals("1", loop1.getMaxPage());

        Loop loop2 = (Loop) lunaticQuestionnaire.getComponents().get(3);
        assertEquals("4", loop2.getPage());
        assertEquals("4", loop2.getComponents().get(0).getPage());
        assertEquals("4", loop2.getComponents().get(1).getPage());
        assertEquals("4", loop2.getComponents().get(2).getPage());
        assertNull(loop2.getMaxPage());

        assertEquals("5", lunaticQuestionnaire.getComponents().get(4).getPage());
    }

}
