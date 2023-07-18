package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.processing.impl.LunaticFilterResult;
import fr.insee.lunatic.model.flat.IVariableType;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.VariableType;
import fr.insee.lunatic.model.flat.VariableTypeEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

        private static List<VariableType> calculatedVariables;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                    CalculatedVariableTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-variables.xml"));
            calculatedVariables = lunaticQuestionnaire.getVariables().stream()
                    .map(VariableType.class::cast)
                    .filter(variableType -> VariableTypeEnum.CALCULATED.equals(variableType.getVariableType()))
                    .filter(variableType -> !variableType.getName().startsWith(LunaticFilterResult.FILTER_RESULT_PREFIX))
                    .toList();
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
                    calculatedVariables.stream().map(IVariableType::getName).collect(Collectors.toSet()));
        }

    }

    @Nested
    class IntegrationTest2 {

        @Test
        void oneCalculated_testAllProperties() throws DDIParsingException {
            // Given + When
            Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                    CalculatedVariableTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-declarations.xml"));
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
