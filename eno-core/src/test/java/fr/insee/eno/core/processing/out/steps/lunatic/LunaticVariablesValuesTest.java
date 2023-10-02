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
        assertEquals(VariableTypeEnum.COLLECTED, lunaticQuestionnaire.getVariables().get(0).getVariableType());
        assertEquals("FOO_VAR", lunaticQuestionnaire.getVariables().get(0).getName());
    }
}
