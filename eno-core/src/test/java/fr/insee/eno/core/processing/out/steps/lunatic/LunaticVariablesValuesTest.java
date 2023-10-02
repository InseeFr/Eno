package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LunaticVariablesValuesTest {

    @Test
    void replaceVariablesFromLoop() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        Loop loop = new Loop();
        Textarea textarea = new Textarea();
        textarea.setComponentType(ComponentTypeEnum.TEXTAREA);
        textarea.setResponse(new ResponseType());
        textarea.getResponse().setName("FOO_VAR");
        loop.getComponents().add(textarea);
        lunaticQuestionnaire.getComponents().add(loop);
        //
        VariableType variableType = new VariableType();
        variableType.setName("FOO_VAR");
        lunaticQuestionnaire.getVariables().add(variableType);

        //
        new LunaticVariablesValues().apply(lunaticQuestionnaire);

        //
        assertFalse(lunaticQuestionnaire.getVariables().isEmpty());
        assertEquals(1, lunaticQuestionnaire.getVariables().size());
        assertTrue(lunaticQuestionnaire.getVariables().get(0) instanceof VariableTypeArray);
        //
        VariableTypeArray loopVariable = (VariableTypeArray) lunaticQuestionnaire.getVariables().get(0);
        assertEquals(VariableTypeEnum.COLLECTED, loopVariable.getVariableType());
        assertEquals("FOO_VAR", loopVariable.getName());
        assertNotNull(loopVariable.getValues());
        assertNotNull(loopVariable.getValues().getCollected());
    }

    @Test
    void replaceVariablesFromPairwise() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        PairwiseLinks pairwiseLinks = new PairwiseLinks();
        Dropdown dropdown = new Dropdown();
        dropdown.setComponentType(ComponentTypeEnum.DROPDOWN);
        dropdown.setResponse(new ResponseType());
        dropdown.getResponse().setName("LINKS_VAR");
        pairwiseLinks.getComponents().add(dropdown);
        lunaticQuestionnaire.getComponents().add(pairwiseLinks);

        //
        new LunaticVariablesValues().apply(lunaticQuestionnaire);

        //
        assertFalse(lunaticQuestionnaire.getVariables().isEmpty());
        assertEquals(1, lunaticQuestionnaire.getVariables().size());
        assertTrue(lunaticQuestionnaire.getVariables().get(0) instanceof VariableTypeTwoDimensionsArray);
        //
        VariableTypeTwoDimensionsArray pairwiseVariable = (VariableTypeTwoDimensionsArray)
                lunaticQuestionnaire.getVariables().get(0);
        assertEquals(VariableTypeEnum.COLLECTED, pairwiseVariable.getVariableType());
        assertEquals("LINKS_VAR", pairwiseVariable.getName());
        assertNotNull(pairwiseVariable.getValues());
        assertNotNull(pairwiseVariable.getValues().getCollected());
    }
    
}
