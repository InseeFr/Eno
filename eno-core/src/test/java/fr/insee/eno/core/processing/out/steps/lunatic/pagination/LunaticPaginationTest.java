package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.eno.core.parameter.LunaticParameters;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Other tests on the Lunatic pagination.
 */
class LunaticPaginationTest {

    @Nested
    class LoopOnSequence {

        private Questionnaire questionnaire;

        @BeforeEach
        void createLoopWithSequence() {
            //
            questionnaire = new Questionnaire();
            Sequence sequence = new Sequence();
            sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
            Input input1 = new Input();
            input1.setComponentType(ComponentTypeEnum.INPUT);
            Input input2 = new Input();
            input2.setComponentType(ComponentTypeEnum.INPUT);
            Loop loop = new Loop();
            loop.setComponentType(ComponentTypeEnum.LOOP);
            loop.getComponents().add(sequence);
            loop.getComponents().add(input1);
            loop.getComponents().add(input2);
            questionnaire.getComponents().add(loop);
        }

        @Test
        void questionMode() {
            //
            new LunaticAddPageNumbers(LunaticParameters.LunaticPaginationMode.QUESTION).apply(questionnaire);
            //
            assertEquals("question", questionnaire.getPagination());
            assertEquals("1", questionnaire.getMaxPage());
            Loop loop = (Loop) questionnaire.getComponents().get(0);
            assertTrue(loop.getPaginatedLoop());
            assertEquals("3", loop.getMaxPage());
            assertEquals("1.1", loop.getComponents().get(0).getPage());
            assertEquals("1.2", loop.getComponents().get(1).getPage());
            assertEquals("1.3", loop.getComponents().get(2).getPage());
        }

        @Test
        void sequenceMode() {
            //
            new LunaticAddPageNumbers(LunaticParameters.LunaticPaginationMode.SEQUENCE).apply(questionnaire);
            //
            assertEquals("sequence", questionnaire.getPagination());
            assertEquals("1", questionnaire.getMaxPage());
            Loop loop = (Loop) questionnaire.getComponents().get(0);
            assertTrue(loop.getPaginatedLoop());
            assertEquals("1", loop.getMaxPage());
            loop.getComponents().forEach(loopComponent ->
                    assertEquals("1.1", loopComponent.getPage()));
        }
    }

    @Nested
    class LoopOnSubsequence {
        private Questionnaire questionnaire;
        @BeforeEach
        void createLoopWithSubsequence() {
            questionnaire = new Questionnaire();
        }
        @Test
        void questionMode() {
            assertNotNull(questionnaire);
        }
    }

}
