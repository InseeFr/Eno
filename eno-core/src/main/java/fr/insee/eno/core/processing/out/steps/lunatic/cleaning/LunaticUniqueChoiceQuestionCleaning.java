package fr.insee.eno.core.processing.out.steps.lunatic.cleaning;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.cleaning.CleaningType;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fr.insee.eno.core.processing.out.steps.lunatic.cleaning.LunaticAddCleaning.*;
import static fr.insee.eno.core.utils.LunaticUtils.findComponentById;
import static fr.insee.eno.core.utils.vtl.VtlSyntaxUtils.*;

@Slf4j
public class LunaticUniqueChoiceQuestionCleaning {

    private final Questionnaire lunaticQuestionnaire;
    private final Map<String, String> variableShapeFromIndex;

    public LunaticUniqueChoiceQuestionCleaning(Questionnaire lunaticQuestionnaire, Map<String, String> variableShapeFromIndex){
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        this.variableShapeFromIndex = variableShapeFromIndex;
    }

    public void processCleaningUniqueChoiceQuestion(UniqueChoiceQuestion enoUniqueChoiceQuestion){
        Optional<ComponentType> uniqueChoiceQuestion = findComponentById(lunaticQuestionnaire, enoUniqueChoiceQuestion.getId());
        if(uniqueChoiceQuestion.isEmpty()){
            throw new MappingException("Cannot find Lunatic component for " + enoUniqueChoiceQuestion + ".");
        }
        if (uniqueChoiceQuestion.get() instanceof Radio radio)
            radio.getOptions().forEach(option -> processCleaningOption(option, radio.getResponse().getName()));
        if (uniqueChoiceQuestion.get() instanceof CheckboxOne checkboxOne)
            checkboxOne.getOptions().forEach(option -> processCleaningOption(option, checkboxOne.getResponse().getName()));
        if (uniqueChoiceQuestion.get() instanceof Dropdown dropdown)
            dropdown.getOptions().forEach(option -> processCleaningOption(option, dropdown.getResponse().getName()));
    }



    private Optional<String> getResponseNameOfDetailResponse(Option option){
        if(option.getDetail() != null) option.getDetail().getResponse().getName();
        return Optional.empty();
    }

    private void processCleaningOption(Option option, String uniqueResponseVariableName){
        CleaningType cleaning = lunaticQuestionnaire.getCleaning();
        ConditionFilterType conditionFilter = option.getConditionFilter();
        if(isConditionFilterActive(conditionFilter)){
            Optional<String> detailResponseNameOfOption = getResponseNameOfDetailResponse(option);
            // cleaning detail response
            detailResponseNameOfOption.ifPresent(detailResponse -> processCleaningForFilterExpression(
                    cleaning, variableShapeFromIndex,
                    conditionFilter.getValue(),
                    getFinalBindingReferencesWithCalculatedVariables(conditionFilter),
                    List.of(detailResponse)
            ));
            // cleaning only if value is selected and filtered
            ConditionFilterType extraCondition = buildExpressionForCleaningQCU(option, uniqueResponseVariableName);
            processCleaningForFilterExpression(cleaning, variableShapeFromIndex,
                    extraCondition.getValue(),
                    getFinalBindingReferencesWithCalculatedVariables(extraCondition),
                    List.of(uniqueResponseVariableName)
            );
        }
    }

    private ConditionFilterType buildExpressionForCleaningQCU(Option option, String variableName){
        // special step, add cleaning condition of Variable:
        // We have to clean if the variable if the codeValue is filtered and this coodeValue is selected
        // The new condition is ( conditionFilter OR optionValue is not selected  i.e variable <> optionValue)
        // why ? expression of cleaning: if at least one is false -> should clean variable (brain fuck)
        ConditionFilterType extraConditionFilter = new ConditionFilterType();
        extraConditionFilter.setType(LabelTypeEnum.VTL);
        // build new condition
        String conditionOfCodeNotSelected = expressionNotEqualToOther(variableName, surroundByDoubleQuotes(option.getValue()));
        extraConditionFilter.setValue(joinByORLogicExpression(option.getConditionFilter().getValue(), conditionOfCodeNotSelected));
        // update bindingsDeps
        List<String> allVariablesThatInfluenceExpression = new ArrayList<>(getFinalBindingReferencesWithCalculatedVariables(option.getConditionFilter()));
        allVariablesThatInfluenceExpression.add(variableName);
        extraConditionFilter.setBindingDependencies(allVariablesThatInfluenceExpression);
        return extraConditionFilter;
    }

}
