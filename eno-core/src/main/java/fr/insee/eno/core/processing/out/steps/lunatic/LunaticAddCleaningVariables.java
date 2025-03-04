package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.cleaning.CleanedVariableEntry;
import fr.insee.lunatic.model.flat.cleaning.CleaningType;
import fr.insee.lunatic.model.flat.cleaning.CleaningVariableEntry;
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
    private Map<String, Filter> filterIndex = new LinkedHashMap<>();
    private Map<String, Variable> variableIndex = new LinkedHashMap<>();
    private Map<String, List<String>> variablesByQuestion;

    public static final Comparator<Filter> filterComparator = (filter1, filter2) -> {
        boolean isParentOfFilter2 = filter1.getFilterItems().stream().map(i->i.getId()).toList().contains(filter2.getId());
        boolean isChildOfFilter2 = filter2.getFilterItems().stream().map(i->i.getId()).toList().contains(filter1.getId());
        if(isParentOfFilter2) return -1;
        if(isChildOfFilter2) return 1;
        return 0;
    };


    public LunaticAddCleaningVariables(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        this.enoIndex = enoQuestionnaire.getIndex();
        enoQuestionnaire.getVariables().forEach(v -> variableIndex.put(v.getName(), v));
        List<Filter> sortedFilters = new ArrayList<>(enoQuestionnaire.getFilters());
        Collections.sort(sortedFilters, filterComparator);
        sortedFilters.forEach(f -> filterIndex.put(f.getId(), f));
    }

    public void preProcessVariables(Questionnaire lunaticQuestionnaire){
        variablesByQuestion = getCollectedVariablesByQuestion(lunaticQuestionnaire);
    }

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

    public List<String> getFinalBindingReferences(CalculatedExpression expression) {
        return expression.getBindingReferences().stream()
                .map(b -> b.getVariableName())
                .map(vName -> variableIndex.get(vName))
                .map(variable -> {
                    if (!(variable instanceof CalculatedVariable)) return List.of(variable.getName());
                    return ((CalculatedVariable) variable).getLunaticBindingDependencies();
                })
                .flatMap(Collection::stream)
                .filter(vName -> {
                    Variable variable = variableIndex.get(vName);
                    return !(variable instanceof CalculatedVariable);
                })
                .toList();
    }



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
                    questionCollectedVarIndex.put(questionId, collectedVars);
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
        preProcessVariables(lunaticQuestionnaire);
        CleaningType cleaning = new CleaningType();
        filterIndex.forEach((filterId, filter) -> {
            CalculatedExpression filterExpression = filter.getExpression();
            List<String> variablesWhichInfluenceFilterExpression = getFinalBindingReferences(filterExpression);
            List<String> variablesCollectedInsideFilter = getCollectedVariablesInFilter(filter);
            variablesWhichInfluenceFilterExpression.stream().forEach(
                    variableName -> {
                        boolean isExistCleaningVariableEntry = cleaning.getCleaningVariableNames().contains(variableName);
                        if (!isExistCleaningVariableEntry){
                            CleaningVariableEntry cleaningVariableEntry = new CleaningVariableEntry(variableName);
                            variablesCollectedInsideFilter.forEach(variableToClean -> {
                                CleanedVariableEntry cleanedVariableEntry = new CleanedVariableEntry(variableToClean);
                                cleanedVariableEntry.getFilterExpressions().add(filterExpression.getValue());
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
                                    cleanedVariableEntry.getFilterExpressions().add(filterExpression.getValue());
                                    cleaning.getCleaningEntry(variableName).addCleanedVariable(cleanedVariableEntry);
                                } else {
                                    CleanedVariableEntry existingCleanedVariableEntry = existingCleaningVariableEntry.getCleanedVariable(variableToClean);
                                    existingCleanedVariableEntry.getFilterExpressions().add(filter.getExpression().getValue());
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
