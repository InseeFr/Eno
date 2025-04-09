package fr.insee.eno.core.processing.out.steps.lunatic.cleaning;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.cleaning.CleaningType;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static fr.insee.eno.core.processing.out.steps.lunatic.cleaning.LunaticAddCleaning.*;
import static fr.insee.eno.core.utils.LunaticUtils.findComponentById;

@Slf4j
public class LunaticMultipleChoiceQuestionCleaning {

    private final Questionnaire lunaticQuestionnaire;
    private final Map<String, String> variableShapeFromIndex;

    public LunaticMultipleChoiceQuestionCleaning(Questionnaire lunaticQuestionnaire, Map<String, String> variableShapeFromIndex){
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        this.variableShapeFromIndex = variableShapeFromIndex;
    }

    private static List<String> getResponseNamesOfCheckboxResponse(ResponseCheckboxGroup responseCheckboxGroup){
        List<String> variableNames = new ArrayList<>();
        if(responseCheckboxGroup.getResponse() != null) variableNames.add(responseCheckboxGroup.getResponse().getName());
        if(responseCheckboxGroup.getDetail() != null) variableNames.add(responseCheckboxGroup.getDetail().getResponse().getName());
        return variableNames;
    }

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
                    List<String> allVariablesThatInfluenceFilterExpression = getFinalBindingReferencesWithCalculatedVariables(conditionFilter);
                    List<String> variablesCollectedInsideFilter = getResponseNamesOfCheckboxResponse(responseCheckboxGroup);
                    processCleaningForFilterExpression(cleaning, variableShapeFromIndex,
                            conditionFilter.getValue(),
                            allVariablesThatInfluenceFilterExpression,
                            variablesCollectedInsideFilter
                    );
                }
            });
        }
    }
}
