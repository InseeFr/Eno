package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CollectedVariableType;
import fr.insee.lunatic.model.flat.variable.VariableType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class LunaticPairwiseResizingLogicTest {

    @Test
    void pairwiseResizingTest() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        ResizingType resizingType = new ResizingType();
        //
        PairwiseLinks lunaticPairwise = new PairwiseLinks();
        lunaticPairwise.setId("pairwise-id");
        lunaticPairwise.setXAxisIterations(new LabelType());
        lunaticPairwise.getXAxisIterations().setValue("count(LOOP_VAR)");
        lunaticPairwise.setYAxisIterations(new LabelType());
        lunaticPairwise.getYAxisIterations().setValue("count(LOOP_VAR)");
        Dropdown dropdown = new Dropdown();
        dropdown.setResponse(new ResponseType());
        dropdown.getResponse().setName("LINKS_VAR");
        lunaticPairwise.getComponents().add(dropdown);
        lunaticQuestionnaire.getComponents().add(lunaticPairwise);
        //
        VariableType loopVariable = new CollectedVariableType();
        loopVariable.setName("LOOP_VAR");
        lunaticQuestionnaire.getVariables().add(loopVariable);

        //
        PairwiseQuestion enoPairwise = new PairwiseQuestion();
        enoPairwise.setId("pairwise-id");
        enoPairwise.setLoopVariableName("LOOP_VAR");

        //
        EnoIndex enoIndex = new EnoIndex();
        enoIndex.put("pairwise-id", enoPairwise);

        // When
        LunaticPairwiseResizingLogic pairwiseResizingLogic = new LunaticPairwiseResizingLogic(
                lunaticQuestionnaire, enoIndex);
        pairwiseResizingLogic.buildPairwiseResizingEntries(lunaticPairwise, resizingType);

        // Test
        assertEquals(1, resizingType.countResizingEntries());
        ResizingPairwiseEntry resizingPairwiseEntry = assertInstanceOf(ResizingPairwiseEntry.class,
                resizingType.getResizingEntry("LOOP_VAR"));
        assertEquals("count(LOOP_VAR)", resizingPairwiseEntry.getSizeForLinksVariables().getXAxisSize());
        assertEquals("count(LOOP_VAR)", resizingPairwiseEntry.getSizeForLinksVariables().getYAxisSize());
        assertEquals(List.of("LINKS_VAR"), resizingPairwiseEntry.getLinksVariables());
    }

}
