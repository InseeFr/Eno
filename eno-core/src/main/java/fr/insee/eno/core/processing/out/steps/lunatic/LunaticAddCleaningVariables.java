package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.eno.core.utils.vtl.VtlSyntaxUtils;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.cleaning.CleanedVariableEntry;
import fr.insee.lunatic.model.flat.cleaning.CleaningExpression;
import fr.insee.lunatic.model.flat.cleaning.CleaningType;
import fr.insee.lunatic.model.flat.cleaning.CleaningVariableEntry;
import fr.insee.lunatic.model.flat.variable.CalculatedVariableType;
import fr.insee.lunatic.model.flat.variable.VariableType;
import lombok.Getter;
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
 */
@Slf4j
@Getter
public class LunaticAddCleaningVariables implements ProcessingStep<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;
    private final EnoIndex enoIndex;
    // String: filterId - Filter: enoFilter
    private final Map<String, Filter> filterIndex = new LinkedHashMap<>();
    // String: filterId - List<String> list of filterID inside
    private final Map<String, List<String>> filterHierarchyIndex = new LinkedHashMap<>();
    private final Map<String, VariableType> variableIndex = new LinkedHashMap<>();
    private final Map<String, String> variableShapeFromIndex = new LinkedHashMap<>();
    private Map<String, List<String>> variablesByQuestion;

    /**
     * filter 1 is parent of filter2 if filter2 in inside filter 1
     * This comparator method needs filterHierarchyIndex
     */
    public final Comparator<Filter> filterComparator = (filter1, filter2) -> {
        boolean isParentOfFilter2 = filterHierarchyIndex.get(filter1.getId()).contains(filter2.getId());
        boolean isChildOfFilter2 = filterHierarchyIndex.get(filter2.getId()).contains(filter1.getId());
        if(isParentOfFilter2) return -1;
        if(isChildOfFilter2) return 1;
        return 0;
    };

    private List<String> getFilterItemInside(ItemReference itemReference){
        List<String> filterIds = new ArrayList<>();
        if(ItemReference.ItemType.FILTER.equals(itemReference.getType())) {
            filterIds.add(itemReference.getId());
            Filter filter = (Filter) enoIndex.get(itemReference.getId());
            filterIds.addAll(
                    filter.getFilterItems().stream()
                            .map(this::getFilterItemInside)
                            .flatMap(Collection::stream)
                            .toList()
            );
        }
        if(ItemReference.ItemType.SEQUENCE.equals(itemReference.getType()) || ItemReference.ItemType.SUBSEQUENCE.equals(itemReference.getType())){
            AbstractSequence sequence = (AbstractSequence) enoIndex.get(itemReference.getId());
            filterIds.addAll(
                    sequence.getSequenceItems().stream()
                            .map(this::getFilterItemInside)
                            .flatMap(Collection::stream)
                            .toList()
            );
        }
        return filterIds;

    }

    public LunaticAddCleaningVariables(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        this.enoIndex = enoQuestionnaire.getIndex();
        this.preProcessUtilsIndex();
    }


    private void preProcessUtilsIndex(){
        // Create Filter hierarchy Index (Filter which include other filter)
        enoQuestionnaire.getFilters().forEach(filter -> {
            List<String> filterIdsInside = filter.getFilterItems().stream()
                    .map(this::getFilterItemInside)
                    .flatMap(Collection::stream)
                    .toList();
            filterHierarchyIndex.put(filter.getId(), filterIdsInside);
        });
        // Create order filter Index (only Filter), based on hierarchy
        List<Filter> sortedFilters = new ArrayList<>(enoQuestionnaire.getFilters());
        Collections.sort(sortedFilters, filterComparator);
        sortedFilters.forEach(f -> filterIndex.put(f.getId(), f));

    }

    public void preProcessVariablesAndShapeFrom(Questionnaire lunaticQuestionnaire){
        variablesByQuestion = getCollectedVariablesByQuestion(lunaticQuestionnaire);
        // Create filter shapeFrom index based on filterHierarchyIndex and loop
        lunaticQuestionnaire.getVariables().stream()
                .forEach(variableType -> {
                    variableIndex.put(variableType.getName(), variableType);
                    variableShapeFromIndex.put(variableType.getName(), getShapeFromOfVariable(lunaticQuestionnaire, variableType.getName()));
                });
    }
    /**
     *
     * @param variableName
     * @return ShapeFrom: the variable Name of filter scope
     */
    public String getShapeFromOfVariable(Questionnaire lunaticQuestionnaire, String variableName){
        VariableType variableType = variableIndex.get(variableName);
        Optional<ComponentType> iterationComponent = lunaticQuestionnaire.getComponents().stream()
                .filter(component -> variableType.getIterationReference() != null
                        && variableType.getIterationReference().equals(component.getId()))
                .findAny();
        if (iterationComponent.isEmpty()) return null;
        return LunaticUtils.getResponseNames(iterationComponent.get()).getFirst();
    }

    /**
     *
     * @param filter
     * @return List of variable Name collected inside the scope of filter
     */
    public List<String> getCollectedVariablesInFilter(Filter filter) {
        List<String> collectedVarForFilter = new ArrayList<>();
        filter.getFilterScope().forEach(
                itemReference -> {
                    if (StructureItemReference.StructureItemType.QUESTION.equals(itemReference.getType())) {
                        collectedVarForFilter.addAll(variablesByQuestion.get(itemReference.getId()));
                    }
                    if (StructureItemReference.StructureItemType.SEQUENCE.equals(itemReference.getType()) ||
                            StructureItemReference.StructureItemType.SUBSEQUENCE.equals(itemReference.getType())) {
                        collectedVarForFilter.addAll(getCollectedVarsInSequence((AbstractSequence) enoIndex.get(itemReference.getId())));
                    }
                }
        );
        return collectedVarForFilter;
    }

    /**
     *
     * @param abstractSequence
     * @return List of variable Name collected inside the scope of sequence or subsequence
     */
    public List<String> getCollectedVarsInSequence(AbstractSequence abstractSequence) {
        List<String> collectedVarInSequence = new ArrayList<>();
        abstractSequence.getSequenceStructure().stream().forEach(itemReference -> {
                    if (StructureItemReference.StructureItemType.QUESTION.equals(itemReference.getType())) {
                        collectedVarInSequence.addAll(variablesByQuestion.get(itemReference.getId()));
                    } else {
                        collectedVarInSequence.addAll(getCollectedVarsInSequence((AbstractSequence) enoIndex.get(itemReference.getId())));
                    }
                }
        );
        return collectedVarInSequence;
    }

    public List<String> getFinalBindingReferencesWithCalculatedVariables(CalculatedExpression expression) {
        return expression.getBindingReferences().stream()
                .map(BindingReference::getVariableName)
                .map(vName -> variableIndex.get(vName))
                .map(variable -> {
                    List<String> variablesNames = new ArrayList<>(List.of(variable.getName()));
                    if ((variable instanceof CalculatedVariableType calculatedVariable)) variablesNames.addAll(calculatedVariable.getBindingDependencies());
                    return variablesNames;
                })
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    public List<String> removeCalculatedVariables(List<String> variableNames){
        return variableNames.stream()
                .filter(vName -> !(variableIndex.get(vName) instanceof CalculatedVariableType))
                .toList();
    }

    /**
     *
     * @param filter
     * @param allVariableNamesForFilter
     * @return true if an aggregator function of VTL language is used in filter (or in its dependencies)
     */
    private boolean isAggregatorUsedInFilter(Filter filter, List<String> allVariableNamesForFilter){
        String filterExpression = filter.getExpression().getValue();
        if(VtlSyntaxUtils.isAggregatorUsedInsideExpression(filterExpression)) return true;
        for(String vName: allVariableNamesForFilter){
            VariableType variable = variableIndex.get(vName);
            if(variable instanceof CalculatedVariableType calculatedVariable &&
                    VtlSyntaxUtils.isAggregatorUsedInsideExpression(calculatedVariable.getExpression().getValue())) {
                    return true;
            }
        }
        return false;
    }

    public static List<String> getCollectedVariablesByComponent(ComponentType componentType){
        List<String> collectedVars = new ArrayList<>();
        if (componentType instanceof ComponentSimpleResponseType simpleResponseType) {
            collectedVars.add(simpleResponseType.getResponse().getName());
            if (componentType instanceof Suggester suggester && suggester.getArbitrary() != null) {
                collectedVars.add(suggester.getArbitrary().getResponse().getName());
            }
            if (componentType instanceof CheckboxOne checkboxOne) {
                collectedVars.addAll(checkboxOne.getOptions().stream()
                        .filter(o -> o.getDetail() != null && o.getDetail().getResponse() != null)
                        .map(o -> o.getDetail().getResponse().getName()).toList());
            }
            if (componentType instanceof Radio radio) {
                collectedVars.addAll(radio.getOptions().stream()
                        .filter(o -> o.getDetail() != null && o.getDetail().getResponse() != null)
                        .map(o -> o.getDetail().getResponse().getName()).toList());
            }
            if (componentType instanceof Dropdown dropdown) {
                collectedVars.addAll(dropdown.getOptions().stream()
                        .filter(o -> o.getDetail() != null && o.getDetail().getResponse() != null)
                        .map(o -> o.getDetail().getResponse().getName()).toList());
            }
        }
        if (componentType instanceof ComponentMultipleResponseType) {
            switch (componentType.getComponentType()) {
                case TABLE -> collectedVars.addAll(((Table) componentType).getBodyLines().stream()
                        .map(BodyLine::getBodyCells)
                        .flatMap(Collection::stream)
                        .map(BodyCell::getResponse)
                        .filter(Objects::nonNull)
                        .map(ResponseType::getName)
                        .toList());

                case ROSTER_FOR_LOOP ->
                        collectedVars.addAll(((RosterForLoop) componentType).getComponents().stream()
                                .map(BodyCell::getResponse)
                                .filter(Objects::nonNull)
                                .map(ResponseType::getName)
                                .toList());

                case CHECKBOX_GROUP -> {
                    collectedVars.addAll(((CheckboxGroup) componentType).getResponses().stream()
                            .map(ResponseCheckboxGroup::getResponse)
                            .map(ResponseType::getName)
                            .toList());

                    collectedVars.addAll(((CheckboxGroup) componentType).getResponses().stream()
                            .map(ResponseCheckboxGroup::getDetail)
                            .filter(Objects::nonNull)
                            .map(DetailResponse::getResponse)
                            .map(ResponseType::getName)
                            .toList());
                }
            }
        }
        return collectedVars;
    }

    /**
     *
     * @param lunaticQuestionnaire
     * @return A map of QuestionName: List of collected variables in the question.
     */
    public Map<String, List<String>> getCollectedVariablesByQuestion(Questionnaire lunaticQuestionnaire) {
        Map<String, List<String>> questionCollectedVarIndex = new HashMap<>();
        lunaticQuestionnaire.getComponents().stream()
                .map(componentType -> {
                    if (componentType instanceof Loop loop) return loop.getComponents();
                    return List.of(componentType);
                })
                .flatMap(Collection::stream)
                .forEach(componentType -> {
                    String questionId = componentType.getId();
                    List<String> collectedVariables = getCollectedVariablesByComponent(componentType);
                    questionCollectedVarIndex.put(questionId, collectedVariables);
                });
        return questionCollectedVarIndex;
    }

    /**
     * Create the 'cleaning' block in the given Lunatic questionnaire. (See class documentation for details.)
     *
     * @param lunaticQuestionnaire A Lunatic questionnaire object.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        preProcessVariablesAndShapeFrom(lunaticQuestionnaire);
        CleaningType cleaning = new CleaningType();
        filterIndex.forEach((filterId, filter) -> {
            CalculatedExpression filterExpression = filter.getExpression();
            List<String> allVariablesThatInfluenceFilterExpression = getFinalBindingReferencesWithCalculatedVariables(filterExpression);
            List<String> variablesCollectedInsideFilter = getCollectedVariablesInFilter(filter);
            boolean isAggregatorUsedOfFilter = isAggregatorUsedInFilter(filter, allVariablesThatInfluenceFilterExpression);
            removeCalculatedVariables(allVariablesThatInfluenceFilterExpression).stream().forEach(
                    variableName -> {
                        boolean isExistCleaningVariableEntry = cleaning.getCleaningVariableNames().contains(variableName);
                        if (!isExistCleaningVariableEntry){
                            CleaningVariableEntry cleaningVariableEntry = new CleaningVariableEntry(variableName);
                            variablesCollectedInsideFilter.forEach(variableToClean -> {
                                CleanedVariableEntry cleanedVariableEntry = new CleanedVariableEntry(variableToClean);
                                cleanedVariableEntry.getCleaningExpressions().add(
                                        new CleaningExpression(
                                                filterExpression.getValue(),
                                                variableShapeFromIndex.get(variableToClean),
                                                isAggregatorUsedOfFilter)
                                );
                                cleaningVariableEntry.addCleanedVariable(cleanedVariableEntry);
                            });
                            cleaning.addCleaningEntry(cleaningVariableEntry);
                        }
                        else {
                            CleaningVariableEntry existingCleaningVariableEntry = cleaning.getCleaningEntry(variableName);
                            variablesCollectedInsideFilter.forEach(variableToClean -> {
                                boolean isExistCleanedVariableEntry = existingCleaningVariableEntry.getCleanedVariableNames().contains(variableToClean);
                                if(!isExistCleanedVariableEntry){
                                    CleanedVariableEntry cleanedVariableEntry = new CleanedVariableEntry(variableToClean);
                                    cleanedVariableEntry.getCleaningExpressions().add(new CleaningExpression(
                                            filterExpression.getValue(),
                                            variableShapeFromIndex.get(variableToClean),
                                            isAggregatorUsedOfFilter));
                                    cleaning.getCleaningEntry(variableName).addCleanedVariable(cleanedVariableEntry);
                                } else {
                                    CleanedVariableEntry existingCleanedVariableEntry = existingCleaningVariableEntry.getCleanedVariable(variableToClean);
                                    existingCleanedVariableEntry.getCleaningExpressions().add(new CleaningExpression(
                                            filterExpression.getValue(),
                                            variableShapeFromIndex.get(variableToClean),
                                            isAggregatorUsedOfFilter));
                                    cleaning.getCleaningEntry(variableName).addCleanedVariable(existingCleanedVariableEntry);
                                }
                            });
                        }
                    }
            );
        });
        lunaticQuestionnaire.setCleaning(cleaning);
    }


}
