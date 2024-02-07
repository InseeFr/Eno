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
        assertInstanceOf(VariableTypeArray.class, lunaticQuestionnaire.getVariables().getFirst());
        //
        VariableTypeArray loopVariable = (VariableTypeArray) lunaticQuestionnaire.getVariables().getFirst();
        assertEquals(VariableTypeEnum.COLLECTED, loopVariable.getVariableType());
        assertEquals("FOO_VAR", loopVariable.getName());
        assertNotNull(loopVariable.getValues());
        assertNotNull(loopVariable.getValues().getCollected());
    }

    @Test
    void replaceVariablesFromRosterForLoop() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        RosterForLoop rosterForLoop = new RosterForLoop();
        BodyCell dropdownCell = new BodyCell();
        dropdownCell.setComponentType(ComponentTypeEnum.DROPDOWN);
        dropdownCell.setResponse(new ResponseType());
        dropdownCell.getResponse().setName("COLUMN1_VAR");
        rosterForLoop.getComponents().add(dropdownCell);
        lunaticQuestionnaire.getComponents().add(rosterForLoop);
        //
        VariableType variableType = new VariableType();
        variableType.setName("COLUMN1_VAR");
        lunaticQuestionnaire.getVariables().add(variableType);

        //
        new LunaticVariablesValues().apply(lunaticQuestionnaire);

        //
        assertFalse(lunaticQuestionnaire.getVariables().isEmpty());
        assertEquals(1, lunaticQuestionnaire.getVariables().size());
        assertInstanceOf(VariableTypeArray.class, lunaticQuestionnaire.getVariables().getFirst());
        //
        VariableTypeArray loopVariable = (VariableTypeArray) lunaticQuestionnaire.getVariables().getFirst();
        assertEquals(VariableTypeEnum.COLLECTED, loopVariable.getVariableType());
        assertEquals("COLUMN1_VAR", loopVariable.getName());
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
        assertInstanceOf(VariableTypeTwoDimensionsArray.class, lunaticQuestionnaire.getVariables().getFirst());
        //
        VariableTypeTwoDimensionsArray pairwiseVariable = (VariableTypeTwoDimensionsArray)
                lunaticQuestionnaire.getVariables().getFirst();
        assertEquals(VariableTypeEnum.COLLECTED, pairwiseVariable.getVariableType());
        assertEquals("LINKS_VAR", pairwiseVariable.getName());
        assertNotNull(pairwiseVariable.getValues());
        assertNotNull(pairwiseVariable.getValues().getCollected());
    }
    
}
