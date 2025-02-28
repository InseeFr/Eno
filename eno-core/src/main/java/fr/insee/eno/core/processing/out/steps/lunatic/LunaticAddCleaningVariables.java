package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.VariableType;
import fr.insee.lunatic.model.flat.variable.VariableTypeEnum;
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

    private final EnoQuestionnaire enoQuestionnaire;
    private final EnoIndex enoIndex;
    private HashMap<String, Filter> filterIndex;
    private HashMap<String, Variable> variableIndex;


    public LunaticAddCleaningVariables(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        this.enoIndex = enoQuestionnaire.getIndex();
        enoQuestionnaire.getVariables().forEach(v -> this.variableIndex.put(v.getName(),v));
        enoQuestionnaire.getFilters().forEach(f -> this.filterIndex.put(f.getId(),f));
    }

    private List<String> getCollectedVariablesInFilter(Object filter, Map<String, List<String>> collectedVarsByQuestion){
        List<String> collectedVarForFilter = new ArrayList<>();
        filter.getFilterItems().forEach(
                itemReference -> {
                    if(ItemReference.ItemType.FILTER.equals(itemReference.getType())){
                        collectedVarForFilter.addAll(
                                getCollectedVariablesInFilter(itemReference, collectedVarsByQuestion)
                        )
                    }
                }
        );
    }

    private Map<String,List<String>> getCollectedVariablesByQuestion(Questionnaire lunaticQuestionnaire) {
        Map<String, List<String>> questionCollectedVarIndex = new HashMap<>();
        lunaticQuestionnaire.getComponents().forEach(componentType -> {
            String questionId = componentType.getId();
            List<String> collectedVars = new ArrayList<>();
            if (componentType instanceof ComponentSimpleResponseType) {
                collectedVars = List.of(((ComponentSimpleResponseType) componentType).getResponse().getName());
            }
            if (componentType instanceof ComponentMultipleResponseType) {
                switch(componentType.getComponentType()) {
                    case TABLE -> collectedVars = ((Table) componentType).getBodyLines().stream()
                            .map(BodyLine::getBodyCells)
                            .flatMap(Collection::stream)
                            .map(BodyCell::getResponse)
                            .filter(Objects::nonNull)
                            .map(ResponseType::getName)
                            .toList();

                    case ROSTER_FOR_LOOP ->
                            collectedVars = ((RosterForLoop) componentType).getComponents().stream()
                                    .map(BodyCell::getResponse)
                                    .filter(Objects::nonNull)
                                    .map(ResponseType::getName)
                                    .toList();

                    case CHECKBOX_GROUP ->
                            collectedVars = ((CheckboxGroup) componentType).getResponses().stream()
                                    .map(ResponseCheckboxGroup::getResponse)
                                    .map(ResponseType::getName)
                                    .toList();
                    default -> collectedVars = List.of();
                }
            }

            questionCollectedVarIndex.put(questionId, collectedVars);
        });
        return questionCollectedVarIndex;
    }

    /**
     * Create the 'cleaning' block in the given Lunatic questionnaire. (See class documentation for details.)
     * @param lunaticQuestionnaire A Lunatic questionnaire object.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        List<CleaningVariableEntry> cleaningVariables = createCleaningVariables(components, lunaticQuestionnaire);

        enoQuestionnaire.getFilters().stream().forEach(
                filter -> {
                    System.out.println("==========");
                    System.out.println("Expression");
                    System.out.println("----------");
                    System.out.println(filter.getExpression().getValue());
                    System.out.println("----------");
                    System.out.println("BindingReferences");
                    System.out.println("----------");
                    filter.getExpression().getBindingReferences().stream()
                                    .map(b->b.getVariableName())
                                    .map(id -> enoQuestionnaire.getVariables()
                                            .stream()
                                            .filter(v->id.equals(v.getName()))
                                            .findFirst().get())
                                    .forEach(variable -> {
                                                if(variable instanceof CalculatedVariable){
                                                    System.out.println("Calculated "+variable.getName());
                                                }
                                                /* else if (variable instanceof ExternalVariable) {
                                                    System.out.println("External "+variable.getName());
                                                } else  System.out.println("Collected "+variable.getName());
                                            */
                                            }

                                        )
                                    ;
                    System.out.println("Items filtered");
                    System.out.println("----------");
                    filter.getFilterItems().forEach(
                            itemReference -> System.out.println(itemReference.getId() + " " + itemReference.getType())
                    );
                }
        );

        if(cleaningVariables.isEmpty()) {
            return;
        }

        CleaningType cleaningType = new CleaningType();

        cleaningVariables.forEach(cleaningType::addCleaningEntry);

        lunaticQuestionnaire.setCleaning(cleaningType);
    }

    /**
     * Create cleaning variables for all response components. Can be called recursively for components that contain
     * other components (such as loops).
     * @param components A list of components.
     * @param lunaticQuestionnaire Lunatic questionnaire to process.
     * @return All cleaning entries for the questionnaire's components.
     */
    private List<CleaningVariableEntry> createCleaningVariables(List<ComponentType> components, Questionnaire lunaticQuestionnaire) {
        List<CleaningVariableEntry> cleaningEntries = new ArrayList<>();

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

        return groupCleaningVariables(cleaningEntries);
    }

    /**
     * Create cleaning variables for a simple response type component
     * @param componentType component to process (must be a simple response type)
     * @return cleaning variables for this component
     */
    private List<CleaningVariableEntry> createCleaningVariablesFromSimpleResponseComponent(
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
        CleanedVariableEntry cleanedVariableEntry = new CleanedVariableEntry(
                simpleResponseType.getResponse().getName(),
                componentType.getConditionFilter().getValue());

        // Cleaning entries
        return bindingDependencies.stream()
                .map(bindingDependency -> {
                    CleaningVariableEntry cleaningVariableEntry = new CleaningVariableEntry(bindingDependency);
                    cleaningVariableEntry.addCleanedVariable(cleanedVariableEntry);
                    return cleaningVariableEntry;
                })
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
                .map(VariableType::getName)
                .filter(variableNames::contains)
                .toList();
    }

    /**
     * Create cleaning variables for a multiple response type component
     * @param componentType component to process (must be a multiple response type)
     * @return cleaning variables for this component
     */
    private List<CleaningVariableEntry> createCleaningVariablesFromMultipleResponseComponent(ComponentType componentType) {
        if(!(componentType instanceof ComponentMultipleResponseType)) {
            throw new IllegalArgumentException(String.format("Cannot create cleaning variable from this multiple response component %s", componentType.getId()));
        }
        List<String> bindingDependencies = componentType.getConditionFilter().getBindingDependencies();
        if(bindingDependencies.isEmpty()) {
            return new ArrayList<>();
        }
        String conditionFilter = componentType.getConditionFilter().getValue();

        List<CleanedVariableEntry> cleanedVariableEntries;

        switch(componentType.getComponentType()) {
            case TABLE -> cleanedVariableEntries = ((Table) componentType).getBodyLines().stream()
                    .map(BodyLine::getBodyCells)
                    .flatMap(Collection::stream)
                    .map(BodyCell::getResponse)
                    .filter(Objects::nonNull)
                    .map(ResponseType::getName)
                    .map(name -> new CleanedVariableEntry(name, conditionFilter))
                    .toList();

            case ROSTER_FOR_LOOP -> cleanedVariableEntries = ((RosterForLoop) componentType).getComponents().stream()
                    .map(BodyCell::getResponse)
                    .filter(Objects::nonNull)
                    .map(ResponseType::getName)
                    .map(name -> new CleanedVariableEntry(name, conditionFilter))
                    .toList();

            case CHECKBOX_GROUP -> cleanedVariableEntries = ((CheckboxGroup) componentType).getResponses().stream()
                    .map(ResponseCheckboxGroup::getResponse)
                    .map(ResponseType::getName)
                    .map(name -> new CleanedVariableEntry(name, conditionFilter))
                    .toList();

            default -> throw new IllegalArgumentException(String.format("Cannot create cleaning variable from this multiple response component %s, componentType not defined", componentType.getId()));
        }

        return bindingDependencies.stream()
                .map(bindingDependency -> {
                    CleaningVariableEntry cleaningVariableEntry = new CleaningVariableEntry(bindingDependency);
                    cleanedVariableEntries.forEach(cleaningVariableEntry::addCleanedVariable);
                    return cleaningVariableEntry;
                })
                .toList();
    }

    /**
     * Regroup cleaning variables with same name together
     * @param cleaningEntries cleaning variables to regroup
     * @return grouped variables
     */
    private List<CleaningVariableEntry> groupCleaningVariables(List<CleaningVariableEntry> cleaningEntries) {

        Map<String, CleaningVariableEntry> groupCleaningVariables = new LinkedHashMap<>();

        for (CleaningVariableEntry cleaningEntry : cleaningEntries) {
            if(!groupCleaningVariables.containsKey(cleaningEntry.getCleaningVariableName())) {
                groupCleaningVariables.put(cleaningEntry.getCleaningVariableName(), cleaningEntry);
                continue;
            }

            CleaningVariableEntry groupVariable = groupCleaningVariables.get(cleaningEntry.getCleaningVariableName());
            cleaningEntry.getCleanedVariableNames().forEach(cleanedVariableName ->
                    groupVariable.addCleanedVariable(cleaningEntry.getCleanedVariable(cleanedVariableName)));
        }

        return groupCleaningVariables.values().stream()
                .toList();
    }

}
