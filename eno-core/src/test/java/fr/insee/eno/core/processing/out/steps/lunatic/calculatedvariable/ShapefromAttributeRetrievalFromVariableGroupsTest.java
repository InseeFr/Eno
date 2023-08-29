package fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.variable.VariableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShapefromAttributeRetrievalFromVariableGroupsTest {

    ShapefromAttributeRetrievalFromVariableGroups shapefromAttributeRetrieval;

    EnoQuestionnaire enoQuestionnaire;

    VariableGroup variableGroup1, variableGroup2, variableGroup3;

    Variable variable1, variable2, variable3, variable4, variable5;

    @BeforeEach
    void init() {
        shapefromAttributeRetrieval = new ShapefromAttributeRetrievalFromVariableGroups();
        enoQuestionnaire = new EnoQuestionnaire();

        variableGroup1 = new VariableGroup();
        variableGroup1.setType("Loop");
        variable1 = new CalculatedVariable();
        variable1.setName("variable1");
        variable2 = new CollectedVariable();
        variable2.setName("variable2");
        variableGroup1.getVariables().addAll(List.of(variable1, variable2));

        variableGroup2 = new VariableGroup();
        variableGroup2.setType("Questionnaire");
        variable3 = new CollectedVariable();
        variable3.setName("variable3");
        variableGroup2.getVariables().add(variable3);

        variableGroup3 = new VariableGroup();
        variableGroup3.setType("Loop");
        variable4 = new CollectedVariable();
        variable4.setName("variable4");
        variable5 = new CalculatedVariable();
        variable5.setName("variable5");
        variableGroup3.getVariables().addAll(List.of(variable4, variable5));

        enoQuestionnaire.getVariableGroups().addAll(List.of(variableGroup1, variableGroup2, variableGroup3));
    }

    @Test
    void whenVariableFromQuestionnaireVariableGroupReturnEmpty() {
        Optional<Variable> variable = shapefromAttributeRetrieval.getShapeFrom("variable3", enoQuestionnaire);
        assertTrue(variable.isEmpty());
    }

    @Test
    void whenVariableNotExistingReturnEmpty() {
        Optional<Variable> variable = shapefromAttributeRetrieval.getShapeFrom("variable59", enoQuestionnaire);
        assertTrue(variable.isEmpty());
    }

    @Test
    void whenVariableInLoopVariableGroupReturnFirstCollectedVariable() {
        Optional<Variable> variable = shapefromAttributeRetrieval.getShapeFrom("variable5", enoQuestionnaire);
        assertTrue(variable.isPresent());
        assertEquals("variable4", variable.get().getName());

        variable = shapefromAttributeRetrieval.getShapeFrom("variable1", enoQuestionnaire);
        assertTrue(variable.isPresent());
        assertEquals("variable2", variable.get().getName());
    }
}
