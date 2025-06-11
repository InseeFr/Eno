package fr.insee.eno.core.processing.out.steps.lunatic.cleaning;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.utils.vtl.VtlSyntaxUtils;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.cleaning.CleaningType;
import fr.insee.lunatic.model.flat.variable.VariableType;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fr.insee.eno.core.processing.out.steps.lunatic.cleaning.CleaningUtils.getFinalBindingReferencesWithCalculatedVariables;
import static fr.insee.eno.core.processing.out.steps.lunatic.cleaning.CleaningUtils.processCleaningForFilterExpression;
import static fr.insee.eno.core.utils.LunaticUtils.findComponentById;
import static fr.insee.eno.core.utils.LunaticUtils.isConditionFilterActive;

@Slf4j
public class LunaticMultipleChoiceQuestionCleaning {

    private final Questionnaire lunaticQuestionnaire;
    private final Map<String, VariableType> variableIndex;
    private final Map<String, String> variableShapeFromIndex;

    public LunaticMultipleChoiceQuestionCleaning(Questionnaire lunaticQuestionnaire,
                                                 Map<String, VariableType> variableIndex,
                                                 Map<String, String> variableShapeFromIndex){
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        this.variableIndex = variableIndex;
        this.variableShapeFromIndex = variableShapeFromIndex;
    }

    /** Get the variable names associated to the option (response and clarification). */
    private static List<String> getResponseNamesOfCheckboxResponse(ResponseCheckboxGroup responseCheckboxGroup){
        List<String> variableNames = new ArrayList<>();
        if(responseCheckboxGroup.getResponse() != null) variableNames.add(responseCheckboxGroup.getResponse().getName());
        if(responseCheckboxGroup.getDetail() != null) variableNames.add(responseCheckboxGroup.getDetail().getResponse().getName());
        return variableNames;
    }

    /** Add a cleaning for responses for which a condition filter exists. */
    public void processCleaningMultipleChoiceQuestion(SimpleMultipleChoiceQuestion enoMultipleChoiceQuestion){
        CleaningType cleaning = lunaticQuestionnaire.getCleaning();
        Optional<ComponentType> multipleChoiceQuestion = findComponentById(lunaticQuestionnaire, enoMultipleChoiceQuestion.getId());
        if(multipleChoiceQuestion.isEmpty()){
            throw new MappingException("Cannot find Lunatic component for " + enoMultipleChoiceQuestion + ".");
        }
        if(multipleChoiceQuestion.get() instanceof CheckboxGroup checkboxGroup){
            checkboxGroup.getResponses().forEach(responseCheckboxGroup -> {
                ConditionFilterType conditionFilter = responseCheckboxGroup.getConditionFilter();
                if(isConditionFilterActive(conditionFilter)){
                    List<String> allVariablesThatInfluenceFilterExpression = getFinalBindingReferencesWithCalculatedVariables(conditionFilter, variableIndex);
                    List<String> variablesCollectedInsideFilter = getResponseNamesOfCheckboxResponse(responseCheckboxGroup);
                    processCleaningForFilterExpression(cleaning, variableIndex, variableShapeFromIndex,
                            conditionFilter.getValue(),
                            allVariablesThatInfluenceFilterExpression,
                            variablesCollectedInsideFilter
                    );
                }
            });
        }
    }

    /** Add a cleaning for clarification question that are displayed only when a specific option is checked. */
    public void processCleaningMultipleChoiceQuestionClarification(SimpleMultipleChoiceQuestion enoMultipleChoiceQuestion){
        CleaningType cleaning = lunaticQuestionnaire.getCleaning();
        Optional<ComponentType> lunaticComponent = findComponentById(lunaticQuestionnaire, enoMultipleChoiceQuestion.getId());
        if(lunaticComponent.isEmpty()){
            throw new MappingException("Cannot find Lunatic component for " + enoMultipleChoiceQuestion + ".");
        }
        if(! (lunaticComponent.get() instanceof CheckboxGroup checkboxGroup)) {
            throw new MappingException("Lunatic component " + lunaticComponent.get() + " is not a checkbox group.");
        }
        checkboxGroup.getResponses().forEach(responseCheckboxGroup -> {
            DetailResponse detailResponse = responseCheckboxGroup.getDetail();
            if (detailResponse == null)
                return;
            String clarificationVariable = detailResponse.getResponse().getName();
            String responseVariable = responseCheckboxGroup.getResponse().getName();
            processCleaningForFilterExpression(cleaning, variableIndex, variableShapeFromIndex,
                VtlSyntaxUtils.nvlDefaultValue(responseVariable, "false"),
                List.of(responseVariable),
                List.of(clarificationVariable));
        });
    }

}
