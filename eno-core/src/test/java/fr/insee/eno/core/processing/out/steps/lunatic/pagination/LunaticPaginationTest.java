package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.parameter.LunaticParameters;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Other tests on the Lunatic pagination.
 */
class LunaticPaginationTest {

    @Nested
    class LoopOnSequence {

        private Questionnaire questionnaire;
        private List<fr.insee.eno.core.model.navigation.Loop> enoLoops;

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
            loop.setId("loop-id");
            loop.setComponentType(ComponentTypeEnum.LOOP);
            loop.setLines(new LinesLoop());
            loop.getComponents().add(sequence);
            loop.getComponents().add(input1);
            loop.getComponents().add(input2);
            questionnaire.getComponents().add(loop);

            fr.insee.eno.core.model.navigation.Loop enoLoop = new StandaloneLoop();
            enoLoop.setId("loop-id");
            enoLoop.setOccurrencePagination(false);
            enoLoops = List.of(enoLoop);
        }

        @Test
        void questionMode() {
            //
            new LunaticAddPageNumbers(LunaticParameters.LunaticPaginationMode.QUESTION, enoLoops).apply(questionnaire);
            //
            assertEquals("question", questionnaire.getPaginationEnum().value());
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
            new LunaticAddPageNumbers(LunaticParameters.LunaticPaginationMode.SEQUENCE, enoLoops).apply(questionnaire);
            //
            assertEquals("sequence", questionnaire.getPaginationEnum().value());
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
        private List<fr.insee.eno.core.model.navigation.Loop> enoLoops;

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
            loop.setId("loop-id");
            loop.setComponentType(ComponentTypeEnum.LOOP);
            loop.setLines(new LinesLoop());
            loop.getComponents().add(sequence1);
            loop.getComponents().add(input1);
            loop.getComponents().add(sequence2);
            loop.getComponents().add(input2);
            questionnaire.getComponents().add(loop);

            fr.insee.eno.core.model.navigation.Loop enoLoop = new StandaloneLoop();
            enoLoop.setId("loop-id");
            enoLoop.setOccurrencePagination(false);
            enoLoops = List.of(enoLoop);
        }

        @Test
        void questionMode() {
            //
            new LunaticAddPageNumbers(LunaticParameters.LunaticPaginationMode.QUESTION, enoLoops).apply(questionnaire);
            //
            assertEquals("question", questionnaire.getPaginationEnum().value());
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
            new LunaticAddPageNumbers(LunaticParameters.LunaticPaginationMode.SEQUENCE, enoLoops).apply(questionnaire);
            //
            assertEquals("sequence", questionnaire.getPaginationEnum().value());
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
        private List<fr.insee.eno.core.model.navigation.Loop> enoLoops;

        @BeforeEach
        void createLoopWithSequence() {
            questionnaire = new Questionnaire();
            Sequence sequence = new Sequence();
            sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
            Input input1 = new Input();
            input1.setComponentType(ComponentTypeEnum.INPUT);
            Loop loop = new Loop();
            loop.setId("loop-id");
            loop.setComponentType(ComponentTypeEnum.LOOP);
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

            fr.insee.eno.core.model.navigation.Loop enoLoop = new StandaloneLoop();
            enoLoop.setId("loop-id");
            enoLoop.setOccurrencePagination(false);
            enoLoops = List.of(enoLoop);
        }

        @Test
        void questionMode() {
            //
            new LunaticAddPageNumbers(LunaticParameters.LunaticPaginationMode.QUESTION, enoLoops).apply(questionnaire);
            //
            assertEquals("question", questionnaire.getPaginationEnum().value());
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
            new LunaticAddPageNumbers(LunaticParameters.LunaticPaginationMode.SEQUENCE, enoLoops).apply(questionnaire);
            //
            assertEquals("sequence", questionnaire.getPaginationEnum().value());
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
