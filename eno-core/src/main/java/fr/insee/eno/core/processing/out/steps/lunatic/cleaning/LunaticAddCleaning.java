package fr.insee.eno.core.processing.out.steps.lunatic.cleaning;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.cleaning.CleaningType;
import fr.insee.lunatic.model.flat.variable.VariableType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static fr.insee.eno.core.processing.out.steps.lunatic.cleaning.CleaningUtils.getFinalBindingReferencesWithCalculatedVariables;
import static fr.insee.eno.core.processing.out.steps.lunatic.cleaning.CleaningUtils.processCleaningForFilterExpression;
import static fr.insee.eno.core.utils.LunaticUtils.getCollectedVariablesByQuestion;

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
public class LunaticAddCleaning implements ProcessingStep<Questionnaire> {

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
     * Create the 'cleaning' block in the given Lunatic questionnaire. (See class documentation for details.)
     *
     * @param lunaticQuestionnaire A Lunatic questionnaire object.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        preProcessCleaning(lunaticQuestionnaire);
        // classic filter
        processQuestionLevelFilter(lunaticQuestionnaire);
        // filter of code (item of codeList)
        processCodeFiltered(lunaticQuestionnaire);
        // filter in cell level in dynamicTable/rosterForLoop
        processCellsFiltered(lunaticQuestionnaire);
        // special cleaning for Pairwise
        processPairwiseCleaning(lunaticQuestionnaire);
    }

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

    public LunaticAddCleaning(EnoQuestionnaire enoQuestionnaire) {
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

    public void preProcessCleaning(Questionnaire lunaticQuestionnaire){
        lunaticQuestionnaire.setCleaning(new CleaningType());
        this.preProcessVariablesAndShapeFrom(lunaticQuestionnaire);
    }

    private void preProcessVariablesAndShapeFrom(Questionnaire lunaticQuestionnaire){
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
     * @param abstractSequence
     * @return List of variable Name collected inside the scope of sequence or subsequence
     */
    public List<String> getCollectedVarsInSequence(AbstractSequence abstractSequence) {
        List<String> collectedVarInSequence = new ArrayList<>();
        abstractSequence.getSequenceStructure().forEach(itemReference -> {
                    if (StructureItemReference.StructureItemType.QUESTION.equals(itemReference.getType())) {
                        collectedVarInSequence.addAll(variablesByQuestion.get(itemReference.getId()));
                    } else {
                        collectedVarInSequence.addAll(getCollectedVarsInSequence((AbstractSequence) enoIndex.get(itemReference.getId())));
                    }
                }
        );
        return collectedVarInSequence;
    }
    private void processQuestionLevelFilter(Questionnaire lunaticQuestionnaire){
        CleaningType cleaning = lunaticQuestionnaire.getCleaning();
        filterIndex.forEach((filterId, filter) -> {
            CalculatedExpression filterExpression = filter.getExpression();
            List<String> allVariablesThatInfluenceFilterExpression = getFinalBindingReferencesWithCalculatedVariables(filterExpression, variableIndex);
            List<String> variablesCollectedInsideFilter = getCollectedVariablesInFilter(filter);
            processCleaningForFilterExpression(cleaning, variableIndex, variableShapeFromIndex,
                    filter.getExpression().getValue(),
                    allVariablesThatInfluenceFilterExpression,
                    variablesCollectedInsideFilter);
        });
    }


    public void processCodeFiltered(Questionnaire lunaticQuestionnaire){
        LunaticUniqueChoiceQuestionCleaning uniqueChoiceQuestionCleaning = new LunaticUniqueChoiceQuestionCleaning(lunaticQuestionnaire, variableIndex, variableShapeFromIndex);
        LunaticMultipleChoiceQuestionCleaning multipleChoiceQuestionCleaning = new LunaticMultipleChoiceQuestionCleaning(lunaticQuestionnaire, variableIndex, variableShapeFromIndex);
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
                .forEach(uniqueChoiceQuestionCleaning::processCleaningUniqueChoiceQuestion);

        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(SimpleMultipleChoiceQuestion.class::isInstance)
                .map(SimpleMultipleChoiceQuestion.class::cast)
                .filter(simpleMultipleChoiceQuestion -> !simpleMultipleChoiceQuestion.getCodeFilters().isEmpty())
                .forEach(multipleChoiceQuestionCleaning::processCleaningMultipleChoiceQuestion);
    }

    public void processCellsFiltered(Questionnaire lunaticQuestionnaire){
        LunaticDynamicTableQuestionCleaning dynamicTableQuestionCleaning = new LunaticDynamicTableQuestionCleaning(lunaticQuestionnaire, variableIndex, variableShapeFromIndex);
        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(DynamicTableQuestion.class::isInstance)
                .map(DynamicTableQuestion.class::cast)
                .forEach(dynamicTableQuestionCleaning::processCleaningDynamicTableQuestion);
    }

    public void processPairwiseCleaning(Questionnaire lunaticQuestionnaire){
        LunaticPairwiseQuestionCleaning pairwiseQuestionCleaning = new LunaticPairwiseQuestionCleaning(lunaticQuestionnaire, variableIndex, variableShapeFromIndex);
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(PairwiseQuestion.class::isInstance)
                .map(PairwiseQuestion.class::cast)
                .forEach(pairwiseQuestionCleaning::processCleaningPairwiseQuestion);
    }

}
