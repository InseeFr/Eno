package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable.ShapefromAttributeRetrievalReturnVariableNameInVariable;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LunaticAddShapeToCalculatedVariablesTest {
    LunaticAddShapeToCalculatedVariables processing;
    Questionnaire lunaticQuestionnaire;
    EnoQuestionnaire enoQuestionnaire;

    VariableType var1, var2, var3;

    @BeforeEach
    void init() {
        enoQuestionnaire = new EnoQuestionnaire();
        lunaticQuestionnaire = new Questionnaire();
        var1 = new VariableType();
        var1.setVariableType(VariableTypeEnum.CALCULATED);
        var1.setName("var1");
        var2 = new VariableType();
        var2.setVariableType(VariableTypeEnum.COLLECTED);
        var2.setName("var2");
        var3 = new VariableType();
        var3.setVariableType(VariableTypeEnum.EXTERNAL);
        var3.setName("var3");

        List<IVariableType> variables = lunaticQuestionnaire.getVariables();
        variables.addAll(Set.of(var1, var2, var3));

        processing = new LunaticAddShapeToCalculatedVariables(enoQuestionnaire, new ShapefromAttributeRetrievalReturnVariableNameInVariable());
    }

    @Test
    void whenApplyingShapeOnNonCalculatedVariablesDoNothing() {
        processing.apply(lunaticQuestionnaire);
        assertNull(var2.getShapeFrom());
        assertNull(var3.getShapeFrom());
    }

    @Test
    void whenApplyingShapeOnCalculatedVariablesSetShapeAttribute() {
        processing.apply(lunaticQuestionnaire);
        assertEquals("var1", var1.getShapeFrom());
    }
}
