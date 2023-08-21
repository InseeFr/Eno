package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.ItemReference.ItemType;
import fr.insee.eno.core.model.sequence.StructureItemReference.StructureItemType;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.in.steps.ddi.DDIResolveSequencesStructure;
import fr.insee.eno.core.reference.EnoIndex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DDIResolveSequencesStructureTest {

    private static final String SEQUENCE_ID = "sequence-id";
    private static final String SUBSEQUENCE_ID = "subsequence-id";
    private static final String QUESTION_ID = "question-id";
    private static final String LOOP_ID = "loop-id";
    private static final String FILTER_ID = "filter-id";

    @Test
    @DisplayName("1 sequence, 1 question")
    void test01() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        Sequence sequence = new Sequence();
        sequence.setId(SEQUENCE_ID);
        sequence.getSequenceItems().add(
                ItemReference.builder().id(QUESTION_ID).type(ItemType.QUESTION).build());
        enoQuestionnaire.getSequences().add(sequence);
        //
        DDIResolveSequencesStructure ddiResolveSequencesStructure = new DDIResolveSequencesStructure();
        ddiResolveSequencesStructure.apply(enoQuestionnaire);
        //
        assertEquals(1, sequence.getSequenceStructure().size());
        assertEquals(QUESTION_ID, sequence.getSequenceStructure().get(0).getId());
        assertEquals(StructureItemType.QUESTION, sequence.getSequenceStructure().get(0).getType());
    }

    @Test
    @DisplayName("1 sequence, 1 subsequence, 1 question")
    void test02() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        Sequence sequence = new Sequence();
        sequence.setId(SEQUENCE_ID);
        sequence.getSequenceItems().add(
                ItemReference.builder().id(SUBSEQUENCE_ID).type(ItemType.SUBSEQUENCE).build());
        enoQuestionnaire.getSequences().add(sequence);
        //
        Subsequence subsequence = new Subsequence();
        subsequence.setId(SUBSEQUENCE_ID);
        subsequence.getSequenceItems().add(
                ItemReference.builder().id(QUESTION_ID).type(ItemType.QUESTION).build());
        enoQuestionnaire.getSubsequences().add(subsequence);
        //
        EnoIndex enoIndex = new EnoIndex();
        enoIndex.put(SUBSEQUENCE_ID, subsequence);
        enoQuestionnaire.setIndex(enoIndex);

        //
        DDIResolveSequencesStructure ddiResolveSequencesStructure = new DDIResolveSequencesStructure();
        ddiResolveSequencesStructure.apply(enoQuestionnaire);

        //
        assertEquals(1, sequence.getSequenceStructure().size());
        assertEquals(SUBSEQUENCE_ID, sequence.getSequenceStructure().get(0).getId());
        assertEquals(StructureItemType.SUBSEQUENCE, sequence.getSequenceStructure().get(0).getType());
        assertEquals(1, subsequence.getSequenceStructure().size());
        assertEquals(QUESTION_ID, subsequence.getSequenceStructure().get(0).getId());
        assertEquals(StructureItemType.QUESTION, subsequence.getSequenceStructure().get(0).getType());
    }

    @Test
    @DisplayName("1 sequence, 1 subsequence, loop on subsequence")
    void test03() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        Sequence sequence = new Sequence();
        sequence.setId(SEQUENCE_ID);
        sequence.getSequenceItems().add(
                ItemReference.builder().id(LOOP_ID).type(ItemType.LOOP).build());
        enoQuestionnaire.getSequences().add(sequence);
        //
        Loop loop = new StandaloneLoop();
        loop.setId(LOOP_ID);
        loop.getLoopItems().add(
                ItemReference.builder().id(SUBSEQUENCE_ID).type(ItemType.SUBSEQUENCE).build());
        //
        Subsequence subsequence = new Subsequence();
        subsequence.setId(SUBSEQUENCE_ID);
        subsequence.getSequenceItems().add(
                ItemReference.builder().id(QUESTION_ID).type(ItemType.QUESTION).build());
        enoQuestionnaire.getSubsequences().add(subsequence);
        //
        EnoIndex enoIndex = new EnoIndex();
        enoIndex.put(SUBSEQUENCE_ID, subsequence);
        enoIndex.put(LOOP_ID, loop);
        enoQuestionnaire.setIndex(enoIndex);

        //
        DDIResolveSequencesStructure ddiResolveSequencesStructure = new DDIResolveSequencesStructure();
        ddiResolveSequencesStructure.apply(enoQuestionnaire);

        //
        assertEquals(1, sequence.getSequenceStructure().size());
        assertEquals(SUBSEQUENCE_ID, sequence.getSequenceStructure().get(0).getId());
        assertEquals(StructureItemType.SUBSEQUENCE, sequence.getSequenceStructure().get(0).getType());
        assertEquals(1, subsequence.getSequenceStructure().size());
        assertEquals(QUESTION_ID, subsequence.getSequenceStructure().get(0).getId());
        assertEquals(StructureItemType.QUESTION, subsequence.getSequenceStructure().get(0).getType());
    }

    @Test
    @DisplayName("1 sequence, 1 subsequence, filter on question")
    void test04() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        Sequence sequence = new Sequence();
        sequence.setId(SEQUENCE_ID);
        sequence.getSequenceItems().add(
                ItemReference.builder().id(SUBSEQUENCE_ID).type(ItemType.SUBSEQUENCE).build());
        enoQuestionnaire.getSequences().add(sequence);
        //
        Subsequence subsequence = new Subsequence();
        subsequence.setId(SUBSEQUENCE_ID);
        subsequence.getSequenceItems().add(
                ItemReference.builder().id(FILTER_ID).type(ItemType.FILTER).build());
        enoQuestionnaire.getSubsequences().add(subsequence);
        //
        Filter filter = new Filter();
        filter.getFilterItems().add(
                ItemReference.builder().id(QUESTION_ID).type(ItemType.QUESTION).build());
        //
        EnoIndex enoIndex = new EnoIndex();
        enoIndex.put(SUBSEQUENCE_ID, subsequence);
        enoIndex.put(FILTER_ID, filter);
        enoQuestionnaire.setIndex(enoIndex);

        //
        DDIResolveSequencesStructure ddiResolveSequencesStructure = new DDIResolveSequencesStructure();
        ddiResolveSequencesStructure.apply(enoQuestionnaire);

        //
        assertEquals(1, sequence.getSequenceStructure().size());
        assertEquals(SUBSEQUENCE_ID, sequence.getSequenceStructure().get(0).getId());
        assertEquals(StructureItemType.SUBSEQUENCE, sequence.getSequenceStructure().get(0).getType());
        assertEquals(1, subsequence.getSequenceStructure().size());
        assertEquals(QUESTION_ID, subsequence.getSequenceStructure().get(0).getId());
        assertEquals(StructureItemType.QUESTION, subsequence.getSequenceStructure().get(0).getType());
    }

    @Nested
    @DisplayName("Larger tests with 'l20g2ba7'")
    class IntegrationTests {

        static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            // Given
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(DDIParser.parse(
                    IntegrationTests.class.getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-l20g2ba7.xml")),
                    enoQuestionnaire);
            // When
            DDIResolveSequencesStructure ddiResolveSequencesStructure = new DDIResolveSequencesStructure();
            ddiResolveSequencesStructure.apply(enoQuestionnaire);
            // Then
            // -> split in several tests
        }

        @Test
        @DisplayName("Sequences / subsequences structure")
        void integrationTest_subsequences() {
            //
            assertEquals(3, enoQuestionnaire.getSequences().get(0).getSequenceStructure().size());
            enoQuestionnaire.getSequences().get(0).getSequenceStructure().forEach(sequenceItem ->
                    assertEquals(StructureItemType.SUBSEQUENCE, sequenceItem.getType()));
            //
            assertEquals(2, enoQuestionnaire.getSequences().get(1).getSequenceStructure().size());
            enoQuestionnaire.getSequences().get(1).getSequenceStructure().forEach(sequenceItem ->
                    assertEquals(StructureItemType.SUBSEQUENCE, sequenceItem.getType()));
            //
            assertEquals(6, enoQuestionnaire.getSequences().get(2).getSequenceStructure().size());
            enoQuestionnaire.getSequences().get(2).getSequenceStructure().forEach(sequenceItem ->
                    assertEquals(StructureItemType.QUESTION, sequenceItem.getType()));
            //
            assertEquals(5, enoQuestionnaire.getSequences().get(3).getSequenceStructure().size());
            assertEquals(StructureItemType.QUESTION,
                    enoQuestionnaire.getSequences().get(3).getSequenceStructure().get(0).getType());
            assertEquals(StructureItemType.QUESTION,
                    enoQuestionnaire.getSequences().get(3).getSequenceStructure().get(1).getType());
            assertEquals(StructureItemType.SUBSEQUENCE,
                    enoQuestionnaire.getSequences().get(3).getSequenceStructure().get(2).getType());
            assertEquals(StructureItemType.SUBSEQUENCE,
                    enoQuestionnaire.getSequences().get(3).getSequenceStructure().get(3).getType());
            assertEquals(StructureItemType.SUBSEQUENCE,
                    enoQuestionnaire.getSequences().get(3).getSequenceStructure().get(4).getType());
            //
            assertEquals(3, enoQuestionnaire.getSequences().get(4).getSequenceStructure().size());
            enoQuestionnaire.getSequences().get(4).getSequenceStructure().forEach(sequenceItem ->
                    assertEquals(StructureItemType.SUBSEQUENCE, sequenceItem.getType()));
            //
            assertEquals(0, enoQuestionnaire.getSequences().get(5).getSequenceStructure().size());
        }

        @Test
        @DisplayName("Questions in subsequence")
        void integrationTest_questions() {
            Subsequence subsequence11 = (Subsequence) enoQuestionnaire.get(
                    enoQuestionnaire.getSequences().get(0).getSequenceStructure().get(0).getId());
            assertEquals(2, subsequence11.getSequenceStructure().size());
            assertEquals(StructureItemType.QUESTION, subsequence11.getSequenceStructure().get(0).getType());
            assertEquals(StructureItemType.QUESTION, subsequence11.getSequenceStructure().get(1).getType());
        }

        @Test
        @DisplayName("Loop references should be resolved")
        void integrationTest_loops() {
            // Subsequence targeted by a loop
            Subsequence subsequence52 = (Subsequence) enoQuestionnaire.get(
                    enoQuestionnaire.getSequences().get(4).getSequenceStructure().get(1).getId());
            assertEquals(2, subsequence52.getSequenceStructure().size());
            assertEquals(StructureItemType.QUESTION, subsequence52.getSequenceStructure().get(0).getType());
            assertEquals(StructureItemType.QUESTION, subsequence52.getSequenceStructure().get(1).getType());
            // Subsequence targeted by a linked loop;
            Subsequence subsequence53 = (Subsequence) enoQuestionnaire.get(
                    enoQuestionnaire.getSequences().get(4).getSequenceStructure().get(2).getId());
            assertEquals(1, subsequence53.getSequenceStructure().size());
            assertEquals(StructureItemType.QUESTION, subsequence53.getSequenceStructure().get(0).getType());
        }

        @Test
        @DisplayName("Filter references should be resolved")
        void integrationTest_filters() {
            // A question in this subsequence is targeted by a filter
            Subsequence subsequence21 = (Subsequence) enoQuestionnaire.get(
                    enoQuestionnaire.getSequences().get(1).getSequenceStructure().get(0).getId());
            assertEquals(7, subsequence21.getSequenceStructure().size());
            subsequence21.getSequenceStructure().forEach(sequenceItem ->
                    assertEquals(StructureItemType.QUESTION, sequenceItem.getType()));
            // Subsequence targeted by a filter
            Subsequence subsequence42 = (Subsequence) enoQuestionnaire.get(
                    enoQuestionnaire.getSequences().get(3).getSequenceStructure().get(3).getId());
            assertEquals(1, subsequence42.getSequenceStructure().size());
            assertEquals(StructureItemType.QUESTION, subsequence42.getSequenceStructure().get(0).getType());
            // Subsequence targeted by nested filters
            Subsequence subsequence43 = (Subsequence) enoQuestionnaire.get(
                    enoQuestionnaire.getSequences().get(3).getSequenceStructure().get(4).getId());
            assertEquals(1, subsequence43.getSequenceStructure().size());
            assertEquals(StructureItemType.QUESTION, subsequence43.getSequenceStructure().get(0).getType());
        }

    }

}
