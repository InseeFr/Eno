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
            questionnaire = new Questionnaire();
            Sequence sequence = new Sequence();
            sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
            Input input1 = new Input();
            input1.setComponentType(ComponentTypeEnum.INPUT);
            Input input2 = new Input();
            input2.setComponentType(ComponentTypeEnum.INPUT);
            Loop loop = new Loop();
            loop.setComponentType(ComponentTypeEnum.LOOP);
            loop.setLines(new LinesLoop());
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
            assertFalse(loop.getPaginatedLoop());
            // Non paginated loops don't have a "max page" property
            assertNull(loop.getMaxPage());
            // Non paginated loops  don't use "dotted" numbering
            loop.getComponents().forEach(loopComponent ->
                    assertEquals("1", loopComponent.getPage()));
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
            // Paginated loops have a "max page"
            assertEquals("1", loop.getMaxPage());
            // Sequence mode: each component of the sequence is on the same page
            loop.getComponents().forEach(loopComponent ->
                    assertEquals("1.1", loopComponent.getPage()));
        }
    }

    @Nested
    class LoopOnTwoSequences {

        private Questionnaire questionnaire;

        @BeforeEach
        void createLoopWithTwoSequences() {
            questionnaire = new Questionnaire();
            Sequence sequence1 = new Sequence();
            sequence1.setComponentType(ComponentTypeEnum.SEQUENCE);
            Input input1 = new Input();
            input1.setComponentType(ComponentTypeEnum.INPUT);
            Sequence sequence2 = new Sequence();
            sequence2.setComponentType(ComponentTypeEnum.SEQUENCE);
            Input input2 = new Input();
            input2.setComponentType(ComponentTypeEnum.INPUT);
            Loop loop = new Loop();
            loop.setComponentType(ComponentTypeEnum.LOOP);
            loop.setLines(new LinesLoop());
            loop.getComponents().add(sequence1);
            loop.getComponents().add(input1);
            loop.getComponents().add(sequence2);
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
            assertFalse(loop.getPaginatedLoop());
            // Non paginated loops don't have a "max page" property
            assertNull(loop.getMaxPage());
            // Non paginated loops  don't use "dotted" numbering
            loop.getComponents().forEach(loopComponent ->
                    assertEquals("1", loopComponent.getPage()));
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
            // Paginated loops have a "max page"
            assertEquals("2", loop.getMaxPage());
            // Sequence mode: each component of the sequence is on the same page
            assertEquals("1.1", loop.getComponents().get(0).getPage());
            assertEquals("1.1", loop.getComponents().get(1).getPage());
            assertEquals("1.2", loop.getComponents().get(2).getPage());
            assertEquals("1.2", loop.getComponents().get(3).getPage());
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
