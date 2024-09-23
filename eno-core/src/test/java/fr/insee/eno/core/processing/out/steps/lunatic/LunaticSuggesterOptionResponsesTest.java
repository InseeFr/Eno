package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.business.InvalidSuggesterExpression;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSuggesterOptionResponses.SuggesterResponseExpression;
import fr.insee.lunatic.model.flat.LabelType;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.ResponseType;
import fr.insee.lunatic.model.flat.Suggester;
import fr.insee.lunatic.model.flat.variable.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static fr.insee.eno.core.processing.out.steps.lunatic.LunaticSuggesterOptionResponses.unpackSuggesterResponseExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class LunaticSuggesterOptionResponsesTest {

    @Test
    void unpackExpressionTest() throws InvalidSuggesterExpression {
        //
        String testExpression = """
                left_join(RESPONSE_NAME, NOMENCLATURE_NAME using id, ATTRIBUTE_NAME)""";
        //
        SuggesterResponseExpression result = unpackSuggesterResponseExpression(testExpression);
        //
        assertEquals("RESPONSE_NAME", result.responseName());
        assertEquals("NOMENCLATURE_NAME", result.storeName());
        assertEquals("id", result.idField());
        assertEquals("ATTRIBUTE_NAME", result.fieldName());
    }

    @Test
    void unitTest() {
        // Given
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        // Some collected variable
        CollectedVariableType fooCollected = new CollectedVariableType();
        fooCollected.setName("FOO_COLLECTED");
        lunaticQuestionnaire.getVariables().add(fooCollected);
        // Some external variable
        lunaticQuestionnaire.getVariables().add(new ExternalVariableType());
        // Some calculated variable
        CalculatedVariableType fooCalculated = new CalculatedVariableType();
        fooCalculated.setName("FOO_CALCULATED");
        fooCalculated.setExpression(new LabelType());
        fooCalculated.getExpression().setValue("<some expression>");
        lunaticQuestionnaire.getVariables().add(fooCalculated);
        // Suggester component
        Suggester suggester = new Suggester();
        suggester.setResponse(new ResponseType());
        suggester.getResponse().setName("CITY_ID");
        lunaticQuestionnaire.getComponents().add(suggester);
        CollectedVariableType suggesterResponseVariable = new CollectedVariableType();
        suggesterResponseVariable.setName("CITY_ID");
        suggesterResponseVariable.setDimension(VariableDimension.SCALAR);
        suggesterResponseVariable.setValues(new CollectedVariableValues.Scalar());
        lunaticQuestionnaire.getVariables().add(suggesterResponseVariable);
        // Suggester option response variables
        String expression1 = """
                left_join(CITY_ID, CITY_CODE_LIST using id, label)""";
        CalculatedVariableType suggesterResponseVariable1 = new CalculatedVariableType();
        suggesterResponseVariable1.setName("CITY_LABEL");
        suggesterResponseVariable1.setExpression(new LabelType());
        suggesterResponseVariable1.getExpression().setValue(expression1);
        suggesterResponseVariable1.setDimension(VariableDimension.SCALAR);
        lunaticQuestionnaire.getVariables().add(suggesterResponseVariable1);
        String expression2 = """
                left_join(CITY_ID, CITY_CODE_LIST using id, code)""";
        CalculatedVariableType suggesterResponseVariable2 = new CalculatedVariableType();
        suggesterResponseVariable2.setName("CITY_CODE");
        suggesterResponseVariable2.setExpression(new LabelType());
        suggesterResponseVariable2.getExpression().setValue(expression2);
        suggesterResponseVariable2.setDimension(VariableDimension.SCALAR);
        lunaticQuestionnaire.getVariables().add(suggesterResponseVariable2);

        // When
        LunaticSuggesterOptionResponses processing = new LunaticSuggesterOptionResponses();
        processing.apply(lunaticQuestionnaire);

        // Then
        assertEquals(2, suggester.getOptionResponses().size());
        assertEquals("CITY_LABEL", suggester.getOptionResponses().get(0).name());
        assertEquals("CITY_CODE", suggester.getOptionResponses().get(1).name());
        assertEquals("label", suggester.getOptionResponses().get(0).attribute());
        assertEquals("code", suggester.getOptionResponses().get(1).attribute());
        //
        assertEquals(1, lunaticQuestionnaire.getVariables().stream()
                .filter(CalculatedVariableType.class::isInstance).count());
        //
        Map<String, CollectedVariableType> collectedVariables = new HashMap<>();
        lunaticQuestionnaire.getVariables().stream()
                .filter(CollectedVariableType.class::isInstance).map(CollectedVariableType.class::cast)
                .forEach(collectedVariable -> collectedVariables.put(collectedVariable.getName(), collectedVariable));
        assertEquals(Set.of("FOO_COLLECTED", "CITY_ID", "CITY_LABEL", "CITY_CODE"), collectedVariables.keySet());
        collectedVariables.values().stream()
                .filter(variable -> !"FOO_COLLECTED".equals(variable.getName()))
                .forEach(collectedVariable -> {
                    assertEquals(VariableDimension.SCALAR, collectedVariable.getDimension());
                    assertInstanceOf(CollectedVariableValues.Scalar.class, collectedVariable.getValues());
        });
    }

}
