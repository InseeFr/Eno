package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CollectedVariableType;
import fr.insee.lunatic.model.flat.variable.CollectedVariableValues;
import fr.insee.lunatic.model.flat.variable.VariableType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LunaticVariablesValuesTest {

    @Test
    void scalarVariable_integrationTest_ValuesShouldBeNotNull() throws DDIParsingException {
        //
        Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-simple.xml"),
                EnoParameters.of(EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
        //
        Optional<VariableType> searchedVariable = lunaticQuestionnaire.getVariables().stream()
                .filter(variable -> "Q1".equals(variable.getName())).findAny();
        assertTrue(searchedVariable.isPresent());
        CollectedVariableType variableType = assertInstanceOf(CollectedVariableType.class, searchedVariable.get());
        assertInstanceOf(CollectedVariableValues.Scalar.class, variableType.getValues());
    }

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
        VariableType variableType = new CollectedVariableType();
        variableType.setName("FOO_VAR");
        lunaticQuestionnaire.getVariables().add(variableType);

        //
        new LunaticVariablesValues().apply(lunaticQuestionnaire);

        //
        assertFalse(lunaticQuestionnaire.getVariables().isEmpty());
        assertEquals(1, lunaticQuestionnaire.getVariables().size());
        CollectedVariableType loopVariable = assertInstanceOf(CollectedVariableType.class, lunaticQuestionnaire.getVariables().getFirst());
        assertEquals("FOO_VAR", loopVariable.getName());
        CollectedVariableValues.Array values = assertInstanceOf(CollectedVariableValues.Array.class, loopVariable.getValues());
        assertNotNull(values.getCollected());
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
        VariableType variableType = new CollectedVariableType();
        variableType.setName("COLUMN1_VAR");
        lunaticQuestionnaire.getVariables().add(variableType);

        //
        new LunaticVariablesValues().apply(lunaticQuestionnaire);

        //
        assertFalse(lunaticQuestionnaire.getVariables().isEmpty());
        assertEquals(1, lunaticQuestionnaire.getVariables().size());
        CollectedVariableType loopVariable = assertInstanceOf(CollectedVariableType.class, lunaticQuestionnaire.getVariables().getFirst());
        assertEquals("COLUMN1_VAR", loopVariable.getName());
        CollectedVariableValues.Array values = assertInstanceOf(CollectedVariableValues.Array.class, loopVariable.getValues());
        assertNotNull(values.getCollected());
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
        VariableType variableType = new CollectedVariableType();
        variableType.setName("LINKS_VAR");
        lunaticQuestionnaire.getVariables().add(variableType);

        //
        new LunaticVariablesValues().apply(lunaticQuestionnaire);

        //
        assertFalse(lunaticQuestionnaire.getVariables().isEmpty());
        assertEquals(1, lunaticQuestionnaire.getVariables().size());
        CollectedVariableType pairwiseVariable = assertInstanceOf(CollectedVariableType.class, lunaticQuestionnaire.getVariables().getFirst());
        assertEquals("LINKS_VAR", pairwiseVariable.getName());
        CollectedVariableValues.DoubleArray values = assertInstanceOf(CollectedVariableValues.DoubleArray.class, pairwiseVariable.getValues());
        assertNotNull(values.getCollected());
    }

}
