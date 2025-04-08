package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.model.response.CodeFilter;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference;
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

import static fr.insee.eno.core.model.navigation.ComponentFilter.DEFAULT_FILTER_VALUE;
import static fr.insee.eno.core.utils.LunaticUtils.findComponentById;
import static fr.insee.eno.core.utils.vtl.VtlSyntaxUtils.joinByANDLogicExpression;

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
        sortedFilters.sort(filterComparator);
        sortedFilters.forEach(f -> filterIndex.put(f.getId(), f));

    }

    public void preProcessVariablesAndShapeFrom(Questionnaire lunaticQuestionnaire){
        variablesByQuestion = getCollectedVariablesByQuestion(lunaticQuestionnaire);
        // Create filter shapeFrom index based on filterHierarchyIndex and loop
        lunaticQuestionnaire.getVariables()
                .forEach(lunaticVariable -> {
                    variableIndex.put(lunaticVariable.getName(), lunaticVariable);
                    variableShapeFromIndex.put(lunaticVariable.getName(), getShapeFromOfVariable(lunaticQuestionnaire, lunaticVariable.getName()));
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
     * @param multipleChoiceQuestion
     * @return List of variable Name collected inside the scope of filter
     */
    private List<String> getCollectedVariablesInCodeFilter(SimpleMultipleChoiceQuestion multipleChoiceQuestion) {
        List<String> collectedVarForFilter = new ArrayList<>();

        return collectedVarForFilter;
    }

    /**
     *
     * @param uniqueChoiceQuestion
     * @return List of variable Name collected inside the scope of filter
     */
    private List<String> getCollectedVariablesInCodeFilter(UniqueChoiceQuestion uniqueChoiceQuestion) {
        List<String> collectedVarForFilter = new ArrayList<>();

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
                .map(variableIndex::get)
                .map(variable -> {
                    List<String> variablesNames = new ArrayList<>(List.of(variable.getName()));
                    if ((variable instanceof CalculatedVariableType calculatedVariable)) variablesNames.addAll(calculatedVariable.getBindingDependencies());
                    return variablesNames;
                })
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }


    public List<String> getFinalBindingReferencesWithCalculatedVariables(ConditionFilterType expression) {
        return expression.getBindingDependencies().stream()
                .map(variableIndex::get)
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
     * @param filterExpression
     * @param allVariableNamesForFilter
     * @return true if an aggregator function of VTL language is used in filter (or in its dependencies)
     */
    private boolean isAggregatorUsedInFilter(String filterExpression, List<String> allVariableNamesForFilter){
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

    private static void addNewCleaningExpression(CleanedVariableEntry cleanedVariable,
                                          String filterExpression,
                                          String shapeFrom,
                                          boolean isAggregatorUsed){
        cleanedVariable
                .getCleaningExpressions()
                .add(new CleaningExpression(filterExpression, shapeFrom, isAggregatorUsed)
        );
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
        lunaticQuestionnaire.setCleaning(cleaning);
        processQuestionLevelFilter(lunaticQuestionnaire);
    }

    /**
     *
     * @param cleaning, the final cleaning object in lunatic-model to update
     * @param filterExpression, the expression of filter (as string expression)
     * @param allVariablesThatInfluenceFilterExpression, list of all variables that influence the filter
     * @param variablesCollectedInsideFilter, list of all variable that collected inside filter i.e that should be cleaned
     */
    private void processCleaningForFilterExpression(
            CleaningType cleaning,
            String filterExpression,
            List<String> allVariablesThatInfluenceFilterExpression,
            List<String> variablesCollectedInsideFilter){
        boolean isAggregatorUsedOfFilter = isAggregatorUsedInFilter(filterExpression, allVariablesThatInfluenceFilterExpression);
        removeCalculatedVariables(allVariablesThatInfluenceFilterExpression).forEach(
                variableName -> {
                    boolean isExistCleaningVariableEntry = cleaning.getCleaningVariableNames().contains(variableName);
                    if (!isExistCleaningVariableEntry){
                        CleaningVariableEntry cleaningVariableEntry = new CleaningVariableEntry(variableName);
                        variablesCollectedInsideFilter.forEach(variableToClean -> {
                            CleanedVariableEntry cleanedVariableEntry = new CleanedVariableEntry(variableToClean);
                            addNewCleaningExpression(cleanedVariableEntry,
                                    filterExpression,
                                    variableShapeFromIndex.get(variableToClean),
                                    isAggregatorUsedOfFilter);
                            cleaningVariableEntry.addCleanedVariable(cleanedVariableEntry);
                        });
                        cleaning.addCleaningEntry(cleaningVariableEntry);
                    } else {
                        CleaningVariableEntry existingCleaningVariableEntry = cleaning.getCleaningEntry(variableName);
                        variablesCollectedInsideFilter.forEach(variableToClean -> {
                            boolean isExistCleanedVariableEntry = existingCleaningVariableEntry.getCleanedVariableNames().contains(variableToClean);
                            CleanedVariableEntry cleanedVariableEntry = isExistCleanedVariableEntry
                                    ? existingCleaningVariableEntry.getCleanedVariable(variableToClean)
                                    : new CleanedVariableEntry(variableToClean);
                            addNewCleaningExpression(cleanedVariableEntry,
                                    filterExpression,
                                    variableShapeFromIndex.get(variableToClean),
                                    isAggregatorUsedOfFilter);
                            existingCleaningVariableEntry.addCleanedVariable(cleanedVariableEntry);
                        });
                    }
                }
        );
    }

    private void processQuestionLevelFilter(Questionnaire lunaticQuestionnaire){
        CleaningType cleaning = lunaticQuestionnaire.getCleaning();
        filterIndex.forEach((filterId, filter) -> {
            CalculatedExpression filterExpression = filter.getExpression();
            List<String> allVariablesThatInfluenceFilterExpression = getFinalBindingReferencesWithCalculatedVariables(filterExpression);
            List<String> variablesCollectedInsideFilter = getCollectedVariablesInFilter(filter);
            processCleaningForFilterExpression(cleaning,
                    filter.getExpression().getValue(),
                    allVariablesThatInfluenceFilterExpression,
                    variablesCollectedInsideFilter);
        });
    }


    private void processCodeFilters(Questionnaire lunaticQuestionnaire){
        // 1. retrieve all codeFilters
        // 2. retrieve collectedVariable inside
        //   - UniqueChoiceQuestion: only DetailResponse Question variable
        //   - MultipleChoiceQuestion: DetailResponse Question variable and response.name
        // 3. retrieve formula
        // 4. retrieve variables that influencesExpression
        // 5. update existing Cleaning, to add new cleaning entry
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(UniqueChoiceQuestion.class::isInstance)
                .map(UniqueChoiceQuestion.class::cast)
                .filter(uniqueChoiceQuestion -> !uniqueChoiceQuestion.getCodeFilters().isEmpty())
                .forEach(uniqueChoiceQuestion -> processCleaningUniqueChoiceQuestion(lunaticQuestionnaire, uniqueChoiceQuestion));

        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(SimpleMultipleChoiceQuestion.class::isInstance)
                .map(SimpleMultipleChoiceQuestion.class::cast)
                .filter(simpleMultipleChoiceQuestion -> !simpleMultipleChoiceQuestion.getCodeFilters().isEmpty())
                .forEach(multipleChoiceQuestion -> processCleaningMultipleChoiceQuestion(lunaticQuestionnaire, multipleChoiceQuestion));
    }

    List<String> getResponseNameOfCheckboxResponse(ResponseCheckboxGroup responseCheckboxGroup){
        List<String> variableNames = new ArrayList<>();
        if(responseCheckboxGroup.getResponse() != null) variableNames.add(responseCheckboxGroup.getResponse().getName());
        if(responseCheckboxGroup.getDetail() != null) variableNames.add(responseCheckboxGroup.getDetail().getResponse().getName());
        return variableNames;
    }

    List<String> getResponseNameOfCheckboxResponse(Option option){
        List<String> variableNames = new ArrayList<>();
        if(option.getDetail() != null) variableNames.add(option.getDetail().getResponse().getName());
        return variableNames;
    }

    private static boolean isConditionFilterActive(ConditionFilterType conditionFilter){
        if(conditionFilter == null) return false;
        if(conditionFilter.getValue().isEmpty()) return false;
        return !DEFAULT_FILTER_VALUE.equals(conditionFilter.getValue());

    }

    // detail & response
    private void processCleaningMultipleChoiceQuestion(Questionnaire lunaticQuestionnaire, SimpleMultipleChoiceQuestion enoMultipleChoiceQuestion){
        CleaningType cleaning = lunaticQuestionnaire.getCleaning();
        Optional<ComponentType> multipleChoiceQuestion = findComponentById(lunaticQuestionnaire, enoMultipleChoiceQuestion.getId());
        if(multipleChoiceQuestion.isEmpty()){
            throw new MappingException("Cannot find Lunatic component for " + enoMultipleChoiceQuestion + ".");
        }
        if(multipleChoiceQuestion.get() instanceof CheckboxGroup checkboxGroup){
            checkboxGroup.getResponses().forEach(responseCheckboxGroup -> {
                ConditionFilterType conditionFilter = responseCheckboxGroup.getConditionFilter();
                if(isConditionFilterActive(conditionFilter)){
                    List<String> allVariablesThatInfluenceFilterExpression = getFinalBindingReferencesWithCalculatedVariables(conditionFilter);
                    List<String> variablesCollectedInsideFilter = getResponseNameOfCheckboxResponse(responseCheckboxGroup);
                    processCleaningForFilterExpression(
                            cleaning, conditionFilter.getValue(),
                            allVariablesThatInfluenceFilterExpression,
                            variablesCollectedInsideFilter
                    );
                }
            });
        }
    }

    private void processCleaningUniqueChoiceQuestion(Questionnaire lunaticQuestionnaire, UniqueChoiceQuestion enoUniqueChoiceQuestion){
        Optional<ComponentType> uniqueChoiceQuestion = findComponentById(lunaticQuestionnaire, enoUniqueChoiceQuestion.getId());
        if(uniqueChoiceQuestion.isEmpty()){
            throw new MappingException("Cannot find Lunatic component for " + enoUniqueChoiceQuestion + ".");
        }
        if (uniqueChoiceQuestion.get() instanceof Radio radio)
            radio.getOptions().forEach(option -> processCleaningOption(lunaticQuestionnaire, option, radio.getResponse().getName()));
        if (uniqueChoiceQuestion.get() instanceof CheckboxOne checkboxOne)
            checkboxOne.getOptions().forEach(option -> processCleaningOption(lunaticQuestionnaire, option, checkboxOne.getResponse().getName()));
        if (uniqueChoiceQuestion.get() instanceof Dropdown dropdown)
            dropdown.getOptions().forEach(option -> processCleaningOption(lunaticQuestionnaire, option, dropdown.getResponse().getName()));
    }

    private void processCleaningOption(Questionnaire lunaticQuestionnaire, Option option, String uniqueResponseVariableName){
        CleaningType cleaning = lunaticQuestionnaire.getCleaning();
        ConditionFilterType conditionFilter = option.getConditionFilter();
        if(isConditionFilterActive(conditionFilter)){
            List<String> allVariablesThatInfluenceFilterExpression = getFinalBindingReferencesWithCalculatedVariables(conditionFilter);
            List<String> variablesCollectedInsideFilter = getResponseNameOfCheckboxResponse(option);
            processCleaningForFilterExpression(
                    cleaning, conditionFilter.getValue(),
                    allVariablesThatInfluenceFilterExpression,
                    variablesCollectedInsideFilter
            );
            // special step, add cleaning condition of Variable: if condition filter and optionValue is selected i.e variable = optionValue
            // step 1: create new conditionFilter
            ConditionFilterType extraConditionFilter = new ConditionFilterType();
            // example: CITY = "P" where P is codeValue of "Paris"
            String conditionOfCodeSelected = String.format("%s = \"%s\"", uniqueResponseVariableName, option.getValue());
            extraConditionFilter.setValue(joinByANDLogicExpression(conditionFilter.getValue(), conditionOfCodeSelected));
            List<String> allVariablesThatInfluenceExtraFilterExpression = new ArrayList<>(allVariablesThatInfluenceFilterExpression);
            allVariablesThatInfluenceExtraFilterExpression.add(uniqueResponseVariableName);
            extraConditionFilter.setBindingDependencies(allVariablesThatInfluenceExtraFilterExpression);
            extraConditionFilter.setType(LabelTypeEnum.VTL);
            // cleaning variable of this question if codeValue is selected and there is conditionFilter evaluated to true on this option
            processCleaningForFilterExpression(
                    cleaning, conditionFilter.getValue(),
                    allVariablesThatInfluenceExtraFilterExpression,
                    List.of(uniqueResponseVariableName)
            );
        }
    }

    private void processTableCellFilters(){
        // ToDo
    }

}
