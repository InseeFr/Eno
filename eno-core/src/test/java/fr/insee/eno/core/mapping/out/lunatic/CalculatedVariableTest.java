package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticFilterResult;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.VariableType;
import fr.insee.lunatic.model.flat.VariableTypeEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculatedVariableTest {

    private CalculatedVariable enoVariable;
    private VariableType lunaticVariable;

    @BeforeEach
    void createObjects() {
        enoVariable = new CalculatedVariable();
        lunaticVariable = new VariableType();
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
        assertEquals(Constant.LUNATIC_LABEL_VTL, lunaticVariable.getExpression().getType());
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
    class IntegrationTest1 {

        private static Map<String, VariableType> calculatedVariables;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                    CalculatedVariableTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-variables.xml"),
                    EnoParameters.of(EnoParameters.Context.DEFAULT, Format.LUNATIC, EnoParameters.ModeParameter.CAWI));
            //
            calculatedVariables = new HashMap<>();
            lunaticQuestionnaire.getVariables().stream()
                    .map(VariableType.class::cast)
                    .filter(variableType -> VariableTypeEnum.CALCULATED.equals(variableType.getVariableType()))
                    .filter(variableType -> !variableType.getName().startsWith(LunaticFilterResult.FILTER_RESULT_PREFIX))
                    .forEach(variableType -> calculatedVariables.put(variableType.getName(), variableType));
        }

        @Test
        void variablesCount() {
            assertEquals(9, calculatedVariables.size());
        }

        @Test
        void variableNames() {
            assertEquals(
                    Set.of("CALCULATED1", "CALCULATED2", "CALCULATED3", "CALCULATED4",
                            "CALCULATED5", "CALCULATED6", "CALCULATED7", "CALCULATED8", "CALCULATED9"),
                    calculatedVariables.keySet());
        }

        @Test
        void calculatedExpressions() {
            assertEquals("cast(NUMBER1, number) * 10",
                    calculatedVariables.get("CALCULATED1").getExpression().getValue());
            // ...
        }

        @Test
        void calculatedExpressionType() {
            calculatedVariables.values().forEach(variableType ->
                    assertEquals(Constant.LUNATIC_LABEL_VTL, variableType.getExpression().getType()));
        }

        @Test
        void bindingDependencies_collectedOnly() {
            assertThat(calculatedVariables.get("CALCULATED1").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("NUMBER1"));
            assertThat(calculatedVariables.get("CALCULATED2").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("NUMBER1", "NUMBER2"));
        }

        @Test
        void bindingDependencies_withCalculated() {
            assertThat(calculatedVariables.get("CALCULATED3").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("CALCULATED1", "NUMBER1"));
            assertThat(calculatedVariables.get("CALCULATED4").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("CALCULATED2", "NUMBER1", "NUMBER2"));
        }

        @Test
        void bindingDependencies_intermediateCalculatedReference() {
            assertThat(calculatedVariables.get("CALCULATED5").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("CALCULATED4", "NUMBER1", "NUMBER2"));
        }

        @Test
        void bindingDependencies_external() {
            assertThat(calculatedVariables.get("CALCULATED6").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("EXTERNAL_TEXT"));
        }

        @Test
        void bindingDependencies_externalAndCollected() {
            assertThat(calculatedVariables.get("CALCULATED7").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("EXTERNAL_NUMBER", "NUMBER1"));
        }

        @Test
        void bindingDependencies_externalAndCollectedAndCalculated() {
            assertThat(calculatedVariables.get("CALCULATED8").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("EXTERNAL_NUMBER", "NUMBER1", "CALCULATED7"));
        }

        @Test
        void bindingDependencies_finalBoss() {
            assertThat(calculatedVariables.get("CALCULATED9").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("EXTERNAL_NUMBER", "NUMBER1", "NUMBER2",
                            "CALCULATED4", "CALCULATED7"));
        }

    }

    @Nested
    class IntegrationTest2 {

        @Test
        void oneCalculated_testAllProperties() throws DDIParsingException {
            // Given + When
            Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                    CalculatedVariableTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-declarations.xml"),
                    EnoParameters.of(EnoParameters.Context.DEFAULT, Format.LUNATIC, EnoParameters.ModeParameter.CAWI));
            // Then
            Optional<VariableType> lunaticVariable = lunaticQuestionnaire.getVariables().stream()
                    .filter(variableType -> "CALCULATED1".equals(variableType.getName()))
                    .map(VariableType.class::cast)
                    .findAny();
            assertTrue(lunaticVariable.isPresent());
            assertEquals(VariableTypeEnum.CALCULATED, lunaticVariable.get().getVariableType());
            assertEquals("CALCULATED1", lunaticVariable.get().getName());
            assertEquals("cast(Q3, integer) + 5", lunaticVariable.get().getExpression().getValue());
            assertEquals(Constant.LUNATIC_LABEL_VTL, lunaticVariable.get().getExpression().getType());
            assertEquals(1, lunaticVariable.get().getBindingDependencies().size());
            assertEquals("Q3", lunaticVariable.get().getBindingDependencies().get(0));
        }

    }

}
