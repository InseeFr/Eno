package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference.StructureItemType;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DDIResolveLoopsScopeTest {

    @Nested
    class SequenceLoopsTest {

        private static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            // Given
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIDeserializer.deserialize(DDIResolveLoopsScopeTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loops-sequence.xml")),
                    enoQuestionnaire);
            // When
            new DDIResolveLoopsScope().apply(enoQuestionnaire);
            // Then
            // -> tests
        }

        @Test
        void loopScope() {
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(0).getLoopScope().size());
            assertEquals(StructureItemType.SEQUENCE,
                    enoQuestionnaire.getLoops().get(0).getLoopScope().get(0).getType());
            assertEquals(enoQuestionnaire.getSequences().get(0).getId(),
                    enoQuestionnaire.getLoops().get(0).getLoopScope().get(0).getId());
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(1).getLoopScope().size());
            assertEquals(StructureItemType.SEQUENCE,
                    enoQuestionnaire.getLoops().get(1).getLoopScope().get(0).getType());
            assertEquals(enoQuestionnaire.getSequences().get(1).getId(),
                    enoQuestionnaire.getLoops().get(1).getLoopScope().get(0).getId());
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(2).getLoopScope().size());
            assertEquals(StructureItemType.SEQUENCE,
                    enoQuestionnaire.getLoops().get(2).getLoopScope().get(0).getType());
            assertEquals(enoQuestionnaire.getSequences().get(3).getId(),
                    enoQuestionnaire.getLoops().get(2).getLoopScope().get(0).getId());
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(3).getLoopScope().size());
            assertEquals(StructureItemType.SEQUENCE,
                    enoQuestionnaire.getLoops().get(3).getLoopScope().get(0).getType());
            assertEquals(enoQuestionnaire.getSequences().get(4).getId(),
                    enoQuestionnaire.getLoops().get(3).getLoopScope().get(0).getId());
        }

    }

    @Nested
    class SubsequenceLoopsTest {

        private static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            // Given
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIDeserializer.deserialize(DDIResolveLoopsScopeTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loops-subsequence.xml")),
                    enoQuestionnaire);
            // When
            new DDIResolveLoopsScope().apply(enoQuestionnaire);
            // Then
            // -> tests
        }

        @Test
        void loopScope() {
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(0).getLoopScope().size());
            assertEquals(StructureItemType.SUBSEQUENCE,
                    enoQuestionnaire.getLoops().get(0).getLoopScope().get(0).getType());
            assertEquals(enoQuestionnaire.getSubsequences().get(0).getId(),
                    enoQuestionnaire.getLoops().get(0).getLoopScope().get(0).getId());
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(1).getLoopScope().size());
            assertEquals(StructureItemType.SUBSEQUENCE,
                    enoQuestionnaire.getLoops().get(1).getLoopScope().get(0).getType());
            assertEquals(enoQuestionnaire.getSubsequences().get(1).getId(),
                    enoQuestionnaire.getLoops().get(1).getLoopScope().get(0).getId());
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(2).getLoopScope().size());
            assertEquals(StructureItemType.SUBSEQUENCE,
                    enoQuestionnaire.getLoops().get(2).getLoopScope().get(0).getType());
            assertEquals(enoQuestionnaire.getSubsequences().get(2).getId(),
                    enoQuestionnaire.getLoops().get(2).getLoopScope().get(0).getId());
            //
            assertEquals(1, enoQuestionnaire.getLoops().get(3).getLoopScope().size());
            assertEquals(StructureItemType.SUBSEQUENCE,
                    enoQuestionnaire.getLoops().get(3).getLoopScope().get(0).getType());
            assertEquals(enoQuestionnaire.getSubsequences().get(3).getId(),
                    enoQuestionnaire.getLoops().get(3).getLoopScope().get(0).getId());
        }

    }

    @Nested
    class SequenceExtendedLoopsTest {

        private static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            // Given
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIDeserializer.deserialize(DDIResolveLoopsScopeTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loops-extended-sequence.xml")),
                    enoQuestionnaire);
            // When
            new DDIResolveLoopsScope().apply(enoQuestionnaire);
            // Then
            // -> tests
        }

        @Test
        void loopScopeOfMainLoop() {
            Loop loop = enoQuestionnaire.getLoops().get(0);
            //
            assertEquals(3, loop.getLoopScope().size());
            //
            loop.getLoopScope().forEach(structureItemReference ->
                    assertEquals(StructureItemType.SEQUENCE, structureItemReference.getType()));
            //
            assertEquals(enoQuestionnaire.getSequences().get(0).getId(), loop.getLoopScope().get(0).getId());
            assertEquals(enoQuestionnaire.getSequences().get(1).getId(), loop.getLoopScope().get(1).getId());
            assertEquals(enoQuestionnaire.getSequences().get(2).getId(), loop.getLoopScope().get(2).getId());
        }

        @Test
        void loopScopeOfLinkedLoop() {
            Loop loop = enoQuestionnaire.getLoops().get(1);
            //
            assertEquals(3, loop.getLoopScope().size());
            //
            loop.getLoopScope().forEach(structureItemReference ->
                    assertEquals(StructureItemType.SEQUENCE, structureItemReference.getType()));
            //
            assertEquals(enoQuestionnaire.getSequences().get(3).getId(), loop.getLoopScope().get(0).getId());
            assertEquals(enoQuestionnaire.getSequences().get(4).getId(), loop.getLoopScope().get(1).getId());
            assertEquals(enoQuestionnaire.getSequences().get(5).getId(), loop.getLoopScope().get(2).getId());
        }

    }

    @Nested
    class SubsequenceExtendedLoopsTest {

        private static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            // Given
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIDeserializer.deserialize(DDIResolveLoopsScopeTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loops-extended-subsequence.xml")),
                    enoQuestionnaire);
            // When
            new DDIResolveLoopsScope().apply(enoQuestionnaire);
            // Then
            // -> tests
        }

        @Test
        void loopScopeOfMainLoop() {
            Loop loop = enoQuestionnaire.getLoops().get(0);
            //
            assertEquals(3, loop.getLoopScope().size());
            //
            loop.getLoopScope().forEach(structureItemReference ->
                    assertEquals(StructureItemType.SUBSEQUENCE, structureItemReference.getType()));
            //
            assertEquals(enoQuestionnaire.getSubsequences().get(0).getId(), loop.getLoopScope().get(0).getId());
            assertEquals(enoQuestionnaire.getSubsequences().get(1).getId(), loop.getLoopScope().get(1).getId());
            assertEquals(enoQuestionnaire.getSubsequences().get(2).getId(), loop.getLoopScope().get(2).getId());
        }

        @Test
        void loopScopeOfLinkedLoop() {
            Loop loop = enoQuestionnaire.getLoops().get(1);
            //
            assertEquals(3, loop.getLoopScope().size());
            //
            loop.getLoopScope().forEach(structureItemReference ->
                    assertEquals(StructureItemType.SUBSEQUENCE, structureItemReference.getType()));
            //
            assertEquals(enoQuestionnaire.getSubsequences().get(3).getId(), loop.getLoopScope().get(0).getId());
            assertEquals(enoQuestionnaire.getSubsequences().get(4).getId(), loop.getLoopScope().get(1).getId());
            assertEquals(enoQuestionnaire.getSubsequences().get(5).getId(), loop.getLoopScope().get(2).getId());
        }

    }

    @Nested
    class LoopWithExceptTest {

        @Test
        void scopeOfLoopContainingFilter() throws DDIParsingException {
            // Given
            EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIDeserializer.deserialize(DDIResolveLoopsScopeTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loop-except.xml")),
                    enoQuestionnaire);
            // When
            new DDIResolveLoopsScope().apply(enoQuestionnaire);
            // Then
            Optional<LinkedLoop> linkedLoop = enoQuestionnaire.getLoops().stream()
                    .filter(LinkedLoop.class::isInstance).map(LinkedLoop.class::cast).findAny();
            assert linkedLoop.isPresent(); // (not the purpose of this test)
            assertEquals(1, linkedLoop.get().getLoopScope().size());
            assertEquals(StructureItemType.SEQUENCE, linkedLoop.get().getLoopScope().get(0).getType());
            assertEquals(enoQuestionnaire.getSequences().get(2).getId(),
                    linkedLoop.get().getLoopScope().get(0).getId());
        }
    }
    
}
