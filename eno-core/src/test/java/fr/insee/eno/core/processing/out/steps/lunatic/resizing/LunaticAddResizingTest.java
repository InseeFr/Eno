package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.lunatic.LunaticResizingEntry;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairwiseEntry;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticAddResizingTest {

    private static List<Object> resizingList;

    @BeforeAll
    static void init() {
        // Given
        EnoQuestionnaire enoQuestionnaire = buildEnoQuestionnaire();
        Questionnaire lunaticQuestionnaire = buildLunaticQuestionnaire();
        // When
        new LunaticAddResizing(enoQuestionnaire).apply(lunaticQuestionnaire);
        // Then
        resizingList = lunaticQuestionnaire.getResizing().getAny();
        // -> tests
    }

    @Test
    @Disabled("work on resizing is in progress")
    void resizingObjectForLoops() {
        assertTrue(resizingList.get(0) instanceof LunaticResizingEntry);
    }

    @Test
    @Disabled("work on resizing is in progress")
    void resizingObjectForPairwise() {
        assertTrue(resizingList.get(1) instanceof LunaticResizingPairwiseEntry);
    }

    private static EnoQuestionnaire buildEnoQuestionnaire() {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        EnoIndex enoIndex = new EnoIndex();
        //
        // TODO: eno questionnaire content
        //
        enoQuestionnaire.setIndex(enoIndex);
        return enoQuestionnaire;
    }

    private static Questionnaire buildLunaticQuestionnaire() {
        Questionnaire questionnaire = new Questionnaire();
        List<ComponentType> components = new ArrayList<>();
        List<ComponentType> mainLoopComponents = new ArrayList<>();
        List<ComponentType> linkedLoopComponents = new ArrayList<>();
        List<ComponentType> pairwiseComponents = new ArrayList<>();

        components.add(buildSequence("id-1"));
        components.add(buildNumber("id-2"));
        components.add(buildNumber("id-3"));
        components.add(buildSequence("id-4"));
        components.add(buildNumber("id-5"));

        Loop mainLoop = buildEmptyLoop("id-6");
        mainLoop.setLines(new LinesLoop());
        mainLoop.getLines().setMax(new LabelType());
        mainLoop.getLines().getMax().setValue("NUMBER_5");
        mainLoopComponents.add(buildSubsequence("id-6-1"));
        mainLoopComponents.add(buildInput("id-6-2"));
        mainLoop.getComponents().addAll(mainLoopComponents);
        components.add(mainLoop);

        Loop linkedLoop = buildEmptyLoop("id-7");
        linkedLoop.setIterations(new LabelType());
        linkedLoop.getIterations().setValue("count(NUMBER_6_2)");
        linkedLoopComponents.add(buildSubsequence("id-7-1"));
        linkedLoopComponents.add(buildCheckboxOne("id-7-2"));
        linkedLoopComponents.add(buildNumber("id-7-3"));
        linkedLoop.getComponents().addAll(linkedLoopComponents);
        components.add(linkedLoop);

        components.add(buildSubsequence("id-8"));
        PairwiseLinks pairwiseLinks = buildEmptyPairWiseLinks("id-9");
        pairwiseComponents.add(buildCheckboxOne("id-9-1"));
        pairwiseComponents.add(buildNumber("id-9-2"));
        pairwiseLinks.getComponents().addAll(pairwiseComponents);

        components.add(buildSequence("id-10"));
        components.add(buildTextarea("id-11"));

        questionnaire.getComponents().addAll(components);
        return questionnaire;
    }

    private static CheckboxOne buildCheckboxOne(String id) {
        CheckboxOne checkboxOne = new CheckboxOne();
        checkboxOne.setComponentType(ComponentTypeEnum.CHECKBOX_ONE);
        checkboxOne.setId(id);
        return checkboxOne;
    }
    private static Textarea buildTextarea(String id) {
        Textarea textarea = new Textarea();
        textarea.setComponentType(ComponentTypeEnum.TEXTAREA);
        textarea.setId(id);
        return textarea;
    }
    private static Input buildInput(String id) {
        Input input = new Input();
        input.setComponentType(ComponentTypeEnum.INPUT);
        input.setId(id);
        return input;
    }
    private static Sequence buildSequence(String id) {
        Sequence sequence = new Sequence();
        sequence.setId(id);
        sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        return sequence;
    }
    private static Subsequence buildSubsequence(String id) {
        Subsequence subsequence = new Subsequence();
        subsequence.setId(id);
        subsequence.setComponentType(ComponentTypeEnum.SUBSEQUENCE);
        return subsequence;
    }
    private static InputNumber buildNumber(String id) {
        InputNumber number = new InputNumber();
        number.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        number.setId(id);
        return number;
    }
    private static Loop buildEmptyLoop(String id) {
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        loop.setId(id);
        return loop;
    }
    private static PairwiseLinks buildEmptyPairWiseLinks(String id) {
        PairwiseLinks pairwiseLinks = new PairwiseLinks();
        pairwiseLinks.setId(id);
        pairwiseLinks.setComponentType(ComponentTypeEnum.PAIRWISE_LINKS);
        return pairwiseLinks;
    }

}
