package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.exceptions.business.IllegalPoguesElementException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.pogues.model.CalculatedVariableType;
import fr.insee.pogues.model.CollectedVariableType;
import fr.insee.pogues.model.ExpressionType;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CalculatedExpressionTest {

    @Test
    void unitTest_fromCalculatedVariable() {
        // Given
        Questionnaire poguesQuestionnaire = new Questionnaire();
        poguesQuestionnaire.setVariables(new Questionnaire.Variables());
        //
        CollectedVariableType collectedVariableType = new CollectedVariableType();
        collectedVariableType.setId("collected-id");
        collectedVariableType.setName("FOO");
        poguesQuestionnaire.getVariables().getVariable().add(collectedVariableType);
        //
        CalculatedVariableType calculatedVariableType = new CalculatedVariableType();
        calculatedVariableType.setFormula(new ExpressionType());
        calculatedVariableType.getFormula().setValue("$FOO$ + 1");
        poguesQuestionnaire.getVariables().getVariable().add(calculatedVariableType);

        // When
        PoguesMapper poguesMapper = new PoguesMapper();
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        // Then
        Optional<CalculatedVariable> enoCalculatedVariable = enoQuestionnaire.getVariables().stream()
                .filter(CalculatedVariable.class::isInstance).map(CalculatedVariable.class::cast).findFirst();
        assertTrue(enoCalculatedVariable.isPresent());
        assertEquals("FOO + 1", enoCalculatedVariable.get().getExpression().getValue());
        assertEquals(1, enoCalculatedVariable.get().getExpression().getBindingReferences().size());
        assertEquals("FOO", enoCalculatedVariable.get().getExpression().getBindingReferences().getFirst().getVariableName());
    }

    @Test
    void unknownVariableCase() {

        // Given
        Questionnaire poguesQuestionnaire = new Questionnaire();
        poguesQuestionnaire.setVariables(new Questionnaire.Variables());
        // Note: testing from a calculated variable, could also be a control, filter,
        // or any object that has a calculated expression
        CalculatedVariableType calculatedVariable = new CalculatedVariableType();
        String expression = "$FOO$ + 1";
        calculatedVariable.setFormula(new ExpressionType());
        calculatedVariable.getFormula().setValue(expression);
        poguesQuestionnaire.getVariables().getVariable().add(calculatedVariable);

        // When + Then
        PoguesMapper poguesMapper = new PoguesMapper();
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        MappingException mappingException = assertThrows(MappingException.class, () ->
                poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire));
        IllegalPoguesElementException exception = assertInstanceOf(IllegalPoguesElementException.class,
                mappingException.getCause());
        assertTrue(exception.getMessage().startsWith("Name 'FOO' used in expression:"));
        assertTrue(exception.getMessage().contains(expression));
    }

}
