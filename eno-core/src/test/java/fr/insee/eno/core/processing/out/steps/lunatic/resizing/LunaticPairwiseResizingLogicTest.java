package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.model.lunatic.LunaticResizingPairwiseEntry;
import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticPairwiseResizingLogicTest {

    @Test
    void pairwiseResizingTest() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        PairwiseLinks lunaticPairwise = new PairwiseLinks();
        lunaticPairwise.setId("pairwise-id");
        lunaticPairwise.setXAxisIterations(new LabelType());
        lunaticPairwise.getXAxisIterations().setValue("count(LOOP_VAR)");
        lunaticPairwise.setYAxisIterations(new LabelType());
        lunaticPairwise.getYAxisIterations().setValue("count(LOOP_VAR)");
        Dropdown dropdown = new Dropdown();
        dropdown.setComponentType(ComponentTypeEnum.DROPDOWN);
        dropdown.setResponse(new ResponseType());
        dropdown.getResponse().setName("LINKS_VAR");
        lunaticPairwise.getComponents().add(dropdown);
        lunaticQuestionnaire.getComponents().add(lunaticPairwise);
        //
        VariableType loopVariable = new VariableType();
        loopVariable.setVariableType(VariableTypeEnum.COLLECTED);
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
        List<LunaticResizingPairwiseEntry> pairwiseResizingEntries = pairwiseResizingLogic
                .buildPairwiseResizingEntries(lunaticPairwise);

        // Test
        assertEquals(1, pairwiseResizingEntries.size());
        assertEquals("LOOP_VAR", pairwiseResizingEntries.get(0).getName());
        assertEquals(List.of("count(LOOP_VAR)", "count(LOOP_VAR)"), pairwiseResizingEntries.get(0).getSizeForLinksVariables());
        assertThat(pairwiseResizingEntries.get(0).getLinksVariables())
                .containsExactlyInAnyOrderElementsOf(List.of("LINKS_VAR"));
    }

}
