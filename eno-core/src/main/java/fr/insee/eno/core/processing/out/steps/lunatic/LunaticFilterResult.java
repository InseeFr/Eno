package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CalculatedVariableType;
import fr.insee.lunatic.model.flat.variable.CollectedVariableType;
import fr.insee.lunatic.model.flat.variable.VariableTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.insee.eno.core.utils.LunaticUtils.getDirectResponseNames;

public class LunaticFilterResult implements ProcessingStep<Questionnaire> {

    private static final String FILTER_RESULT_PREFIX = "FILTER_RESULT_";

    private final EnoIndex enoIndex;

    private Map<String, CollectedVariableType> collectedVariableMap;

    public LunaticFilterResult(EnoQuestionnaire enoQuestionnaire) {
        this.enoIndex = enoQuestionnaire.getIndex();
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        indexVariables(lunaticQuestionnaire);
        //
        lunaticQuestionnaire.getVariables().addAll(
                generateFilterResultVariables(lunaticQuestionnaire.getComponents()));
    }

    /**
     * Indexes the collected variables of the given questionnaire in the local map object.
     * @param lunaticQuestionnaire Lunatic questionnaire.
     */
    private void indexVariables(Questionnaire lunaticQuestionnaire) {
        collectedVariableMap = new HashMap<>();
        lunaticQuestionnaire.getVariables().stream()
                .filter(CollectedVariableType.class::isInstance).map(CollectedVariableType.class::cast)
                .forEach(collectedVariable -> collectedVariableMap.put(collectedVariable.getName(), collectedVariable));
    }

    /**
     * Iterates on components given to generate the "filter result" variables. If a component is nesting other
     * components, the method iterates recursively on the inner components.
     * @param components List of Lunatic components.
     * @return The list of filter result variables for the components.
     */
    private List<CalculatedVariableType> generateFilterResultVariables(List<ComponentType> components) {
        List<CalculatedVariableType> filterResultVariables = new ArrayList<>();
        for(ComponentType component : components) {
            // If the component is a nesting component, recursion on its inner components.
            if (component instanceof ComponentNestingType nestingComponent) {
                filterResultVariables.addAll(generateFilterResultVariables(nestingComponent.getComponents()));
                continue;
            }
            //
            filterResultVariables.addAll(generateFilterResultVariables(component));
        }
        return filterResultVariables;
    }

    /**
     * Iterates on responses contained in the component to generate its "filter result" variables.
     * @param component Lunatic response component.
     * @return List of filter result variables for the component.
     */
    private List<CalculatedVariableType> generateFilterResultVariables(ComponentType component) {
        assert !(component instanceof ComponentNestingType); // Safety check (should never happen by design)
        // If the component has no condition filter, there is no filter variable to generate
        if (component.getConditionFilter() == null)
            return new ArrayList<>();
        //
        return getDirectResponseNames(component).stream()
                .map(responseName -> generateFilterResultVariable(responseName, component))
                .toList();
    }

    /**
     * Generates the "filter result" variable corresponding to the given response. The corresponding component is
     * passed since it is used to find the necessary information.
     * @param responseName Response name.
     * @param component Lunatic component that holds the given response name.
     * @return The filter result variable for the given response name.
     */
    private CalculatedVariableType generateFilterResultVariable(String responseName, ComponentType component) {
        assert component.getConditionFilter() != null ; // Safety check (should never happen by design)
        CalculatedVariableType filterResultVariable = new CalculatedVariableType();
        filterResultVariable.setVariableType(VariableTypeEnum.CALCULATED);
        setName(filterResultVariable, responseName);
        setExpression(filterResultVariable, component.getConditionFilter());
        setBindingDependencies(filterResultVariable, findEnoFilter(component));
        setDimensionProperties(filterResultVariable, findResponseVariable(responseName));
        return filterResultVariable;
    }

    private CollectedVariableType findResponseVariable(String responseName) {
        CollectedVariableType responseVariable = collectedVariableMap.get(responseName);
        if (responseVariable == null)
            throw new MappingException(String.format(
                    "Unable to find variable corresponding to response '%s'.", responseName));
        return responseVariable;
    }

    private ComponentFilter findEnoFilter(ComponentType lunaticComponent) {
        String questionId = lunaticComponent.getId();
        var enoObject = enoIndex.get(questionId);
        if (enoObject == null)
            throw new MappingException(String.format(
                    "Unable to retrieve Eno object corresponding to Lunatic component '%s'.", questionId));
        if (! (enoObject instanceof Question enoQuestion))
            throw new MappingException(String.format(
                    "Eno object of type '%s' associated with Lunatic component '%s' is not a question.",
                    enoObject.getClass(), lunaticComponent));
        return enoQuestion.getComponentFilter();
    }

    /**
     * Sets the name of the filter result variable.
     * @param filterResultVariable Lunatic filter result variable.
     * @param responseName Response name.
     */
    private static void setName(CalculatedVariableType filterResultVariable, String responseName) {
        filterResultVariable.setName(FILTER_RESULT_PREFIX + responseName);
    }

    /**
     * Sets the filter result variable expression, which is the expression defined in the condition filter object.
     * @param filterResultVariable Lunatic filter result variable.
     * @param lunaticFilter Lunatic condition filter object.
     */
    private static void setExpression(CalculatedVariableType filterResultVariable, ConditionFilterType lunaticFilter) {
        LabelType expression = new LabelType();
        expression.setValue(lunaticFilter.getValue());
        expression.setType(lunaticFilter.getType());
        filterResultVariable.setExpression(expression);
    }

    /**
     * Sets the filter result variable binding dependencies, using the information in the Eno filter object.
     * @param filterResultVariable Lunatic filter result variable.
     * @param enoFilter Eno component filter object.
     */
    private static void setBindingDependencies(CalculatedVariableType filterResultVariable, ComponentFilter enoFilter) {
        enoFilter.getBindingReferences().stream()
                .map(BindingReference::getVariableName)
                .forEach(variableName -> filterResultVariable.getBindingDependencies().add(variableName));
    }

    // Note: both expression and binding dependencies could be retrieved from the Eno filter object,
    // but I try to use information that is directly in the Lunatic questionnaire as much as possible.

    /**
     * Sets dimension properties of the filter result variable, that are the same as the corresponding response
     * variable.
     * @param filterResultVariable Lunatic filter result variable.
     * @param responseVariable Lunatic response variable corresponding to the filter result variable.
     */
    private static void setDimensionProperties(CalculatedVariableType filterResultVariable, CollectedVariableType responseVariable) {
        filterResultVariable.setDimension(responseVariable.getDimension());
        filterResultVariable.setIterationReference(responseVariable.getIterationReference());
        // Note: the shape from property is not computed here, see the "shape from" processing step.
    }

}
