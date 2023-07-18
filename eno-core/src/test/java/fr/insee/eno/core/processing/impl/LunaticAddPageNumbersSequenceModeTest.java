package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.processing.impl.lunatic.pagination.LunaticAddPageNumbersSequenceMode;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LunaticAddPageNumbersSequenceModeTest {

    private Questionnaire questionnaire;
    private Sequence s1, s4, s71, s8, s9;
    private InputNumber n2, n3, n72, n73, n5;
    private Textarea t10;
    private Loop l6, l7;
    private Subsequence ss6;
    private Input i6;
    private CheckboxOne co71, co73;
    private PairwiseLinks p73;
    private List<ComponentType> components;

    @BeforeEach
    void init() {
        LunaticAddPageNumbersSequenceMode processing = new LunaticAddPageNumbersSequenceMode();

        questionnaire = new Questionnaire();

        components = new ArrayList<>();
        List<ComponentType> l6Components = new ArrayList<>();
        List<ComponentType> l7Components = new ArrayList<>();
        List<ComponentType> p73Components = new ArrayList<>();

        s1 = LunaticAddPageNumbersUtils.buildSequence("jfaz9kv9");
        components.add(s1);

        n2 = LunaticAddPageNumbersUtils.buildNumber("jfazk91m", "Q1", s1, null);
        components.add(n2);

        n3 = LunaticAddPageNumbersUtils.buildNumber("lhpz37kh", "Q2", s1, null);
        components.add(n3);

        s4 = LunaticAddPageNumbersUtils.buildSequence("li1w5tqk");
        components.add(s4);

        n5 = LunaticAddPageNumbersUtils.buildNumber("li1w3tmf", "NB", s4, null);
        components.add(n5);

        l6 = LunaticAddPageNumbersUtils.buildEmptyLoop("li1wjxs2", s4);
        // to consider this loop as main loop
        l6.setLines(new LinesLoop());

        // build loop 6 components
        ss6 = LunaticAddPageNumbersUtils.buildSubsequence("li1wbv47", s4);
        //set a declaration for this subsequence
        ss6.getDeclarations().add(new DeclarationType());
        l6Components.add(ss6);

        i6 = LunaticAddPageNumbersUtils.buildInput("li1wptdt", "PRENOM", s4, ss6);
        l6Components.add(i6);

        l6.getComponents().addAll(l6Components);
        components.add(l6);

        l7 = LunaticAddPageNumbersUtils.buildEmptyLoop("li1wsotd", s4);

        // to make this loop considered as linked
        s71 = LunaticAddPageNumbersUtils.buildSequence("li1wfnbk");
        l7Components.add(s71);

        co71 = LunaticAddPageNumbersUtils.buildCheckboxOne("lhpyz9b0", "Q5", s71, null);
        l7Components.add(co71);

        n72 = LunaticAddPageNumbersUtils.buildNumber("lhpzan4t", "Q6", s71, null);
        l7Components.add(n72);

        p73 = LunaticAddPageNumbersUtils.buildEmptyPairWiseLinks("pairwise-links", s71, null);
        co73 = LunaticAddPageNumbersUtils.buildCheckboxOne("lhpyz9b73", "QQCO", s71, null);
        n73 = LunaticAddPageNumbersUtils.buildNumber("lhpzan73", "QQN", s71, null);

        p73Components.add(co73);
        p73Components.add(n73);
        p73.getComponents().addAll(p73Components);
        l7Components.add(p73);

        l7.getComponents().addAll(l7Components);
        components.add(l7);

        s8 = LunaticAddPageNumbersUtils.buildSequence("li1wjpqw");
        components.add(s8);

        s9 = LunaticAddPageNumbersUtils.buildSequence("COMMENT-SEQ");
        components.add(s9);

        t10 = LunaticAddPageNumbersUtils.buildTextarea("COMMENT-QUESTION", "COMMENT_QE", s9);
        components.add(t10);

        questionnaire.getComponents().addAll(components);

        //JSONSerializer jsonSerializer = new JSONSerializer();
        //System.out.println(jsonSerializer.serialize2(questionnaire));
        processing.apply(questionnaire);
        //System.out.println(jsonSerializer.serialize2(questionnaire));
    }

    @Test
    void shouldQuestionnaireHavePaginationPropertySet() {
        assertEquals("sequence", questionnaire.getPagination());
    }

    @Test
    void shouldLoopWithFirstElementSequenceIsConsideredAsPaginatedLoop() {
        assertTrue(l7.getPaginatedLoop());
        assertEquals(ComponentTypeEnum.SEQUENCE, l7.getComponents().get(0).getComponentType());
    }

    @Test
    void shouldPaginatedLoopIncrementPage() {
        assertTrue(l7.getPaginatedLoop());
        assertEquals("2", l6.getPage());
        assertEquals("3", l7.getPage());
    }

    @Test
    void shouldLoopWithNonFirstElementSequenceIsConsideredAsNonPaginatedLoop() {
        assertFalse(l6.getPaginatedLoop());
        assertNotEquals(ComponentTypeEnum.SEQUENCE, l6.getComponents().get(0).getComponentType());
    }

    @Test
    void shouldComponentsInNotPaginatedLoopToHaveSamePage() {
        assertFalse(l6.getPaginatedLoop());
        assertEquals("2", l6.getPage());
        assertEquals("2", ss6.getPage());
        assertEquals("2", i6.getPage());
    }

    @Test
    void shouldComponentsInPaginatedLoopToHaveSamePageButDifferentFromLoop() {
        // l7 is paginated loop so we'll increment subcomponents
        assertTrue(l7.getPaginatedLoop());
        assertEquals("3", l7.getPage());
        assertEquals("3.1", s71.getPage());
        assertEquals("3.1", co71.getPage());
        assertEquals("3.1", n72.getPage());
        assertEquals("3.1", p73.getPage());
    }

    @Test
    void shouldComponentsInPairwiseLinksToHaveSamePage() {
        assertEquals("3.1", p73.getPage());
        // components in pairwise links have same page as the paiwise component
        assertEquals("3.1", n73.getPage());
        assertEquals("3.1", co73.getPage());
    }

    @Test
    void shouldNotSetMaxPageOnNotPaginatedLoop() {
        assertFalse(l6.getPaginatedLoop());
        assertNull(l6.getMaxPage());
    }

    @Test
    void shouldSetCorrectMaxPageOnPaginatedLoop() {
        assertTrue(l7.getPaginatedLoop());
        assertEquals("1", l7.getMaxPage());
    }

    @Test
    void shouldSetCorrectMaxPageOnQuestionnaire() {
        assertEquals("5", questionnaire.getMaxPage());
    }

    @Test
    void shouldIncrementComponentsPageInNormalFlow() {
        assertEquals("1", s1.getPage());
        assertEquals("1", n2.getPage());
        assertEquals("1", n3.getPage());
        assertEquals("2", s4.getPage());
        assertEquals("2", n5.getPage());
        assertEquals("2", l6.getPage());
        assertEquals("3", l7.getPage());
        assertEquals("4", s8.getPage());
        assertEquals("5", s9.getPage());
        assertEquals("5", t10.getPage());
    }
}
