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
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
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
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class IntegrationTest1 {

        private Map<String, CalculatedVariableType> filterResultVariables;

        @BeforeAll
        void mapQuestionnaire() throws Exception {
            filterResultVariables = new HashMap<>();

            loadCalculatedVariables("integration/ddi/ddi-variables.xml", true);
            loadCalculatedVariables("integration/pogues/pogues-variables.json", false);
        }

        private void loadCalculatedVariables(String resourcePath, boolean isDDI) throws Exception {
            InputStream resourceStream = CalculatedVariableTest.class.getClassLoader().getResourceAsStream(resourcePath);
            if (resourceStream == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }

            Questionnaire lunaticQuestionnaire;
            if (isDDI) {
                lunaticQuestionnaire = new DDIToLunatic().transform(resourceStream,
                        EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
            } else {
                lunaticQuestionnaire = new PoguesToLunatic().transform(resourceStream,
                        EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
            }

            lunaticQuestionnaire.getVariables().stream()
                    .filter(CalculatedVariableType.class::isInstance)
                    .map(CalculatedVariableType.class::cast)
                    .filter(variableType -> !variableType.getName().startsWith("FILTER_RESULT_"))
                    .forEach(variableType -> filterResultVariables.put(variableType.getName(), variableType));
        }

        @Test
        void variablesCount() {
            assertEquals(9, filterResultVariables.size());
        }

        @Test
        void variableNames() {
            assertEquals(
                    Set.of("CALCULATED1", "CALCULATED2", "CALCULATED3", "CALCULATED4",
                            "CALCULATED5", "CALCULATED6", "CALCULATED7", "CALCULATED8", "CALCULATED9"),
                    filterResultVariables.keySet());
        }

        @Test
        void calculatedExpressions() {
            assertEquals("cast(NUMBER1, number) * 10",
                    filterResultVariables.get("CALCULATED1").getExpression().getValue());
            // ...
        }

        @Test
        void calculatedExpressionType() {
            filterResultVariables.values().forEach(variableType ->
                    assertEquals(LabelTypeEnum.VTL, variableType.getExpression().getType()));
        }

        @Test
        void bindingDependencies_collectedOnly() {
            assertThat(filterResultVariables.get("CALCULATED1").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("NUMBER1"));
            assertThat(filterResultVariables.get("CALCULATED2").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("NUMBER1", "NUMBER2"));
        }

        @Test
        void bindingDependencies_withCalculated() {
            assertThat(filterResultVariables.get("CALCULATED3").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("CALCULATED1", "NUMBER1"));
            assertThat(filterResultVariables.get("CALCULATED4").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("CALCULATED2", "NUMBER1", "NUMBER2"));
        }

        @Test
        void bindingDependencies_intermediateCalculatedReference() {
            assertThat(filterResultVariables.get("CALCULATED5").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("CALCULATED4", "NUMBER1", "NUMBER2"));
        }

        @Test
        void bindingDependencies_external() {
            assertThat(filterResultVariables.get("CALCULATED6").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("EXTERNAL_TEXT"));
        }

        @Test
        void bindingDependencies_externalAndCollected() {
            assertThat(filterResultVariables.get("CALCULATED7").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("EXTERNAL_NUMBER", "NUMBER1"));
        }

        @Test
        void bindingDependencies_externalAndCollectedAndCalculated() {
            assertThat(filterResultVariables.get("CALCULATED8").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("EXTERNAL_NUMBER", "NUMBER1", "CALCULATED7"));
        }

        @Test
        void bindingDependencies_finalBoss() {
            assertThat(filterResultVariables.get("CALCULATED9").getBindingDependencies())
                    .containsExactlyInAnyOrderElementsOf(List.of("EXTERNAL_NUMBER", "NUMBER1", "NUMBER2",
                            "CALCULATED4", "CALCULATED7"));
        }

    }

    @Nested
    class IntegrationTest2 {

        @Test
        void oneCalculated_testAllProperties() throws Exception {
            testAllProperties("integration/ddi/ddi-declarations.xml", true);
            testAllProperties("integration/pogues/pogues-declarations.json", false);
        }

        private void testAllProperties(String resourcePath, boolean isDDI) throws Exception {
            InputStream resourceStream = CalculatedVariableTest.class.getClassLoader().getResourceAsStream(resourcePath);
            assertNotNull(resourceStream, "Resource not found: " + resourcePath);

            Questionnaire lunaticQuestionnaire = isDDI
                    ? new DDIToLunatic().transform(resourceStream,
                    EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC))
                    : new PoguesToLunatic().transform(resourceStream,
                    EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));

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
