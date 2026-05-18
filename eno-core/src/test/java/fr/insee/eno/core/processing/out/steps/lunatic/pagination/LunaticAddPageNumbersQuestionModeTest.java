package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LunaticAddPageNumbersQuestionModeTest {

    private Questionnaire questionnaire;
    private Sequence s1, s4, s8, s10;
    private InputNumber n2, n3, n72, n5;
    private Textarea t11;
    private Loop l6, l7;
    private Subsequence ss6, ss71;
    private Input i6;
    private CheckboxOne co71;
    private PairwiseLinks p9;

    @BeforeEach
    void init() {

        questionnaire = new Questionnaire();

        List<ComponentType> components = new ArrayList<>();
        List<ComponentType> l6Components = new ArrayList<>();
        List<ComponentType> l7Components = new ArrayList<>();

        s1 = LunaticAddPageNumbersUtils.buildSequence("jfaz9kv9");
        components.add(s1);

        n2 = LunaticAddPageNumbersUtils.buildNumber("jfazk91m", "Q1");
        components.add(n2);

        n3 = LunaticAddPageNumbersUtils.buildNumber("lhpz37kh", "Q2");
        components.add(n3);

        s4 = LunaticAddPageNumbersUtils.buildSequence("li1w5tqk");
        components.add(s4);

        n5 = LunaticAddPageNumbersUtils.buildNumber("li1w3tmf", "NB");
        components.add(n5);

        l6 = LunaticAddPageNumbersUtils.buildEmptyLoop("li1wjxs2");
        // to consider this loop as main loop
        l6.setLines(new LinesLoop());

        // build loop 6 components
        ss6 = LunaticAddPageNumbersUtils.buildSubsequence("li1wbv47");
        //set a declaration for this subsequence
        ss6.getDeclarations().add(new DeclarationType());
        l6Components.add(ss6);

        i6 = LunaticAddPageNumbersUtils.buildInput("li1wptdt", "PRENOM");
        l6Components.add(i6);

        l6.getComponents().addAll(l6Components);
        components.add(l6);

        l7 = LunaticAddPageNumbersUtils.buildEmptyLoop("li1wsotd");
        // to make this loop considered as linked
        l7.setIterations(new LabelType());
        ss71 = LunaticAddPageNumbersUtils.buildSubsequence("li1wfnbk");
        l7Components.add(ss71);
        co71 = LunaticAddPageNumbersUtils.buildCheckboxOne("lhpyz9b0", "Q5");
        l7Components.add(co71);
        n72 = LunaticAddPageNumbersUtils.buildNumber("lhpzan4t", "Q6");
        l7Components.add(n72);
        l7.getComponents().addAll(l7Components);
        components.add(l7);

        s8 = LunaticAddPageNumbersUtils.buildSequence("li1wjpqw");
        components.add(s8);

        p9 = LunaticAddPageNumbersUtils.buildEmptyPairWiseLinks("pairwise-links");
        p9.getComponents().add(LunaticAddPageNumbersUtils.buildCheckboxOne("lhpyz9b73", "QQCO"));
        p9.getComponents().add(LunaticAddPageNumbersUtils.buildNumber("lhpzan73", "QQN"));
        components.add(p9);

        s10 = LunaticAddPageNumbersUtils.buildSequence("COMMENT-SEQ");
        components.add(s10);

        t11 = LunaticAddPageNumbersUtils.buildTextarea("COMMENT-QUESTION", "COMMENT_QE");
        components.add(t11);

        questionnaire.getComponents().addAll(components);


        LunaticPaginationQuestionMode processing = new LunaticPaginationQuestionMode();

        processing.apply(questionnaire);
    }

    @Test
    void shouldQuestionnaireHavePaginationPropertySet() {
        assertEquals(Pagination.QUESTION, questionnaire.getPagination());
    }

    @Test
    void shouldMainLoopConsideredAsNonPaginatedLoop() {
        assertFalse(l6.getPaginatedLoop());
    }

    @Test
    void shouldLinkedLoopConsideredAsPaginatedLoop() {
        assertTrue(l7.getPaginatedLoop());
    }

    @Test
    void shouldComponentsInNotPaginatedLoopToHaveSamePage() {
        assertFalse(l6.getPaginatedLoop());
        assertEquals("6", l6.getPage());
        assertEquals("6", ss6.getPage());
        assertEquals("6", i6.getPage());
    }

    @Test
    void shouldComponentsInPairwiseLinksToHaveSamePage() {
        assertEquals("9", p9.getPage());
        // components in pairwise links have same page as the pairwise component
        p9.getComponents().forEach(component ->
                assertEquals("9", component.getPage()));
    }

    @Test
    void shouldComponentsInPaginatedLoopToHaveDifferentPage() {
        // l7 is paginated loop so we'll increment subcomponents
        assertTrue(l7.getPaginatedLoop());
        assertEquals("7", l7.getPage());
        assertEquals("7.1", co71.getPage());
        assertEquals("7.2", n72.getPage());
    }

    @Test
    void shouldSubsequenceWithNoDeclarationsAndParentIsPaginatedTakesSamePageAsNextComponent() {
        assertNull(ss71.getPage());
        assertTrue(ss71.getDeclarations().isEmpty());
        assertTrue(l7.getPaginatedLoop());
        assertEquals("7.1", ss71.getGoToPage());
        assertEquals("7.1", co71.getPage());
    }

    @Test
    void shouldSubsequenceWithDeclarationsAndParentIsNotPaginatedHasPageAttributeSet() {
        assertFalse(ss6.getDeclarations().isEmpty());
        assertFalse(l6.getPaginatedLoop());
        assertEquals("6", ss6.getPage());
        assertNull(ss6.getGoToPage());
    }

    @Test
    void shouldSubsequenceWithNoDeclarationsTakesSamePageAsNextComponent() {
        assertNull(ss71.getPage());
        assertTrue(ss71.getDeclarations().isEmpty());
        assertEquals("7.1", ss71.getGoToPage());
        assertEquals("7.1", co71.getPage());
    }

    @Test
    void shouldNotSetMaxPageOnNotPaginatedLoop() {
        assertFalse(l6.getPaginatedLoop());
        assertNull(l6.getMaxPage());
    }

    @Test
    void shouldSetCorrectMaxPageOnPaginatedLoop() {
        assertTrue(l7.getPaginatedLoop());
        assertEquals("2", l7.getMaxPage());
    }

    @Test
    void shouldSetCorrectMaxPageOnQuestionnaire() {
        assertEquals("11", questionnaire.getMaxPage());
    }

    @Test
    void shouldIncrementComponentsPageInNormalFlow() {
        assertEquals("1", s1.getPage());
        assertEquals("2", n2.getPage());
        assertEquals("3", n3.getPage());
        assertEquals("4", s4.getPage());
        assertEquals("5", n5.getPage());
        assertEquals("6", l6.getPage());
        assertEquals("7", l7.getPage());
        assertEquals("8", s8.getPage());
        assertEquals("9", p9.getPage());
        assertEquals("10", s10.getPage());
        assertEquals("11", t11.getPage());
    }
}
