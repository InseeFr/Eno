package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.lunatic.LunaticResizingLoopVariable;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairWiseVariable;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
    @Disabled("work on resizing is in progress")
    void whenProcessingLoopVariableIsConsideredAsLoopVariable() {
        //
        processing.apply(lunaticQuestionnaire);
        List<Object> resizings = lunaticQuestionnaire.getResizing().getAny();
        //
        assertTrue(resizings.get(0) instanceof LunaticResizingLoopVariable);
    }

    @Test
    @Disabled("work on resizing is in progress")
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
        List<ComponentType> mainLoopComponents = new ArrayList<>();
        List<ComponentType> linkedLoopComponents = new ArrayList<>();
        List<ComponentType> pairwiseComponents = new ArrayList<>();

        Sequence s1 = buildSequence("id-1");
        components.add(s1);
        InputNumber n2 = buildNumber("id-2");
        components.add(n2);
        InputNumber n3 = buildNumber("id-3");
        components.add(n3);
        Sequence s4 = buildSequence("id-4");
        components.add(s4);

        InputNumber n5 = buildNumber("id-5");
        components.add(n5);

        Loop l6 = buildEmptyLoop("id-6");

        // build loop 6 components
        Subsequence ss6 = buildSubsequence("id-6-1");
        mainLoopComponents.add(ss6);

        Input i6 = buildInput("id-6-2");
        mainLoopComponents.add(i6);

        l6.getComponents().addAll(mainLoopComponents);
        components.add(l6);

        Loop l7 = buildEmptyLoop("id-7");

        Subsequence ss71 = buildSubsequence("id-7-1");
        linkedLoopComponents.add(ss71);

        CheckboxOne co71 = buildCheckboxOne("id-7-2");
        linkedLoopComponents.add(co71);

        InputNumber n72 = buildNumber("id-7-3");
        linkedLoopComponents.add(n72);

        PairwiseLinks p73 = buildEmptyPairWiseLinks("pairwise-links");
        CheckboxOne co73 = buildCheckboxOne("lhpyz9b73");
        InputNumber n73 = buildNumber("lhpzan73");

        pairwiseComponents.add(co73);
        pairwiseComponents.add(n73);
        p73.getComponents().addAll(pairwiseComponents);
        linkedLoopComponents.add(p73);

        l7.getComponents().addAll(linkedLoopComponents);
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
