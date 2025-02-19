package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.PoguesToLunatic;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.variable.CalculatedVariableType;
import fr.insee.lunatic.model.flat.variable.VariableTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CalculatedVariableTest {

    private CalculatedVariable enoVariable;
    private CalculatedVariableType lunaticVariable;

    @BeforeEach
    void createObjects() {
        enoVariable = new CalculatedVariable();
        lunaticVariable = new CalculatedVariableType();
    }

    @Test
    void variableType() {
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoVariable, lunaticVariable);
        //
        assertEquals(VariableTypeEnum.CALCULATED, lunaticVariable.getVariableType());
    }

    @Test
    void variableName() {
        //
        enoVariable.setName("FOO");
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoVariable, lunaticVariable);
        //
        assertEquals("FOO", lunaticVariable.getName());
    }

    private void setTestExpression() {
        CalculatedExpression expression = new CalculatedExpression();
        expression.setValue("BAR = 1 or BAZ = 1");
        expression.getBindingReferences().add(new BindingReference("bar-ref", "BAR"));
        expression.getBindingReferences().add(new BindingReference("baz-ref", "BAZ"));
        enoVariable.setExpression(expression);
    }

    @Test
    void expression_valueAndType() {
        //
        setTestExpression();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoVariable, lunaticVariable);
        //
        assertEquals("BAR = 1 or BAZ = 1", lunaticVariable.getExpression().getValue());
        assertEquals(LabelTypeEnum.VTL, lunaticVariable.getExpression().getType());
    }

    @Test
    void bindingDependencies() {
        //
        setTestExpression();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoVariable, lunaticVariable);
        //
        assertEquals(2, lunaticVariable.getBindingDependencies().size());
        assertEquals(Set.of("BAR", "BAZ"), new HashSet<>(lunaticVariable.getBindingDependencies()));
    }

    @Nested
    class IntegrationTest {

        @Test
        void oneCalculated_testAllProperties() throws Exception {
            testAllProperties("integration/ddi/ddi-declarations.xml", true);
            testAllProperties("integration/pogues/pogues-declarations.json", false);
        }

        private void testAllProperties(String resourcePath, boolean isDDI) throws Exception {
            InputStream resourceStream = CalculatedVariableTest.class.getClassLoader().getResourceAsStream(resourcePath);
            assertNotNull(resourceStream, "Resource not found: " + resourcePath);

            Questionnaire lunaticQuestionnaire = isDDI
                    ? DDIToLunatic.fromInputStream(resourceStream).transform(
                            EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC))
                    : PoguesToLunatic.fromInputStream(resourceStream).transform(
                            EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));

            Optional<CalculatedVariableType> lunaticVariable1 = lunaticQuestionnaire.getVariables().stream()
                    .filter(variableType -> "CALCULATED1".equals(variableType.getName()))
                    .map(CalculatedVariableType.class::cast)
                    .findAny();

            assertTrue(lunaticVariable1.isPresent());
            assertEquals(VariableTypeEnum.CALCULATED, lunaticVariable1.get().getVariableType());
            assertEquals("CALCULATED1", lunaticVariable1.get().getName());
            assertEquals("cast(Q3, integer) + 5", lunaticVariable1.get().getExpression().getValue());
            assertEquals(LabelTypeEnum.VTL, lunaticVariable1.get().getExpression().getType());
            assertEquals(1, lunaticVariable1.get().getBindingDependencies().size());
            assertEquals("Q3", lunaticVariable1.get().getBindingDependencies().getFirst());
        }

    }

}
