package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.business.InvalidSuggesterExpression;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.vtl.VtlSyntaxUtils;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CalculatedVariableType;
import fr.insee.lunatic.model.flat.variable.CollectedVariableType;
import fr.insee.lunatic.model.flat.variable.CollectedVariableValues;
import fr.insee.lunatic.model.flat.variable.VariableType;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In Lunatic, when a respondent chooses an entry in a suggester field, multiple variables can be filled.
 * These variables correspond to the different columns of the nomenclature used in the suggester.
 * In DDI, these variables are represented as calculated variables with a "magic" expression using a VTL
 * left join operator.
 * Format of the magic expression:
 * <code>left_join(RESPONSE_NAME, NOMENCLATURE_NAME using ID_FIELD, OTHER_FIELD)</code>
 */
@Slf4j
public class LunaticSuggesterOptionResponses implements ProcessingStep<Questionnaire> {

    // Feature is not designed in Pogues yet.
    // This processing will probably have to be refactored when proper Pogues modeling is done.

    /** Identifier field that must always be present in nomenclature fields. */
    private static final String NOMENCLATURE_ID_FIELD = "id";

    /**
     * Record to store information contained in the "magic" suggester response expressions.
     * Note: made package-private to be unit tested.
     * @param responseName Main response of the suggester component.
     * @param storeName "name" in suggesters at questionnaire level. "storeName" in components.
     * @param idField Identifier field of the nomenclature.
     * @param fieldName Field name to be associated with the calculated variable that holds the expression.
     */
    record SuggesterResponseExpression(
            String responseName,
            String storeName,
            String idField,
            String fieldName
    ){}

    /**
     * Unpacks the given expression to return its pieces of information.
     * Note: made package-private to be unit-tested.
     * @param expression Magic expression of a suggester option response (that contains a left join).
     * @return A record with the information held by the expression.
     * @throws InvalidSuggesterExpression If the expression does not match the format "left_join(A, B using C, D)".
     */
    static SuggesterResponseExpression unpackSuggesterResponseExpression(String expression)
            throws InvalidSuggesterExpression {
        String content = expression.replace(VtlSyntaxUtils.LEFT_JOIN_OPERATOR, "");
        content = content.replace("(", "");
        content = content.replace(")", "");
        String[] splitContent = content.split(",");
        if (3 != splitContent.length)
            throw new InvalidSuggesterExpression("Invalid usage of the left join operator.");
        String[] splitContent2 = splitContent[1].split(VtlSyntaxUtils.USING_KEYWORD);
        if (2 != splitContent2.length)
            throw new InvalidSuggesterExpression("The 'using' keyword is missing or misplaced.");
        String responseName = splitContent[0].trim();
        String nomenclatureName = splitContent2[0].trim();
        String nomenclatureId = splitContent2[1].trim();
        String fieldName = splitContent[2].trim();
        if (!NOMENCLATURE_ID_FIELD.equals(nomenclatureId))
            log.warn("Nomenclature identifier field " + nomenclatureId + " is not equal to " + NOMENCLATURE_ID_FIELD + ".");
        if (NOMENCLATURE_ID_FIELD.equals(fieldName))
            log.warn("Identifier field used in an option response suggester expression.");
        return new SuggesterResponseExpression(responseName, nomenclatureName, nomenclatureId, fieldName);
    }

    /**
     * Transforms the calculated variable with the magic expression that uses a VTL left join into "optionResponses"
     * of suggester components. Also creates corresponding collected variables, and removes these fake calculated ones.
     * @param lunaticQuestionnaire Lunatic questionnaire.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        Map<String, SuggesterResponseExpression> suggesterResponseExpressions = mapSuggesterResponseExpressions(lunaticQuestionnaire);
        Map<String, Object> suggesterComponents = gatherSuggesterComponents(lunaticQuestionnaire);
        //
        suggesterResponseExpressions.keySet().forEach(optionResponseName -> {
            SuggesterResponseExpression suggesterResponseExpression = suggesterResponseExpressions.get(optionResponseName);
            List<Suggester.OptionResponse> optionResponses = getOptionResponses(suggesterComponents, suggesterResponseExpression.responseName());
            optionResponses.add(new Suggester.OptionResponse(
                    optionResponseName, suggesterResponseExpression.fieldName()));
            convertOptionResponseVariable(lunaticQuestionnaire, optionResponseName);
        });
    }

    private Map<String, Object> gatherSuggesterComponents(Questionnaire lunaticQuestionnaire) {
        Map<String, Object> suggesterComponents = new HashMap<>();
        putSuggesterComponents(suggesterComponents, lunaticQuestionnaire.getComponents());
        return suggesterComponents;
    }
    private void putSuggesterComponents(Map<String, Object> suggesterComponents, List<ComponentType> lunaticComponents) {
        lunaticComponents.forEach(component -> {
            if (component instanceof Suggester suggester) {
                ResponseType suggesterResponse = suggester.getResponse();
                if (suggesterResponse == null)
                    throw new MappingException("Suggester '" + suggester.getId() + "' has no response.");
                suggesterComponents.put(suggesterResponse.getName(), suggester);
            }
            if (component instanceof Loop loop)
                putSuggesterComponents(suggesterComponents, loop.getComponents());
            if (component instanceof Roundabout roundabout)
                putSuggesterComponents(suggesterComponents, roundabout.getComponents());
            if (component instanceof Table table)
                table.getBodyLines().forEach(bodyLine -> putSuggesterComponents(
                        suggesterComponents, bodyLine.getBodyCells(), table.getId()));
            if (component instanceof RosterForLoop rosterForLoop)
                putSuggesterComponents(
                        suggesterComponents, rosterForLoop.getComponents(), rosterForLoop.getId());
        });
    }

    /**
     * Inserts the body cells that have the component type "suggester" in the map.
     * @param suggesterComponents Map of suggester (regular suggester components or body cells).
     * @param bodyCells List of body cells.
     * @param tableId Identifier of the table in which the cell belongs for logging purposes.
     */
    private void putSuggesterComponents(Map<String, Object> suggesterComponents, List<BodyCell> bodyCells, String tableId) {
        bodyCells.forEach(bodyCell -> {
            if (ComponentTypeEnum.SUGGESTER.equals(bodyCell.getComponentType())) {
                ResponseType response = bodyCell.getResponse();
                if (response == null)
                    throw new MappingException("Suggester cell in table '" + tableId + "' has no response.");
                suggesterComponents.put(response.getName(), bodyCell);
            }
        });
    }

