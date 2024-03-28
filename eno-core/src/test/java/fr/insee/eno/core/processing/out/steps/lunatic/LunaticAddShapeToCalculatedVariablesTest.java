package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable.ShapefromAttributeRetrievalReturnVariableNameInVariable;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.variable.CalculatedVariableType;
import fr.insee.lunatic.model.flat.variable.CollectedVariableType;
import fr.insee.lunatic.model.flat.variable.ExternalVariableType;
import fr.insee.lunatic.model.flat.variable.VariableType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticAddShapeToCalculatedVariablesTest {
    LunaticAddShapeToCalculatedVariables processing;
    Questionnaire lunaticQuestionnaire;
    EnoQuestionnaire enoQuestionnaire;

    CalculatedVariableType var1;
    CollectedVariableType var2;
    ExternalVariableType var3;

    @BeforeEach
    void init() {
        enoQuestionnaire = new EnoQuestionnaire();
        lunaticQuestionnaire = new Questionnaire();
        var1 = new CalculatedVariableType();
        var1.setName("var1");
        var2 = new CollectedVariableType();
        var2.setName("var2");
        var3 = new ExternalVariableType();
        var3.setName("var3");

        List<VariableType> variables = lunaticQuestionnaire.getVariables();
        variables.addAll(Set.of(var1, var2, var3));

        processing = new LunaticAddShapeToCalculatedVariables(enoQuestionnaire, new ShapefromAttributeRetrievalReturnVariableNameInVariable());
    }

    @Test
    void whenApplyingShapeOnCalculatedVariablesSetShapeAttribute() {
        processing.apply(lunaticQuestionnaire);
        assertEquals("var1", var1.getShapeFrom());
    }
}
