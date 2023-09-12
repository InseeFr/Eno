package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.lunatic.LunaticResizingLoopVariable;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairWiseVariable;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticAddResizing;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticAddResizingTest {

    private Questionnaire lunaticQuestionnaire;
    private LunaticAddResizing processing;

    @BeforeEach
    void init() {
        lunaticQuestionnaire = buildLunaticQuestionnaire();
        processing = new LunaticAddResizing(createEnoQuestionnaire());
    }

    @Test
    void whenProcessingLoopVariableIsConsideredAsLoopVariable() {
        //
        processing.apply(lunaticQuestionnaire);
        List<Object> resizings = lunaticQuestionnaire.getResizing().getAny();
        //
        assertTrue(resizings.get(0) instanceof LunaticResizingLoopVariable);
    }

    @Test
    void whenProcessingLoopVariableIsConsideredAsPairwiseVariable() {
        //
        processing.apply(lunaticQuestionnaire);
        List<Object> resizings = lunaticQuestionnaire.getResizing().getAny();
        //
        assertTrue(resizings.get(1) instanceof LunaticResizingPairWiseVariable);
    }

    private EnoQuestionnaire createEnoQuestionnaire() {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        EnoIndex enoIndex = new EnoIndex();
        //
        // TODO: eno questionnaire content
        //
        enoQuestionnaire.setIndex(enoIndex);
        return enoQuestionnaire;
    }

    private Questionnaire buildLunaticQuestionnaire() {
        Questionnaire questionnaire = new Questionnaire();
        List<ComponentType> components = new ArrayList<>();
        List<ComponentType> l6Components = new ArrayList<>();
        List<ComponentType> l7Components = new ArrayList<>();
        List<ComponentType> p73Components = new ArrayList<>();

        Sequence s1 = buildSequence("jfaz9kv9");
        components.add(s1);

        InputNumber n2 = buildNumber("jfazk91m");
        components.add(n2);

        InputNumber n3 = buildNumber("lhpz37kh");
        components.add(n3);

        Sequence s4 = buildSequence("li1w5tqk");
        components.add(s4);

        InputNumber n5 = buildNumber("li1w3tmf");
        components.add(n5);

        Loop l6 = buildEmptyLoop("li1wjxs2");

        // build loop 6 components
        Subsequence ss6 = buildSubsequence("li1wbv47");
        l6Components.add(ss6);

        Input i6 = buildInput("li1wptdt");
        l6Components.add(i6);

        l6.getComponents().addAll(l6Components);
        components.add(l6);

        Loop l7 = buildEmptyLoop("li1wsotd");

        Subsequence ss71 = buildSubsequence("li1wfnbk");
        l7Components.add(ss71);

        CheckboxOne co71 = buildCheckboxOne("lhpyz9b0");
        l7Components.add(co71);

        InputNumber n72 = buildNumber("lhpzan4t");
        l7Components.add(n72);

        PairwiseLinks p73 = buildEmptyPairWiseLinks("pairwise-links");
        CheckboxOne co73 = buildCheckboxOne("lhpyz9b73");
        InputNumber n73 = buildNumber("lhpzan73");

        p73Components.add(co73);
        p73Components.add(n73);
        p73.getComponents().addAll(p73Components);
        l7Components.add(p73);

        l7.getComponents().addAll(l7Components);
        components.add(l7);

        Sequence s8 = buildSequence("li1wjpqw");
        components.add(s8);

        Sequence s9 = buildSequence("COMMENT-SEQ");
        components.add(s9);

        Textarea t10 = buildTextarea("COMMENT-QUESTION");
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

        LabelType label = new LabelType();
        label.setValue("COUNT(PRENOM)");
        LinesLoop line = new LinesLoop();
        line.setMax(label);
        loop.setLines(line);
        return loop;
    }

    private PairwiseLinks buildEmptyPairWiseLinks(String id) {
        PairwiseLinks pairwiseLinks = new PairwiseLinks();
        pairwiseLinks.setId(id);
        pairwiseLinks.setComponentType(ComponentTypeEnum.PAIRWISE_LINKS);
        return pairwiseLinks;
    }
}
