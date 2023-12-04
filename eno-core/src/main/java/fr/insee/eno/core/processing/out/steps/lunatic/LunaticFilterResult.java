package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable.ShapefromAttributeRetrieval;
import fr.insee.lunatic.model.flat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LunaticFilterResult implements ProcessingStep<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;
    private final ShapefromAttributeRetrieval shapefromAttributeRetrieval;
    public static final String FILTER_RESULT_PREFIX = "FILTER_RESULT_";

    public LunaticFilterResult(EnoQuestionnaire enoQuestionnaire, ShapefromAttributeRetrieval shapefromAttributeRetrieval) {
        this.enoQuestionnaire = enoQuestionnaire;
        this.shapefromAttributeRetrieval = shapefromAttributeRetrieval;
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        List<VariableType> calculatedVariables = generateFilterVariables(lunaticQuestionnaire.getComponents());
        lunaticQuestionnaire.getVariables().addAll(calculatedVariables);
    }

    /**
     * Generate filter result calculated variables from a component list
     * @param components list of components
     * @return filter result calculated variables
     */
    private List<VariableType> generateFilterVariables(List<ComponentType> components) {
        List<VariableType> filterVariables = new ArrayList<>();
        for(ComponentType component : components) {
            generateFilterVariable(component)
                    .ifPresent(filterVariables::add);
            if(component instanceof ComponentNestingType nestingComponent) {
                filterVariables.addAll(generateFilterVariables(nestingComponent.getComponents()));
            }
        }
        return filterVariables;
    }

    /**
     * generate a filter result calculated variable for a component
     * @param component generate a filter variable from this component
     * @return a filter variable if the component is a question with condition filter, otherwise none
     */
    private Optional<VariableType> generateFilterVariable(ComponentType component) {
        if(!isQuestionWithConditionFilter(component)) {
            return Optional.empty();
        }

        VariableType filterVariable = new VariableType();
        filterVariable.setVariableType(VariableTypeEnum.CALCULATED);
        Question question = (Question) enoQuestionnaire.getIndex().get(component.getId());
        String questionName = question.getName();
        filterVariable.setName(FILTER_RESULT_PREFIX + questionName);
        shapefromAttributeRetrieval.getShapeFrom(questionName, enoQuestionnaire)
                .ifPresent(shapeFromVariable -> filterVariable.setShapeFrom(shapeFromVariable.getName()));
        ConditionFilterType conditionFilter = component.getConditionFilter();
        ComponentFilter componentFilter = question.getComponentFilter();
        if(componentFilter != null && !componentFilter.getBindingReferences().isEmpty()) {
            componentFilter.getBindingReferences().stream()
                    .map(BindingReference::getVariableName)
                    .forEach(variableName -> filterVariable.getBindingDependencies().add(variableName));
        }
        LabelType expression = new LabelType();
        expression.setType(conditionFilter.getTypeEnum());
        expression.setValue(conditionFilter.getValue());
        filterVariable.setExpression(expression);
        return Optional.of(filterVariable);
    }

    /**
     * check if a component is a question with a condition filter
     * @param component component to check
     * @return true if it is a question with condition filter, false otherwise
     */
    private boolean isQuestionWithConditionFilter(ComponentType component) {
        return component.getConditionFilter() != null && (component instanceof ComponentSimpleResponseType || component instanceof ComponentMultipleResponseType);
    }
}
