package fr.insee.eno.core.processing.impl;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LunaticAddHierarchyTest {

    private Questionnaire questionnaire;
    private final static String SEQUENCE_ID = "sequence-id";
    private final static String SEQUENCE_LABEL = "Sequence label.";
    private final static String SUBSEQUENCE_ID = "subsequence-id";
    private final static String SUBSEQUENCE_LABEL = "Subsequence label.";

    @BeforeEach
    void createQuestionnaire() {
        // Given
        questionnaire = new Questionnaire();
        //
        Sequence sequence = new Sequence();
        sequence.setId(SEQUENCE_ID);
        sequence.setLabel(new LabelType());
        sequence.getLabel().setValue(SEQUENCE_LABEL);
        sequence.setPage("1");
        questionnaire.getComponents().add(sequence);
        //
        Subsequence subsequence = new Subsequence();
        subsequence.setId(SUBSEQUENCE_ID);
        subsequence.setLabel(new LabelType());
        subsequence.getLabel().setValue(SUBSEQUENCE_LABEL);
        subsequence.setPage("2");
        subsequence.getDeclarations().add(new DeclarationType()); // (see pagination rules)
        questionnaire.getComponents().add(subsequence);
        //
        Input question = new Input();
        question.setPage("3");
        questionnaire.getComponents().add(question);
    }

    @Test
    void simpleCase() {
        //
        ComponentType sequence = questionnaire.getComponents().get(0);
        ComponentType subsequence = questionnaire.getComponents().get(1);
        ComponentType question = questionnaire.getComponents().get(2);

        //
        new LunaticAddHierarchy().apply(questionnaire);

        //
        assertNotNull(sequence.getHierarchy());
        assertNotNull(subsequence.getHierarchy());
        assertNotNull(question.getHierarchy());
        //
        assertEquals(SEQUENCE_ID, sequence.getHierarchy().getSequence().getId());
        assertEquals(SEQUENCE_LABEL, sequence.getHierarchy().getSequence().getLabel().getValue());
        assertEquals("1", sequence.getHierarchy().getSequence().getPage());
        assertNull(sequence.getHierarchy().getSubSequence());
        //
        assertEquals(SEQUENCE_ID, subsequence.getHierarchy().getSequence().getId());
        assertEquals(SEQUENCE_LABEL, subsequence.getHierarchy().getSequence().getLabel().getValue());
        assertEquals("1", subsequence.getHierarchy().getSequence().getPage());
        assertEquals(SUBSEQUENCE_ID, subsequence.getHierarchy().getSubSequence().getId());
        assertEquals(SUBSEQUENCE_LABEL, subsequence.getHierarchy().getSubSequence().getLabel().getValue());
        assertEquals("2", subsequence.getHierarchy().getSubSequence().getPage());
        //
        assertEquals(SEQUENCE_ID, question.getHierarchy().getSequence().getId());
        assertEquals(SEQUENCE_LABEL, question.getHierarchy().getSequence().getLabel().getValue());
        assertEquals("1", question.getHierarchy().getSequence().getPage());
        assertEquals(SUBSEQUENCE_ID, question.getHierarchy().getSubSequence().getId());
        assertEquals(SUBSEQUENCE_LABEL, question.getHierarchy().getSubSequence().getLabel().getValue());
        assertEquals("2", question.getHierarchy().getSubSequence().getPage());
    }

    @Test
    void simpleCase_withLoop() {
        // Insert components inside a loop
        Loop loop = new Loop();
        loop.setPage("1");
        ComponentType sequence = questionnaire.getComponents().remove(0);
        ComponentType subsequence = questionnaire.getComponents().remove(0);
        ComponentType question = questionnaire.getComponents().remove(0);
        loop.getComponents().add(sequence);
        loop.getComponents().add(subsequence);
        loop.getComponents().add(question);
        sequence.setPage("1.1");
        subsequence.setPage("1.2");
        question.setPage("1.3");
        questionnaire.getComponents().add(loop);

        //
        new LunaticAddHierarchy().apply(questionnaire);

        //
        assertNotNull(sequence.getHierarchy());
        assertNotNull(subsequence.getHierarchy());
        assertNotNull(question.getHierarchy());
        //
        assertEquals(SEQUENCE_ID, sequence.getHierarchy().getSequence().getId());
        assertEquals(SEQUENCE_LABEL, sequence.getHierarchy().getSequence().getLabel().getValue());
        assertEquals("1.1", sequence.getHierarchy().getSequence().getPage());
        assertNull(sequence.getHierarchy().getSubSequence());
        //
        assertEquals(SEQUENCE_ID, subsequence.getHierarchy().getSequence().getId());
        assertEquals(SEQUENCE_LABEL, subsequence.getHierarchy().getSequence().getLabel().getValue());
        assertEquals("1.1", subsequence.getHierarchy().getSequence().getPage());
        assertEquals(SUBSEQUENCE_ID, subsequence.getHierarchy().getSubSequence().getId());
        assertEquals(SUBSEQUENCE_LABEL, subsequence.getHierarchy().getSubSequence().getLabel().getValue());
        assertEquals("1.2", subsequence.getHierarchy().getSubSequence().getPage());
        //
        assertEquals(SEQUENCE_ID, question.getHierarchy().getSequence().getId());
        assertEquals(SEQUENCE_LABEL, question.getHierarchy().getSequence().getLabel().getValue());
        assertEquals("1.1", question.getHierarchy().getSequence().getPage());
        assertEquals(SUBSEQUENCE_ID, question.getHierarchy().getSubSequence().getId());
        assertEquals(SUBSEQUENCE_LABEL, question.getHierarchy().getSubSequence().getLabel().getValue());
        assertEquals("1.2", question.getHierarchy().getSubSequence().getPage());
    }

    @Test
    void simpleCase_noSubsequence() {
        //
        ComponentType sequence = questionnaire.getComponents().get(0);
        questionnaire.getComponents().remove(1);
        ComponentType question = questionnaire.getComponents().get(1);
        question.setPage("2");

        //
        new LunaticAddHierarchy().apply(questionnaire);

        //
        assertNotNull(sequence.getHierarchy());
        assertNotNull(question.getHierarchy());
        //
        assertEquals(SEQUENCE_ID, sequence.getHierarchy().getSequence().getId());
        assertEquals(SEQUENCE_LABEL, sequence.getHierarchy().getSequence().getLabel().getValue());
        assertEquals("1", sequence.getHierarchy().getSequence().getPage());
        assertNull(sequence.getHierarchy().getSubSequence());
        //
        assertEquals(SEQUENCE_ID, question.getHierarchy().getSequence().getId());
        assertEquals(SEQUENCE_LABEL, question.getHierarchy().getSequence().getLabel().getValue());
        assertEquals("1", question.getHierarchy().getSequence().getPage());
        assertNull(question.getHierarchy().getSubSequence());
    }

}
