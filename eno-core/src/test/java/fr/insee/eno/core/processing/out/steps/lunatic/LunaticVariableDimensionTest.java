package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.PoguesToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.variable.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LunaticVariableDimensionTest {

    private Map<String, VariableType> lunaticVariables;

    @BeforeAll
    void integrationTestFromDDI() throws DDIParsingException {

        Questionnaire lunaticQuestionnaire = new DDIToLunatic().transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-dimensions.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));

        lunaticVariables = new HashMap<>();
        lunaticQuestionnaire.getVariables().forEach(variableType ->
                lunaticVariables.put(variableType.getName(), variableType));
    }

    @Test
    void dimension() {
        //
        assertEquals(VariableDimension.SCALAR, lunaticVariables.get("Q1").getDimension());
        assertEquals(VariableDimension.SCALAR, lunaticVariables.get("CALC1").getDimension());
        assertEquals(VariableDimension.SCALAR, lunaticVariables.get("EXT1").getDimension());
        //
        assertEquals(VariableDimension.ARRAY, lunaticVariables.get("Q21").getDimension());
        assertEquals(VariableDimension.ARRAY, lunaticVariables.get("Q22").getDimension());
        assertEquals(VariableDimension.ARRAY, lunaticVariables.get("CALC2").getDimension());
        assertEquals(VariableDimension.ARRAY, lunaticVariables.get("EXT2").getDimension());
        //
        assertEquals(VariableDimension.ARRAY, lunaticVariables.get("Q311").getDimension());
        assertEquals(VariableDimension.ARRAY, lunaticVariables.get("Q312").getDimension());
        assertEquals(VariableDimension.ARRAY, lunaticVariables.get("Q32").getDimension());
        assertEquals(VariableDimension.ARRAY, lunaticVariables.get("CALC3").getDimension());
        assertEquals(VariableDimension.ARRAY, lunaticVariables.get("EXT3").getDimension());
        //
        assertEquals(VariableDimension.DOUBLE_ARRAY, lunaticVariables.get("Q4").getDimension());
    }

    @Test
    void iterationReference() {
        //
        assertNull(lunaticVariables.get("Q1").getIterationReference());
        assertNull(lunaticVariables.get("CALC1").getIterationReference());
        assertNull(lunaticVariables.get("EXT1").getIterationReference());
        //
        assertEquals("lw4zklv9", lunaticVariables.get("Q21").getIterationReference());
        assertEquals("lw4zklv9", lunaticVariables.get("Q22").getIterationReference());
        assertEquals("lw4zklv9", lunaticVariables.get("CALC2").getIterationReference());
        assertEquals("lw4zklv9", lunaticVariables.get("EXT2").getIterationReference());
        //
        assertEquals("lw4zumyt", lunaticVariables.get("Q311").getIterationReference());
        assertEquals("lw4zumyt", lunaticVariables.get("Q312").getIterationReference());
        assertEquals("lw4zumyt", lunaticVariables.get("Q32").getIterationReference());
        assertEquals("lw4zumyt", lunaticVariables.get("CALC3").getIterationReference());
        assertEquals("lw4zumyt", lunaticVariables.get("EXT3").getIterationReference());
        //
        assertEquals("lw507epv", lunaticVariables.get("Q4").getIterationReference());
    }

    @Test
    void collectedValues() {
        //
        assertInstanceOf(CollectedVariableValues.Scalar.class,
                ((CollectedVariableType) lunaticVariables.get("Q1")).getValues());
        //
        assertInstanceOf(CollectedVariableValues.Array.class,
                ((CollectedVariableType) lunaticVariables.get("Q21")).getValues());
        assertInstanceOf(CollectedVariableValues.Array.class,
                ((CollectedVariableType) lunaticVariables.get("Q22")).getValues());
        //
        assertInstanceOf(CollectedVariableValues.Array.class,
                ((CollectedVariableType) lunaticVariables.get("Q311")).getValues());
        assertInstanceOf(CollectedVariableValues.Array.class,
                ((CollectedVariableType) lunaticVariables.get("Q312")).getValues());
        assertInstanceOf(CollectedVariableValues.Array.class,
                ((CollectedVariableType) lunaticVariables.get("Q32")).getValues());
        //
        assertInstanceOf(CollectedVariableValues.DoubleArray.class,
                ((CollectedVariableType) lunaticVariables.get("Q4")).getValues());
    }

    @Test
    void externalValue() {
        //
        assertInstanceOf(ExternalVariableValue.Scalar.class,
                ((ExternalVariableType) lunaticVariables.get("EXT1")).getValue());
        //
        assertInstanceOf(ExternalVariableValue.Array.class,
                ((ExternalVariableType) lunaticVariables.get("EXT2")).getValue());
        //
        assertInstanceOf(ExternalVariableValue.Array.class,
                ((ExternalVariableType) lunaticVariables.get("EXT3")).getValue());
    }

}
