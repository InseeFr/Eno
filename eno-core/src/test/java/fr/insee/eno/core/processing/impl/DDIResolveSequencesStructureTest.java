package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.SequenceItem;
import fr.insee.eno.core.model.sequence.SequenceItem.SequenceItemType;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.reference.EnoIndex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DDIResolveSequencesStructureTest {

    private static final String SEQUENCE_ID = "sequence-id";
    private static final String SUBSEQUENCE_ID = "subsequence-id";
    private static final String QUESTION_ID = "question-id";
    private static final String LOOP_ID = "loop-id";
    private static final String FILTER_ID = "loop-id";

    @Test
    @DisplayName("1 sequence, 1 question")
    void test01() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        Sequence sequence = new Sequence();
        sequence.setId(SEQUENCE_ID);
        sequence.getSequenceItems().add(
                SequenceItem.builder().id(QUESTION_ID).type(SequenceItemType.QUESTION).build());
        enoQuestionnaire.getSequences().add(sequence);
        //
        DDIResolveSequencesStructure ddiResolveSequencesStructure = new DDIResolveSequencesStructure();
        ddiResolveSequencesStructure.apply(enoQuestionnaire);
        //
        assertEquals(1, sequence.getSequenceStructure().size());
        assertEquals(QUESTION_ID, sequence.getSequenceStructure().get(0).getId());
        assertEquals(SequenceItemType.QUESTION, sequence.getSequenceStructure().get(0).getType());
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
                SequenceItem.builder().id(SUBSEQUENCE_ID).type(SequenceItemType.SUBSEQUENCE).build());
        enoQuestionnaire.getSequences().add(sequence);
        //
        Subsequence subsequence = new Subsequence();
        subsequence.setId(SUBSEQUENCE_ID);
        subsequence.getSequenceItems().add(
                SequenceItem.builder().id(QUESTION_ID).type(SequenceItemType.QUESTION).build());
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
        assertEquals(SequenceItemType.SUBSEQUENCE, sequence.getSequenceStructure().get(0).getType());
        assertEquals(1, subsequence.getSequenceStructure().size());
        assertEquals(QUESTION_ID, subsequence.getSequenceStructure().get(0).getId());
        assertEquals(SequenceItemType.QUESTION, subsequence.getSequenceStructure().get(0).getType());
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
                SequenceItem.builder().id(LOOP_ID).type(SequenceItemType.LOOP).build());
        enoQuestionnaire.getSequences().add(sequence);
        //
        Loop loop = new StandaloneLoop();
        loop.setId(LOOP_ID);
        loop.setSequenceReference(SUBSEQUENCE_ID);
        //
        Subsequence subsequence = new Subsequence();
        subsequence.setId(SUBSEQUENCE_ID);
        subsequence.getSequenceItems().add(
                SequenceItem.builder().id(QUESTION_ID).type(SequenceItemType.QUESTION).build());
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
        assertEquals(SequenceItemType.SUBSEQUENCE, sequence.getSequenceStructure().get(0).getType());
        assertEquals(1, subsequence.getSequenceStructure().size());
        assertEquals(QUESTION_ID, subsequence.getSequenceStructure().get(0).getId());
        assertEquals(SequenceItemType.QUESTION, subsequence.getSequenceStructure().get(0).getType());
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
                SequenceItem.builder().id(SUBSEQUENCE_ID).type(SequenceItemType.SUBSEQUENCE).build());
        enoQuestionnaire.getSequences().add(sequence);
        //
        Subsequence subsequence = new Subsequence();
        subsequence.setId(SUBSEQUENCE_ID);
        subsequence.getSequenceItems().add(
                SequenceItem.builder().id(FILTER_ID).type(SequenceItemType.FILTER).build());
        enoQuestionnaire.getSubsequences().add(subsequence);
        //
        Filter filter = new Filter();
        filter.getFilterItems().add(
                SequenceItem.builder().id(QUESTION_ID).type(SequenceItemType.QUESTION).build());
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
        assertEquals(SequenceItemType.SUBSEQUENCE, sequence.getSequenceStructure().get(0).getType());
        assertEquals(1, subsequence.getSequenceStructure().size());
        assertEquals(QUESTION_ID, subsequence.getSequenceStructure().get(0).getId());
        assertEquals(SequenceItemType.QUESTION, subsequence.getSequenceStructure().get(0).getType());
    }

}
