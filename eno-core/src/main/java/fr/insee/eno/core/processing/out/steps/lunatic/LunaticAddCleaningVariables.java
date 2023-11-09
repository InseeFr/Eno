package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.lunatic.CleaningConcernedVariable;
import fr.insee.eno.core.model.lunatic.CleaningEntry;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Processing step to add the 'cleaning' block in the Lunatic questionnaire.
 * In Lunatic, the cleaning consists in emptying variables when the scope of a filter changes, due to the modification
 * of some collected variable.
 * Important note: the cleaning only concerns COLLECTED variables.
 * Cleaning entries have variable names as keys, and list of variable names as values.
 * Keys are variables that trigger a cleaning action (i.e. variables used in filter expressions).
 * Values are the variables that may to be cleaned when the key variable is modified + the filter expression (to
 * determine if the variable has to be cleaned or not).
 * */
@Slf4j
public class LunaticAddCleaningVariables implements ProcessingStep<Questionnaire> {

    /**
     * Create the 'cleaning' block in the given Lunatic questionnaire. (See class documentation for details.)
     * @param lunaticQuestionnaire A Lunatic questionnaire object.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        List<CleaningEntry> cleaningVariables = createCleaningVariables(components, lunaticQuestionnaire);

        if(cleaningVariables.isEmpty()) {
            return;
        }

        CleaningType cleaningType = new CleaningType();
        lunaticQuestionnaire.setCleaning(cleaningType);

        cleaningType.getAny().addAll(groupCleaningVariables(cleaningVariables));
    }

    /**
     * Create cleaning variables for all response components. Can be called recursively for components that contain
     * other components (such as loops).
     * @param components A list of components.
     * @param lunaticQuestionnaire Lunatic questionnaire to process.
     * @return All cleaning entries for the questionnaire's components.
     */
    private List<CleaningEntry> createCleaningVariables(List<ComponentType> components, Questionnaire lunaticQuestionnaire) {
        List<CleaningEntry> cleaningEntries = new ArrayList<>();

        components.stream()
                .filter(component -> component.getConditionFilter() != null)
                .forEach(componentType -> {
                    if(componentType instanceof ComponentSimpleResponseType) {
                        cleaningEntries.addAll(
                                createCleaningVariablesFromSimpleResponseComponent(componentType, lunaticQuestionnaire));
                        return;
                    }

                    if(componentType instanceof ComponentMultipleResponseType) {
                        cleaningEntries.addAll(createCleaningVariablesFromMultipleResponseComponent(componentType));
                    }
        });

        components.stream()
                .filter(ComponentNestingType.class::isInstance)
                .map(ComponentNestingType.class::cast)
                .forEach(nestingComponent -> cleaningEntries.addAll(createCleaningVariables(nestingComponent.getComponents(), lunaticQuestionnaire)));

        return cleaningEntries;
    }

    /**
     * Create cleaning variables for a simple response type component
     * @param componentType component to process (must be a simple response type)
     * @return cleaning variables for this component
     */
    private List<CleaningEntry> createCleaningVariablesFromSimpleResponseComponent(
            ComponentType componentType, Questionnaire lunaticQuestionnaire) {
        if(!(componentType instanceof ComponentSimpleResponseType simpleResponseType)) {
            throw new IllegalArgumentException(String.format(
                    "Cannot create cleaning variable from this simple response component %s", componentType.getId()));
        }

        // Cleaning keys
        List<String> bindingDependencies = filterNonCollectedVariables(
                componentType.getConditionFilter().getBindingDependencies(), lunaticQuestionnaire);
        if (bindingDependencies.isEmpty()) {
            return new ArrayList<>();
        }

        // Cleaning value (here one value per key, objects with same key are grouped together afterward)
        CleaningConcernedVariable concernedVariable = new CleaningConcernedVariable(
                simpleResponseType.getResponse().getName(),
                componentType.getConditionFilter().getValue());

        // Cleaning entries
        return bindingDependencies.stream()
                .map(bindingDependency -> new CleaningEntry(bindingDependency, List.of(concernedVariable)))
                .toList();
    }

    /**
     * Returns a new list containing only collected variable names among ones in the given list.
     * @param variableNames List of variable names.
     * @param lunaticQuestionnaire A Lunatic questionnaire.
     * @return A new list containing only collected variable names.
     */
    private static List<String> filterNonCollectedVariables(List<String> variableNames, Questionnaire lunaticQuestionnaire) {
        return lunaticQuestionnaire.getVariables().stream()
                .filter(variable -> VariableTypeEnum.COLLECTED.equals(variable.getVariableType()))
                .map(IVariableType::getName)
                .filter(variableNames::contains)
                .toList();
    }

    /**
     * Create cleaning variables for a multiple response type component
     * @param componentType component to process (must be a multiple response type)
     * @return cleaning variables for this component
     */
    private List<CleaningEntry> createCleaningVariablesFromMultipleResponseComponent(ComponentType componentType) {
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
                .map(bindingDependency -> new CleaningEntry(bindingDependency, concernedVariables))
                .toList();
    }

    /**
     * Regroup cleaning variables with same name together
     * @param cleaningEntries cleaning variables to regroup
     * @return grouped variables
     */
    private List<CleaningEntry> groupCleaningVariables(List<CleaningEntry> cleaningEntries) {
        Map<String, CleaningEntry> groupCleaningVariables = new LinkedHashMap<>();
        for (CleaningEntry cleaningEntry : cleaningEntries) {
            if(!groupCleaningVariables.containsKey(cleaningEntry.getVariableName())) {
                groupCleaningVariables.put(cleaningEntry.getVariableName(), cleaningEntry);
                continue;
            }

            CleaningEntry groupVariable = groupCleaningVariables.get(cleaningEntry.getVariableName());
            List<CleaningConcernedVariable> concernedVariables = new ArrayList<>(groupVariable.getConcernedVariables());
            concernedVariables.addAll(cleaningEntry.getConcernedVariables());
            groupVariable.setConcernedVariables(concernedVariables);
        }
        return groupCleaningVariables.values().stream()
                .toList();
    }

}
