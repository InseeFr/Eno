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
            Input input1 = new Input();
            Input input2 = new Input();
            Loop loop = new Loop();
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
            assertEquals("question", questionnaire.getPagination().value());
            assertEquals("1", questionnaire.getMaxPage());
            Loop loop = (Loop) questionnaire.getComponents().getFirst();
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
            assertEquals("sequence", questionnaire.getPagination().value());
            assertEquals("1", questionnaire.getMaxPage());
            Loop loop = (Loop) questionnaire.getComponents().getFirst();
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
            Input input1 = new Input();
            Sequence sequence2 = new Sequence();
            Input input2 = new Input();
            Loop loop = new Loop();
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
            assertEquals("question", questionnaire.getPagination().value());
            assertEquals("1", questionnaire.getMaxPage());
            Loop loop = (Loop) questionnaire.getComponents().getFirst();
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
            assertEquals("sequence", questionnaire.getPagination().value());
            assertEquals("1", questionnaire.getMaxPage());
            Loop loop = (Loop) questionnaire.getComponents().getFirst();
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
    class LoopOnSequenceMinMaxEquals {

        private Questionnaire questionnaire;

        @BeforeEach
        void createLoopWithSequence() {
            questionnaire = new Questionnaire();
            Sequence sequence = new Sequence();
            Input input1 = new Input();
            Loop loop = new Loop();
            LinesLoop linesLoop = new LinesLoop();
            LabelType minMax = new LabelType();
            minMax.setType(LabelTypeEnum.VTL);
            minMax.setValue("count(NAME)");
            linesLoop.setMin(minMax);
            linesLoop.setMax(minMax);
            loop.setLines(linesLoop);
            loop.getComponents().add(sequence);
            loop.getComponents().add(input1);
            questionnaire.getComponents().add(loop);
        }

        @Test
        void questionMode() {
            //
            new LunaticAddPageNumbers(LunaticParameters.LunaticPaginationMode.QUESTION).apply(questionnaire);
            //
            assertEquals("question", questionnaire.getPagination().value());
            Loop loop = (Loop) questionnaire.getComponents().getFirst();
            assertFalse(loop.getPaginatedLoop());
            assertNotNull(loop.getLines());
            assertNull(loop.getIterations());
            assertEquals(LabelTypeEnum.VTL, loop.getLines().getMin().getType());
            assertEquals(LabelTypeEnum.VTL, loop.getLines().getMax().getType());
            assertEquals("count(NAME)", loop.getLines().getMin().getValue());
            assertEquals("count(NAME)", loop.getLines().getMax().getValue());
        }

        @Test
        void sequenceMode() {
            //
            new LunaticAddPageNumbers(LunaticParameters.LunaticPaginationMode.SEQUENCE).apply(questionnaire);
            //
            assertEquals("sequence", questionnaire.getPagination().value());
            Loop loop = (Loop) questionnaire.getComponents().getFirst();
            assertTrue(loop.getPaginatedLoop());
            // Paginated loops have a "max page"
            assertNull(loop.getLines());
            assertNotNull(loop.getIterations());
            assertEquals(LabelTypeEnum.VTL, loop.getIterations().getType());
            assertEquals("count(NAME)", loop.getIterations().getValue());
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