    /**
     * Suggester components in table objects are not <code>Suggester</code> objects but <code>BodyCell</code> objects.
     * Then, there is no polymorphism for these.
     * This method returns the option responses of the suggester with the given response name, whether it is a
     * 'regular' suggester component or a body cell.
     * @param suggesterComponents Map of suggester components/body cells indexed by response name.
     * @param responseName String response name.
     */
    private List<Suggester.OptionResponse> getOptionResponses(Map<String, Object> suggesterComponents, String responseName) {
        Object searched = suggesterComponents.get(responseName);
        if (searched instanceof Suggester suggester)
            return suggester.getOptionResponses();
        if (searched instanceof BodyCell suggesterCell && ComponentTypeEnum.SUGGESTER.equals(suggesterCell.getComponentType()))
            return  suggesterCell.getOptionResponses();
        throw new IllegalArgumentException("Component with response '" + responseName + "' is not a suggester.");
    }

    /**
     * Maps the information hold by calculated variables that have the magic expression for suggesters, and returns it
     * in a map designed to make the link between a suggester component, one of its fields and the corresponding
     * option response variable.
     * @param lunaticQuestionnaire Lunatic questionnaire.
     * @return A map of response name -> field name -> variable name.
     */
    private Map<String, SuggesterResponseExpression> mapSuggesterResponseExpressions(Questionnaire lunaticQuestionnaire) {
        Map<String, SuggesterResponseExpression> result = new LinkedHashMap<>();
        lunaticQuestionnaire.getVariables().stream()
                .filter(CalculatedVariableType.class::isInstance)
                .map(CalculatedVariableType.class::cast)
                .filter(calculatedVariable -> {
                    try {
                        String editedExpression = calculatedVariable.getExpression().getValue()
                                .replace("\"", ""); // due to dirty workaround in Pogues
                        if (editedExpression.startsWith(VtlSyntaxUtils.LEFT_JOIN_OPERATOR)) {
                            calculatedVariable.getExpression().setValue(editedExpression);
                            return true;
                        }
                        return false;
                    } catch (NullPointerException e) {
                        throw new MappingException("Calculated variable '" + calculatedVariable.getName() + "' has no expression.");
                    }
                })
                .forEachOrdered(calculatedVariable -> {
                    String expression = calculatedVariable.getExpression().getValue();
                    try {
                        SuggesterResponseExpression suggesterResponseExpression = unpackSuggesterResponseExpression(expression);
                        result.put(calculatedVariable.getName(), suggesterResponseExpression);
                    } catch (InvalidSuggesterExpression e) {
                        log.error("Invalid usage of the left join operator in calculated variable {}.",
                                calculatedVariable.getName());
                        throw e;
                    }
                });
        return result;
    }

    /**
     * Transforms the calculated variables that correspond to the option responses of suggesters into collected
     * variables.
     * @param lunaticQuestionnaire Lunatic questionnaire.
     * @param optionResponseName Name of the option response variable to be transformed from calculated into collected.
     */
    private void convertOptionResponseVariable(Questionnaire lunaticQuestionnaire, String optionResponseName) {
        VariableType fakeVariable = removeVariable(lunaticQuestionnaire, optionResponseName);
        if (fakeVariable == null) {
            log.error("Unable to remove variable {} in lunatic questionnaire {}.",
                    optionResponseName, lunaticQuestionnaire.getId());
            throw new InvalidSuggesterExpression(
                    "Error when converting suggester option response variable " + optionResponseName + ".");
        }
        CollectedVariableType suggesterOptionVariable = new CollectedVariableType();
        suggesterOptionVariable.setName(fakeVariable.getName());
        suggesterOptionVariable.setIterationReference(fakeVariable.getIterationReference());
        suggesterOptionVariable.setDimension(fakeVariable.getDimension());
        assert fakeVariable.getDimension() != null : "Dimension processing must be called first.";
        switch (fakeVariable.getDimension()) {
            case SCALAR -> suggesterOptionVariable.setValues(new CollectedVariableValues.Scalar());
            case ARRAY -> suggesterOptionVariable.setValues(new CollectedVariableValues.Array());
            case DOUBLE_ARRAY -> throw new InvalidSuggesterExpression(
                    "Suggester option variable " + optionResponseName + " has an invalid scope.");
        }
        lunaticQuestionnaire.getVariables().add(suggesterOptionVariable);
    }
    private VariableType removeVariable(Questionnaire lunaticQuestionnaire, String variableName) {
        for (VariableType variable : lunaticQuestionnaire.getVariables()) {
            if (variableName.equals(variable.getName())) {
                lunaticQuestionnaire.getVariables().remove(variable);
                return variable;
            }
        }
        return null;
    }

}
