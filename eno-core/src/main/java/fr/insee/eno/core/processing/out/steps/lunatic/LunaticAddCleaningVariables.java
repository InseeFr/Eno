package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.lunatic.CleaningConcernedVariable;
import fr.insee.eno.core.model.lunatic.CleaningVariable;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class LunaticAddCleaningVariables implements ProcessingStep<Questionnaire> {

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        List<CleaningVariable> cleaningVariables = createCleaningVariables(components);

        if(cleaningVariables.isEmpty()) {
            return;
        }

        CleaningType cleaningType = new CleaningType();
        lunaticQuestionnaire.setCleaning(cleaningType);

        cleaningType.getAny().addAll(groupCleaningVariables(cleaningVariables));
    }

    /**
     * create cleaning variables for all response components
     * @param components components to process
     * @return all cleaing variables for these components
     */
    private List<CleaningVariable> createCleaningVariables(List<ComponentType> components) {
        List<CleaningVariable> cleaningVariables = new ArrayList<>();

        components.stream()
                .filter(component -> component.getConditionFilter() != null)
                .forEach(componentType -> {
                    if(componentType instanceof ComponentSimpleResponseType) {
                        cleaningVariables.addAll(createCleaningVariablesFromSimpleResponseComponent(componentType));
                        return;
                    }

                    if(componentType instanceof ComponentMultipleResponseType) {
                        cleaningVariables.addAll(createCleaningVariablesFromMultipleResponseComponent(componentType));
                    }
        });

        components.stream()
                .filter(ComponentNestingType.class::isInstance)
                .map(ComponentNestingType.class::cast)
                .forEach(nestingComponent -> cleaningVariables.addAll(createCleaningVariables(nestingComponent.getComponents())));

        return cleaningVariables;
    }

    /**
     * Create cleaning variables for a simple response type component
     * @param componentType component to process (must be a simple response type)
     * @return cleaning variables for this component
     */
    private List<CleaningVariable> createCleaningVariablesFromSimpleResponseComponent(ComponentType componentType) {
        if(!(componentType instanceof ComponentSimpleResponseType simpleResponseType)) {
            throw new IllegalArgumentException(String.format("Cannot create cleaning variable from this simple response component %s", componentType.getId()));
        }

        List<String> bindingDependencies = componentType.getConditionFilter().getBindingDependencies();
        if(bindingDependencies.isEmpty()) {
            return new ArrayList<>();
        }

        CleaningConcernedVariable concernedVariable = new CleaningConcernedVariable(simpleResponseType.getResponse().getName(), componentType.getConditionFilter().getValue());
        return bindingDependencies.stream()
                .map(bindingDependency -> new CleaningVariable(bindingDependency, List.of(concernedVariable)))
                .toList();
    }

    /**
     * Create cleaning variables for a multiple response type component
     * @param componentType component to process (must be a multiple response type)
     * @return cleaning variables for this component
     */
    private List<CleaningVariable> createCleaningVariablesFromMultipleResponseComponent(ComponentType componentType) {
        if(!(componentType instanceof ComponentMultipleResponseType)) {
            throw new IllegalArgumentException(String.format("Cannot create cleaning variable from this multiple response component %s", componentType.getId()));
        }
        List<String> bindingDependencies = componentType.getConditionFilter().getBindingDependencies();
        if(bindingDependencies.isEmpty()) {
            return new ArrayList<>();
        }
        String conditionFilter = componentType.getConditionFilter().getValue();

        List<CleaningConcernedVariable> concernedVariables;

        switch(componentType.getComponentType()) {
            case TABLE -> concernedVariables = ((Table) componentType).getBodyLines().stream()
                    .map(BodyLine::getBodyCells)
                    .flatMap(Collection::stream)
                    .map(BodyCell::getResponse)
                    .filter(Objects::nonNull)
                    .map(ResponseType::getName)
                    .map(name -> new CleaningConcernedVariable(name, conditionFilter))
                    .toList();

            case ROSTER_FOR_LOOP -> concernedVariables = ((RosterForLoop) componentType).getComponents().stream()
                    .map(BodyCell::getResponse)
                    .filter(Objects::nonNull)
                    .map(ResponseType::getName)
                    .map(name -> new CleaningConcernedVariable(name, conditionFilter))
                    .toList();

            case CHECKBOX_GROUP -> concernedVariables = ((CheckboxGroup) componentType).getResponses().stream()
                    .map(ResponsesCheckboxGroup::getResponse)
                    .map(ResponseType::getName)
                    .map(name -> new CleaningConcernedVariable(name, conditionFilter))
                    .toList();

            default -> throw new IllegalArgumentException(String.format("Cannot create cleaning variable from this multiple response component %s, componentType not defined", componentType.getId()));
        }

        return bindingDependencies.stream()
                .map(bindingDependency -> new CleaningVariable(bindingDependency, concernedVariables))
                .toList();
    }

    /**
     * Regroup cleaning variables with same name together
     * @param variables cleaning variables to regroup
     * @return grouped variables
     */
    private List<CleaningVariable> groupCleaningVariables(List<CleaningVariable> variables) {
        Map<String, CleaningVariable> groupCleaningVariables = new LinkedHashMap<>();
        for (CleaningVariable variable : variables) {
            if(!groupCleaningVariables.containsKey(variable.getName())) {
                groupCleaningVariables.put(variable.getName(), variable);
                continue;
            }

            CleaningVariable groupVariable = groupCleaningVariables.get(variable.getName());
            List<CleaningConcernedVariable> concernedVariables = new ArrayList<>(groupVariable.getConcernedVariables());
            concernedVariables.addAll(variable.getConcernedVariables());
            groupVariable.setConcernedVariables(concernedVariables);
        }
        return groupCleaningVariables.values().stream()
                .toList();
    }

}
