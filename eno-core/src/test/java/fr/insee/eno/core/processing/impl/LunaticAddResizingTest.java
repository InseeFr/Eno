package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.lunatic.LunaticResizingLoopVariable;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairWiseVariable;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticAddResizingTest {

    private Questionnaire lunaticQuestionnaire;
    private SequenceType s1, s4, s8, s9;
    private InputNumber n2, n3, n72, n73, n5;
    private Textarea t10;
    private Loop l6, l7;
    private Subsequence ss6, ss71;
    private Input i6;
    private CheckboxOne co71, co73;
    private PairwiseLinks p73;
    private List<ComponentType> components;
    private EnoQuestionnaire enoQuestionnaire;
    private LunaticAddResizing processing;

    @BeforeEach
    void init() {
        enoQuestionnaire = new EnoQuestionnaire();
        lunaticQuestionnaire = buildLunaticQuestionnaire();
        processing = new LunaticAddResizing(enoQuestionnaire);
    }

    @Test
    void whenProcessingLoopVariableIsConsideredAsLoopVariable() {
        processing.apply(lunaticQuestionnaire);
        List<Object> resizings = lunaticQuestionnaire.getResizing().getAny();

        assertTrue(resizings.get(0) instanceof LunaticResizingLoopVariable);
    }

    @Test
    void whenProcessingLoopVariableIsConsideredAsPairwiseVariable() {
        processing.apply(lunaticQuestionnaire);
        List<Object> resizings = lunaticQuestionnaire.getResizing().getAny();
        assertTrue(resizings.get(1) instanceof LunaticResizingPairWiseVariable);
    }

    @Test
    void whenProcessingLoopVariableIsConsideredAPairwiseVariable() {
        processing.apply(lunaticQuestionnaire);
        List<Object> resizings = lunaticQuestionnaire.getResizing().getAny();
        assertTrue(resizings.get(1) instanceof LunaticResizingPairWiseVariable);
    }

    private Questionnaire buildLunaticQuestionnaire() {
        Questionnaire questionnaire = new Questionnaire();
        components = new ArrayList<>();
        List<ComponentType> l6Components = new ArrayList<>();
        List<ComponentType> l7Components = new ArrayList<>();
        List<ComponentType> p73Components = new ArrayList<>();

        s1 = buildSequenceType("jfaz9kv9");
        components.add(s1);

        n2 = buildNumber("jfazk91m");
        components.add(n2);

        n3 = buildNumber("lhpz37kh");
        components.add(n3);

        s4 = buildSequenceType("li1w5tqk");
        components.add(s4);

        n5 = buildNumber("li1w3tmf");
        components.add(n5);

        l6 = buildEmptyLoop("li1wjxs2");

        // build loop 6 components
        ss6 = buildSubsequence("li1wbv47");
        l6Components.add(ss6);

        i6 = buildInput("li1wptdt");
        l6Components.add(i6);

        l6.getComponents().addAll(l6Components);
        components.add(l6);

        l7 = buildEmptyLoop("li1wsotd");

        ss71 = buildSubsequence("li1wfnbk");
        l7Components.add(ss71);

        co71 = buildCheckboxOne("lhpyz9b0");
        l7Components.add(co71);

        n72 = buildNumber("lhpzan4t");
        l7Components.add(n72);

        p73 = buildEmptyPairWiseLinks("pairwise-links");
        co73 = buildCheckboxOne("lhpyz9b73");
        n73 = buildNumber("lhpzan73");

        p73Components.add(co73);
        p73Components.add(n73);
        p73.getComponents().addAll(p73Components);
        l7Components.add(p73);

        l7.getComponents().addAll(l7Components);
        components.add(l7);

        s8 = buildSequenceType("li1wjpqw");
        components.add(s8);

        s9 = buildSequenceType("COMMENT-SEQ");
        components.add(s9);

        t10 = buildTextarea("COMMENT-QUESTION");
        components.add(t10);

        questionnaire.getComponents().addAll(components);
        return questionnaire;
    }

    private CheckboxOne buildCheckboxOne(String id) {
        CheckboxOne co = new CheckboxOne();
        co.setComponentType(ComponentTypeEnum.CHECKBOX_ONE);
        co.setId(id);
        return co;
    }

    private Textarea buildTextarea(String id) {
        Textarea textarea = new Textarea();
        textarea.setComponentType(ComponentTypeEnum.TEXTAREA);
        textarea.setId(id);
        return textarea;
    }

    private Input buildInput(String id) {
        Input input = new Input();
        input.setComponentType(ComponentTypeEnum.INPUT);
        input.setId(id);
        return input;
    }

    private SequenceType buildSequenceType(String id) {
        SequenceType sequence = new SequenceType();
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

    private InputNumber buildNumber(String id) {
        InputNumber number = new InputNumber();
        number.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        number.setId(id);
        return number;
    }

    private Loop buildEmptyLoop(String id) {
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        loop.setId(id);
        return loop;
    }

    private PairwiseLinks buildEmptyPairWiseLinks(String id) {
        PairwiseLinks pairwiseLinks = new PairwiseLinks();
        pairwiseLinks.setId(id);
        pairwiseLinks.setComponentType(ComponentTypeEnum.PAIRWISE_LINKS);
        return pairwiseLinks;
    }
}
