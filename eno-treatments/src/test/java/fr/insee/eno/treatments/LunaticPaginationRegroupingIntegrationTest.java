package fr.insee.eno.treatments;

import fr.insee.eno.treatments.dto.Regroupement;
import fr.insee.eno.treatments.dto.Regroupements;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LunaticPaginationRegroupingIntegrationTest {

    private Questionnaire questionnaire;
    private Sequence s1, s4, s8, s9;
    private InputNumber n2, n3, n72, n73, n5;
    private Textarea t10;
    private Loop l6, l7;
    private Subsequence ss6, ss71;
    private Input i6;
    private CheckboxOne co71, co73;
    private PairwiseLinks p73;
    private List<ComponentType> components;

    @BeforeEach
    void init() {
        List<Regroupement> regroupementList = new ArrayList<>();
        regroupementList.add(new Regroupement(List.of("Q1", "Q2")));
        regroupementList.add(new Regroupement(List.of("Q5", "Q6")));

        Regroupements regroupements = new Regroupements(regroupementList);
        LunaticPaginationRegrouping processing = new LunaticPaginationRegrouping(regroupements);

        questionnaire = new Questionnaire();

        components = new ArrayList<>();
        List<ComponentType> l6Components = new ArrayList<>();
        List<ComponentType> l7Components = new ArrayList<>();
        List<ComponentType> p73Components = new ArrayList<>();

        s1 = buildSequence("jfaz9kv9");
        components.add(s1);

        n2 = buildNumber("jfazk91m", "Q1");
        components.add(n2);

        n3 = buildNumber("lhpz37kh", "Q2");
        components.add(n3);

        s4 = buildSequence("li1w5tqk");
        components.add(s4);

        n5 = buildNumber("li1w3tmf", "NB");
        components.add(n5);

        l6 = buildEmptyLoop("li1wjxs2");
        // to consider this loop as main loop
        l6.setLines(new LinesLoop());

        // build loop 6 components
        ss6 = buildSubsequence("li1wbv47");
        //set a declaration for this subsequence
        ss6.getDeclarations().add(new DeclarationType());
        l6Components.add(ss6);

        i6 = buildInput("li1wptdt", "PRENOM");
        l6Components.add(i6);

        l6.getComponents().addAll(l6Components);
        components.add(l6);

        l7 = buildEmptyLoop("li1wsotd");
        // to make this loop considered as linked
        l7.setIterations(new LabelType());

        ss71 = buildSubsequence("li1wfnbk");
        l7Components.add(ss71);

        co71 = buildCheckboxOne("lhpyz9b0", "Q5");
        l7Components.add(co71);

        n72 = buildNumber("lhpzan4t", "Q6");
        l7Components.add(n72);

        p73 = buildEmptyPairWiseLinks("pairwise-links");
        co73 = buildCheckboxOne("lhpyz9b73", "QQCO");
        n73 = buildNumber("lhpzan73", "QQN");

        p73Components.add(co73);
        p73Components.add(n73);
        p73.getComponents().addAll(p73Components);
        l7Components.add(p73);

        l7.getComponents().addAll(l7Components);
        components.add(l7);

        s8 = buildSequence("li1wjpqw");
        components.add(s8);

        s9 = buildSequence("COMMENT-SEQ");
        components.add(s9);

        t10 = buildTextarea("COMMENT-QUESTION", "COMMENT_QE");
        components.add(t10);

        questionnaire.getComponents().addAll(components);

        processing.apply(questionnaire);
    }

    @Test
    void shouldComponentsInNotPaginatedLoopToHaveSamePage() {
        assertFalse(l6.getPaginatedLoop());
        assertEquals("5", l6.getPage());
        assertEquals("5", ss6.getPage());
        assertEquals("5", i6.getPage());
    }

    @Test
    void shouldComponentsInPairwiseLinksToHaveSamePage() {
        assertEquals("6.2", p73.getPage());
        // components in pairwise links have same page as the paiwise component
        assertEquals("6.2", n73.getPage());
        assertEquals("6.2", co73.getPage());
    }

    @Test
    void shouldComponentsInPaginatedLoopToHaveDifferentsPage() {
        // l7 is paginated loop so we'll increment subcomponents
        assertTrue(l7.getPaginatedLoop());
        assertEquals("6", l7.getPage());
        assertEquals("6.1", n72.getPage());
        assertEquals("6.2", p73.getPage());
    }

    @Test
    void shouldSubsequenceWithNoDeclarationsTakesSamePageAsNextComponent() {
        assertNull(ss71.getPage());
        assertTrue(ss71.getDeclarations().isEmpty());
        assertEquals("6.1", ss71.getGoToPage());
        assertEquals("6.1", co71.getPage());
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
        assertEquals("9", questionnaire.getMaxPage());
    }

    @Test
    void shouldRegroupedQuestionsHaveSamePage(){
        // Q1 and Q2 are regrouped
        assertEquals("2", n2.getPage());
        assertEquals("2", n3.getPage());
    }

    @Test
    void shouldRegroupedQuestionsInLoopHaveSamePage() {
        assertTrue(l7.getPaginatedLoop());
        assertEquals("6", l7.getPage());
        // Q5 and Q6 components in the l7 loop are regrouped, they take the same page
        assertEquals("6.1", co71.getPage());
        assertEquals("6.1", n72.getPage());
    }

    @Test
    void shouldIncrementComponentsPageInNormalFlow() {
        assertEquals("3", s4.getPage());
        assertEquals("4", n5.getPage());

        assertEquals("7", s8.getPage());
        assertEquals("8", s9.getPage());
        assertEquals("9", t10.getPage());
    }

    private CheckboxOne buildCheckboxOne(String id, String name) {
        CheckboxOne co = new CheckboxOne();
        co.setComponentType(ComponentTypeEnum.CHECKBOX_ONE);
        co.setId(id);
        co.setResponse(buildResponse(name));
        return co;
    }

    private Textarea buildTextarea(String id, String name) {
        Textarea textarea = new Textarea();
        textarea.setComponentType(ComponentTypeEnum.TEXTAREA);
        textarea.setId(id);
        textarea.setResponse(buildResponse(name));
        return textarea;
    }

    private Input buildInput(String id, String name) {
        Input input = new Input();
        input.setComponentType(ComponentTypeEnum.INPUT);
        input.setId(id);
        input.setResponse(buildResponse(name));
        return input;
    }

    private Sequence buildSequence(String id) {
        Sequence sequence = new Sequence();
        sequence.setId(id);
        sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        return sequence;
    }

    private Subsequence buildSubsequence(String id) {
        Subsequence subsequence = new Subsequence();
        subsequence.setId(id);
        subsequence.setComponentType(ComponentTypeEnum.SUBSEQUENCE);
        return subsequence;
    }

    private InputNumber buildNumber(String id, String name) {
        InputNumber number = new InputNumber();
        number.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        number.setId(id);
        number.setResponse(buildResponse(name));
        return number;
    }

    private Loop buildEmptyLoop(String id) {
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        loop.setId(id);
        return loop;
    }

    private ResponseType buildResponse(String name) {
        ResponseType response = new ResponseType();
        response.setName(name);
        return response;
    }

    private PairwiseLinks buildEmptyPairWiseLinks(String id) {
        PairwiseLinks pairwiseLinks = new PairwiseLinks();
        pairwiseLinks.setId(id);
        pairwiseLinks.setComponentType(ComponentTypeEnum.PAIRWISE_LINKS);
        return pairwiseLinks;
    }
}
