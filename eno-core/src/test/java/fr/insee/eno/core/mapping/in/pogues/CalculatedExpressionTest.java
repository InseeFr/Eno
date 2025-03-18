package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.IllegalPoguesElementException;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.variable.VariableType;
import fr.insee.pogues.model.CalculatedVariableType;
import fr.insee.pogues.model.CollectedVariableType;
import fr.insee.pogues.model.ExpressionType;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static fr.insee.eno.core.model.calculated.CalculatedExpression.removeSurroundingDollarSigns;
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
    @Disabled("Exception temporary replaced by a warning") // may be deleted, cf. below test that has lots of comments
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

    /*
     * Pogues allows the user to reference variables that doesn't exist within the questionnaire.
     * The reason is that with the composition feature, the user might want to define calculated variables
     * in the parent/main questionnaire, that use variables from several child questionnaires ("modules"),
     * but all child questionnaires are not necessarily imported in the parent/main one...
     */
    @Test
    void ghostVariableReferences_integrationTest() throws ParsingException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        // Given
        // Note: using Pogues+DDI since it is the current "main" way
        EnoParameters enoParameters = EnoParameters.of(EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        PoguesDDIToLunatic poguesDDIToLunatic = PoguesDDIToLunatic.fromInputStreams(
                classLoader.getResourceAsStream("integration/pogues/pogues-ghost-variables.json"),
                classLoader.getResourceAsStream("integration/ddi/ddi-ghost-variables.xml"));

        // When
        fr.insee.lunatic.model.flat.Questionnaire lunaticQuestionnaire = poguesDDIToLunatic.transform(enoParameters);

        // Then:
        // The questionnaire has 1 question with collected variable "Q1", and 1 calculated "CALCULATED1"
        // The questionnaire also have VTL expressions that references variable names "GHOST" and "ANOTHER_GHOST"
        // 1. No exception should be thrown (at least for now).
        // 2. The former ones must be present, but not the latter.
        List<String> variableNames = lunaticQuestionnaire.getVariables().stream().map(VariableType::getName).toList();
        assertTrue(variableNames.containsAll(List.of("Q1", "CALCULATED1")));
        assertFalse(variableNames.contains("GHOST"));
        assertFalse(variableNames.contains("ANOTHER_GHOST"));
    }

    @Test // dynamic label-like expression
    void removeSurroundingDollarSignsTest1() {
        String expression = "\"II - \" || $FIRST_NAME$";
        String expressionWithout = removeSurroundingDollarSigns(expression);
        assertEquals("\"II - \" || FIRST_NAME", expressionWithout);
    }

    @Test // calculated expression-like expression
    void removeSurroundingDollarSignsTest2() {
        String expression = "cast(nvl($LAST_NAME$, \"X\"), string) || \" \" || cast(nvl($CALC_VAR$, \"\"), string)";
        String expressionWithout = removeSurroundingDollarSigns(expression);
        assertEquals("cast(nvl(LAST_NAME, \"X\"), string) || \" \" || cast(nvl(CALC_VAR, \"\"), string)",
                expressionWithout);
    }

}
