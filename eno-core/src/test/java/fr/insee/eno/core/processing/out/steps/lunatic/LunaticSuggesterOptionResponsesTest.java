package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.InvalidSuggesterExpression;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSuggesterOptionResponses.SuggesterResponseExpression;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.insee.eno.core.processing.out.steps.lunatic.LunaticSuggesterOptionResponses.unpackSuggesterResponseExpression;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void integrationTest() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-suggester-options.xml"))
                .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticVariablesDimension(enoQuestionnaire).apply(lunaticQuestionnaire);

        //
        new LunaticSuggesterOptionResponses().apply(lunaticQuestionnaire);

        // Suggester components option responses
        Suggester citySuggester1 = (Suggester) lunaticQuestionnaire.getComponents().get(1);
        Suggester citySuggester2 = (Suggester) lunaticQuestionnaire.getComponents().get(2);
        Suggester nationalitySuggester = (Suggester) lunaticQuestionnaire.getComponents().get(3);
        Loop loop = (Loop) lunaticQuestionnaire.getComponents().get(4);
        Suggester activitySuggester = (Suggester) loop.getComponents().get(1);
        //
        List.of(citySuggester1, citySuggester2, nationalitySuggester, activitySuggester).forEach(suggester -> {
            assertEquals(1, suggester.getOptionResponses().size());
            assertEquals("label", suggester.getOptionResponses().getFirst().attribute());
        });
        assertEquals("CITY_OF_BIRTH_LABEL", citySuggester1.getOptionResponses().getFirst().name());
        assertEquals("CURRENT_CITY_LABEL", citySuggester2.getOptionResponses().getFirst().name());
        assertEquals("NATIONALITY_LABEL", nationalitySuggester.getOptionResponses().getFirst().name());
        assertEquals("ACTIVITY_LABEL", activitySuggester.getOptionResponses().getFirst().name());

        // Corresponding variables
        assertTrue(lunaticQuestionnaire.getVariables().stream().noneMatch(CalculatedVariableType.class::isInstance));
        //
        Map<String, CollectedVariableType> variables = new HashMap<>();
        lunaticQuestionnaire.getVariables().forEach(variable ->
                variables.put(variable.getName(), (CollectedVariableType) variable));
        //
        List.of("CITY_OF_BIRTH_LABEL", "CURRENT_CITY_LABEL", "NATIONALITY_LABEL").forEach(variableName -> {
            assertNull(variables.get(variableName).getIterationReference());
            assertEquals(VariableDimension.SCALAR, variables.get(variableName).getDimension());
            assertInstanceOf(CollectedVariableValues.Scalar.class, variables.get(variableName).getValues());
        });
        assertEquals(loop.getId(), variables.get("ACTIVITY_LABEL").getIterationReference());
        assertEquals(VariableDimension.ARRAY, variables.get("ACTIVITY_LABEL").getDimension());
        assertInstanceOf(CollectedVariableValues.Array.class, variables.get("ACTIVITY_LABEL").getValues());
    }

    @Test
    void integrationTest_table() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-suggester-options-table.xml"))
                .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticVariablesDimension(enoQuestionnaire).apply(lunaticQuestionnaire);

        //
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticSuggesterOptionResponses().apply(lunaticQuestionnaire);

        // Suggester cells in table
        Table table = (Table) lunaticQuestionnaire.getComponents().get(1);
        table.getBodyLines().forEach(bodyLine -> {
            BodyCell suggesterCell = bodyLine.getBodyCells().get(1);
            assertEquals(ComponentTypeEnum.SUGGESTER, suggesterCell.getComponentType());
            assertEquals(1, suggesterCell.getOptionResponses().size());
            assertEquals("label", suggesterCell.getOptionResponses().getFirst().attribute());
        });
        // Suggester cells in dynamic table
        RosterForLoop roster = (RosterForLoop) lunaticQuestionnaire.getComponents().get(2);
        BodyCell suggesterColumn = roster.getComponents().getFirst();
        assertEquals(ComponentTypeEnum.SUGGESTER, suggesterColumn.getComponentType());
        assertEquals(1, suggesterColumn.getOptionResponses().size());
        assertEquals("label", suggesterColumn.getOptionResponses().getFirst().attribute());
        assertEquals("Q_ROSTER1_LABEL", suggesterColumn.getOptionResponses().getFirst().name());
    }

}
