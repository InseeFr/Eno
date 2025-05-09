package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LunaticPaginationWithFilterDescriptionTest {

    // Utility methods for these tests since component type is not set in constructor in Lunatic-Model yet.
    private Sequence createSequence() {
        Sequence sequence = new Sequence();
        sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        return sequence;
    }
    private Subsequence createSubsequence() {
        Subsequence newSubsequence = new Subsequence();
        newSubsequence.setComponentType(ComponentTypeEnum.SUBSEQUENCE);
        return newSubsequence;
    }

    private Questionnaire lunaticQuestionnaire;
    private Subsequence subsequence;
    private Subsequence loopSubsequence;

    @BeforeEach
    void createQuestionnaire() {
        lunaticQuestionnaire = new Questionnaire();
        //
        lunaticQuestionnaire.getComponents().add(new FilterDescription());
        lunaticQuestionnaire.getComponents().add(createSequence());
        lunaticQuestionnaire.getComponents().add(new FilterDescription());
        lunaticQuestionnaire.getComponents().add(new FilterDescription());
        subsequence = createSubsequence();
        lunaticQuestionnaire.getComponents().add(subsequence);
        lunaticQuestionnaire.getComponents().add(new FilterDescription());
        lunaticQuestionnaire.getComponents().add(new FilterDescription());
        lunaticQuestionnaire.getComponents().add(new FilterDescription());
        lunaticQuestionnaire.getComponents().add(new Question());
        //
        Loop loop = new Loop(); loop.setComponentType(ComponentTypeEnum.LOOP);
        loop.setLines(new LinesLoop()); // current rule for non-linked (=> non-paginated) loops
        loop.getLines().setMin(new LabelType());
        loop.getLines().setMax(new LabelType());
        loop.getLines().getMin().setValue("1");
        loop.getLines().getMin().setValue("10");
        loop.getComponents().add(new FilterDescription());
        loop.getComponents().add(createSequence());
        loop.getComponents().add(new FilterDescription());
        loopSubsequence = createSubsequence();
        loop.getComponents().add(loopSubsequence);
        loop.getComponents().add(new FilterDescription());
        loop.getComponents().add(new Question());
        lunaticQuestionnaire.getComponents().add(loop);
    }

    @Test
    void questionMode_paginatedSubsequence() {
        subsequence.getDeclarations().add(new DeclarationType());
        loopSubsequence.getDeclarations().add(new DeclarationType());

        new LunaticPaginationQuestionMode().apply(lunaticQuestionnaire);

        assertEquals("1", lunaticQuestionnaire.getComponents().get(0).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(1).getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(2).getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(3).getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(4).getPage());
        assertEquals("2", ((Subsequence) lunaticQuestionnaire.getComponents().get(4)).getGoToPage());
        assertEquals("3", lunaticQuestionnaire.getComponents().get(5).getPage());
        assertEquals("3", lunaticQuestionnaire.getComponents().get(6).getPage());
        assertEquals("3", lunaticQuestionnaire.getComponents().get(7).getPage());
        assertEquals("3", lunaticQuestionnaire.getComponents().get(8).getPage());
        Loop loop = (Loop) lunaticQuestionnaire.getComponents().get(9);
        assertEquals("4", loop.getPage());
        assertEquals("4", loop.getComponents().get(0).getPage());
        assertEquals("4", loop.getComponents().get(1).getPage());
        assertEquals("4", loop.getComponents().get(2).getPage());
        assertEquals("4", loop.getComponents().get(3).getPage());
        assertEquals("4", loop.getComponents().get(4).getPage());
        assertEquals("4", loop.getComponents().get(5).getPage());
    }

    @Test
    void questionMode_nonPaginatedSubsequence() {

        new LunaticPaginationQuestionMode().apply(lunaticQuestionnaire);

        assertEquals("1", lunaticQuestionnaire.getComponents().get(0).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(1).getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(2).getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(3).getPage());
        assertNull(lunaticQuestionnaire.getComponents().get(4).getPage());
        assertEquals("2", ((Subsequence) lunaticQuestionnaire.getComponents().get(4)).getGoToPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(5).getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(6).getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(7).getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(8).getPage());
        Loop loop = (Loop) lunaticQuestionnaire.getComponents().get(9);
        assertEquals("3", loop.getPage());
        assertEquals("3", loop.getComponents().get(0).getPage());
        assertEquals("3", loop.getComponents().get(1).getPage());
        assertEquals("3", loop.getComponents().get(2).getPage());
        assertEquals("3", loop.getComponents().get(3).getPage());
        assertEquals("3", loop.getComponents().get(4).getPage());
        assertEquals("3", loop.getComponents().get(5).getPage());
    }

    @Test
    void sequenceMode_paginatedSubsequence() {
        subsequence.getDeclarations().add(new DeclarationType());
        loopSubsequence.getDeclarations().add(new DeclarationType());

        new LunaticPaginationSequenceMode().apply(lunaticQuestionnaire);

        assertEquals("1", lunaticQuestionnaire.getComponents().get(0).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(1).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(2).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(3).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(4).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(5).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(6).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(7).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(8).getPage());
        Loop loop = (Loop) lunaticQuestionnaire.getComponents().get(9);
        assertEquals("2", loop.getPage());
        assertEquals("2.1", loop.getComponents().get(0).getPage());
        assertEquals("2.1", loop.getComponents().get(1).getPage());
        assertEquals("2.1", loop.getComponents().get(2).getPage());
        assertEquals("2.1", loop.getComponents().get(3).getPage());
        assertEquals("2.1", loop.getComponents().get(4).getPage());
        assertEquals("2.1", loop.getComponents().get(5).getPage());
    }

    @Test
    void sequenceMode_nonPaginatedSubsequence() {

        new LunaticPaginationSequenceMode().apply(lunaticQuestionnaire);

        assertEquals("1", lunaticQuestionnaire.getComponents().get(0).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(1).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(2).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(3).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(4).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(5).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(6).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(7).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(8).getPage());
        Loop loop = (Loop) lunaticQuestionnaire.getComponents().get(9);
        assertEquals("2", loop.getPage());
        assertEquals("2.1", loop.getComponents().get(0).getPage());
        assertEquals("2.1", loop.getComponents().get(1).getPage());
        assertEquals("2.1", loop.getComponents().get(2).getPage());
        assertEquals("2.1", loop.getComponents().get(3).getPage());
        assertEquals("2.1", loop.getComponents().get(4).getPage());
        assertEquals("2.1", loop.getComponents().get(5).getPage());
    }
}
