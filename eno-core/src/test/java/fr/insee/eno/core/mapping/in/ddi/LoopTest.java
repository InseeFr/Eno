package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.processing.in.steps.ddi.DDIResolveVariableReferencesInExpressions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoopTest {

    @Nested
    class SequenceLoopsTest {

        private static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            // Given + when
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIDeserializer.deserialize(LoopTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loops-sequence.xml")),
                    enoQuestionnaire);
            new DDIResolveVariableReferencesInExpressions().apply(enoQuestionnaire);
            // Then
            // -> tests
        }

        @Test
        void loopCount() {
            assertEquals(4, enoQuestionnaire.getLoops().size());
        }

        @Test
        void loopItems() {
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(0).getLoopItems().size());
            assertEquals(ItemReference.ItemType.SEQUENCE,
                    enoQuestionnaire.getLoops().get(0).getLoopItems().get(0).getType());
            assertEquals(enoQuestionnaire.getSequences().get(0).getId(),
                    enoQuestionnaire.getLoops().get(0).getLoopItems().get(0).getId());
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(1).getLoopItems().size());
            assertEquals(ItemReference.ItemType.SEQUENCE,
                    enoQuestionnaire.getLoops().get(1).getLoopItems().get(0).getType());
            assertEquals(enoQuestionnaire.getSequences().get(1).getId(),
                    enoQuestionnaire.getLoops().get(1).getLoopItems().get(0).getId());
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(2).getLoopItems().size());
            assertEquals(ItemReference.ItemType.SEQUENCE,
                    enoQuestionnaire.getLoops().get(2).getLoopItems().get(0).getType());
            assertEquals(enoQuestionnaire.getSequences().get(3).getId(),
                    enoQuestionnaire.getLoops().get(2).getLoopItems().get(0).getId());
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(3).getLoopItems().size());
            assertEquals(ItemReference.ItemType.SEQUENCE,
                    enoQuestionnaire.getLoops().get(3).getLoopItems().get(0).getType());
            assertEquals(enoQuestionnaire.getSequences().get(4).getId(),
                    enoQuestionnaire.getLoops().get(3).getLoopItems().get(0).getId());
        }

        @Test
        void standaloneLoops() {
            List<StandaloneLoop> standaloneLoops = enoQuestionnaire.getLoops().stream()
                    .filter(StandaloneLoop.class::isInstance).map(StandaloneLoop.class::cast).toList();
            //
            assertEquals(2, standaloneLoops.size());
            //
            standaloneLoops.forEach(standaloneLoop ->
                    assertEquals("\"Add\"", standaloneLoop.getAddButtonLabel().getValue()));
            //
            assertEquals("1", standaloneLoops.get(0).getMinIteration().getValue());
            assertEquals("3", standaloneLoops.get(0).getMaxIteration().getValue());
            assertEquals("nvl( MIN_OCC , 1)", standaloneLoops.get(1).getMinIteration().getValue());
            assertEquals("nvl( MAX_OCC , 1)", standaloneLoops.get(1).getMaxIteration().getValue());
        }

        @Test
        void linkedLoops() {
            List<LinkedLoop> linkedLoops = enoQuestionnaire.getLoops().stream()
                    .filter(LinkedLoop.class::isInstance).map(LinkedLoop.class::cast).toList();
            //
            assertEquals(2, linkedLoops.size());
            //
            assertEquals(enoQuestionnaire.getLoops().get(0).getId(), linkedLoops.get(0).getReference());
            assertEquals(enoQuestionnaire.getLoops().get(2).getId(), linkedLoops.get(1).getReference());
        }

    }

    @Nested
    class SubsequenceLoopsTest {

        private static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            // Given + when
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIDeserializer.deserialize(LoopTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loops-subsequence.xml")),
                    enoQuestionnaire);
            new DDIResolveVariableReferencesInExpressions().apply(enoQuestionnaire);
            // Then
            // -> tests
        }

        @Test
        void loopCount() {
            assertEquals(4, enoQuestionnaire.getLoops().size());
        }

        @Test
        void loopItems() {
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(0).getLoopItems().size());
            assertEquals(ItemReference.ItemType.SUBSEQUENCE,
                    enoQuestionnaire.getLoops().get(0).getLoopItems().get(0).getType());
            assertEquals(enoQuestionnaire.getSubsequences().get(0).getId(),
                    enoQuestionnaire.getLoops().get(0).getLoopItems().get(0).getId());
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(1).getLoopItems().size());
            assertEquals(ItemReference.ItemType.SUBSEQUENCE,
                    enoQuestionnaire.getLoops().get(1).getLoopItems().get(0).getType());
            assertEquals(enoQuestionnaire.getSubsequences().get(1).getId(),
                    enoQuestionnaire.getLoops().get(1).getLoopItems().get(0).getId());
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(2).getLoopItems().size());
            assertEquals(ItemReference.ItemType.SUBSEQUENCE,
                    enoQuestionnaire.getLoops().get(2).getLoopItems().get(0).getType());
            assertEquals(enoQuestionnaire.getSubsequences().get(2).getId(),
                    enoQuestionnaire.getLoops().get(2).getLoopItems().get(0).getId());
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(3).getLoopItems().size());
            assertEquals(ItemReference.ItemType.SUBSEQUENCE,
                    enoQuestionnaire.getLoops().get(3).getLoopItems().get(0).getType());
            assertEquals(enoQuestionnaire.getSubsequences().get(3).getId(),
                    enoQuestionnaire.getLoops().get(3).getLoopItems().get(0).getId());
        }

        @Test
        void standaloneLoops() {
            List<StandaloneLoop> standaloneLoops = enoQuestionnaire.getLoops().stream()
                    .filter(StandaloneLoop.class::isInstance).map(StandaloneLoop.class::cast).toList();
            //
            assertEquals(2, standaloneLoops.size());
            //
            standaloneLoops.forEach(standaloneLoop ->
                    assertEquals("\"Add\"", standaloneLoop.getAddButtonLabel().getValue()));
            //
            assertEquals("1", standaloneLoops.get(0).getMinIteration().getValue());
            assertEquals("3", standaloneLoops.get(0).getMaxIteration().getValue());
            assertEquals("nvl( MIN_OCC , 1)", standaloneLoops.get(1).getMinIteration().getValue());
            assertEquals("nvl( MAX_OCC , 1)", standaloneLoops.get(1).getMaxIteration().getValue());
        }

        @Test
        void linkedLoops() {
            List<LinkedLoop> linkedLoops = enoQuestionnaire.getLoops().stream()
                    .filter(LinkedLoop.class::isInstance).map(LinkedLoop.class::cast).toList();
            //
            assertEquals(2, linkedLoops.size());
            //
            assertEquals(enoQuestionnaire.getLoops().get(0).getId(), linkedLoops.get(0).getReference());
            assertEquals(enoQuestionnaire.getLoops().get(2).getId(), linkedLoops.get(1).getReference());
        }

    }

    @Nested
    class SequenceExtendedLoopsTest {

        private static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            // Given + when
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIDeserializer.deserialize(LoopTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loops-extended-sequence.xml")),
                    enoQuestionnaire);
            new DDIResolveVariableReferencesInExpressions().apply(enoQuestionnaire);
            // Then
            // -> tests
        }

        @Test
        void loopCount() {
            assertEquals(2, enoQuestionnaire.getLoops().size());
        }

        @Test
        void loopItemsOfMainLoop() {
            Loop loop = enoQuestionnaire.getLoops().get(0);
            //
            assertEquals(3, loop.getLoopItems().size());
            //
            loop.getLoopItems().forEach(itemReference ->
                    assertEquals(ItemReference.ItemType.SEQUENCE, itemReference.getType()));
            //
            assertEquals(enoQuestionnaire.getSequences().get(0).getId(), loop.getLoopItems().get(0).getId());
            assertEquals(enoQuestionnaire.getSequences().get(1).getId(), loop.getLoopItems().get(1).getId());
            assertEquals(enoQuestionnaire.getSequences().get(2).getId(), loop.getLoopItems().get(2).getId());
        }

        @Test
        void loopItemsOfLinkedLoop() {
            Loop loop = enoQuestionnaire.getLoops().get(1);
            //
            assertEquals(3, loop.getLoopItems().size());
            //
            loop.getLoopItems().forEach(itemReference ->
                    assertEquals(ItemReference.ItemType.SEQUENCE, itemReference.getType()));
            //
            assertEquals(enoQuestionnaire.getSequences().get(3).getId(), loop.getLoopItems().get(0).getId());
            assertEquals(enoQuestionnaire.getSequences().get(4).getId(), loop.getLoopItems().get(1).getId());
            assertEquals(enoQuestionnaire.getSequences().get(5).getId(), loop.getLoopItems().get(2).getId());
        }

        @Test
        void standaloneLoops() {
            StandaloneLoop standaloneLoop = (StandaloneLoop) enoQuestionnaire.getLoops().get(0);
            //
            assertEquals("\"Add\"", standaloneLoop.getAddButtonLabel().getValue());
            //
            assertEquals("1", standaloneLoop.getMinIteration().getValue());
            assertEquals("5", standaloneLoop.getMaxIteration().getValue());
        }

        @Test
        void linkedLoops() {
            LinkedLoop linkedLoop = (LinkedLoop) enoQuestionnaire.getLoops().get(1);
            //
            assertEquals(enoQuestionnaire.getLoops().get(0).getId(), linkedLoop.getReference());
        }

    }

    @Nested
    class SubsequenceExtendedLoopsTest {

        private static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            // Given + when
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIDeserializer.deserialize(LoopTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loops-extended-subsequence.xml")),
                    enoQuestionnaire);
            new DDIResolveVariableReferencesInExpressions().apply(enoQuestionnaire);
            // Then
            // -> tests
        }

        @Test
        void loopCount() {
            assertEquals(2, enoQuestionnaire.getLoops().size());
        }

        @Test
        void loopItemsOfMainLoop() {
            Loop loop = enoQuestionnaire.getLoops().get(0);
            //
            assertEquals(3, loop.getLoopItems().size());
            //
            loop.getLoopItems().forEach(itemReference ->
                    assertEquals(ItemReference.ItemType.SUBSEQUENCE, itemReference.getType()));
            //
            assertEquals(enoQuestionnaire.getSubsequences().get(0).getId(), loop.getLoopItems().get(0).getId());
            assertEquals(enoQuestionnaire.getSubsequences().get(1).getId(), loop.getLoopItems().get(1).getId());
            assertEquals(enoQuestionnaire.getSubsequences().get(2).getId(), loop.getLoopItems().get(2).getId());
        }

        @Test
        void loopItemsOfLinkedLoop() {
            Loop loop = enoQuestionnaire.getLoops().get(1);
            //
            assertEquals(3, loop.getLoopItems().size());
            //
            loop.getLoopItems().forEach(itemReference ->
                    assertEquals(ItemReference.ItemType.SUBSEQUENCE, itemReference.getType()));
            //
            assertEquals(enoQuestionnaire.getSubsequences().get(3).getId(), loop.getLoopItems().get(0).getId());
            assertEquals(enoQuestionnaire.getSubsequences().get(4).getId(), loop.getLoopItems().get(1).getId());
            assertEquals(enoQuestionnaire.getSubsequences().get(5).getId(), loop.getLoopItems().get(2).getId());
        }

        @Test
        void standaloneLoops() {
            StandaloneLoop standaloneLoop = (StandaloneLoop) enoQuestionnaire.getLoops().get(0);
            //
            assertEquals("\"Add\"", standaloneLoop.getAddButtonLabel().getValue());
            //
            assertEquals("1", standaloneLoop.getMinIteration().getValue());
            assertEquals("5", standaloneLoop.getMaxIteration().getValue());
        }

        @Test
        void linkedLoops() {
            LinkedLoop linkedLoop = (LinkedLoop) enoQuestionnaire.getLoops().get(1);
            //
            assertEquals(enoQuestionnaire.getLoops().get(0).getId(), linkedLoop.getReference());
        }

    }

}
