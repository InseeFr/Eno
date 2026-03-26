package fr.insee.eno.core.processing.out.steps.lunatic.cleaning;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
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
import static fr.insee.eno.core.utils.vtl.VtlSyntaxUtils.*;

@Slf4j
public record LunaticUniqueChoiceQuestionCleaning(Questionnaire lunaticQuestionnaire,
                                                  Map<String, VariableType> variableIndex,
                                                  Map<String, String> variableShapeFromIndex) {

    /**
     * Add a cleaning for filtered option
     */
    public void processCleaningUniqueChoiceQuestionOptionFilters(UniqueChoiceQuestion enoUniqueChoiceQuestion){
        Optional<ComponentType> uniqueChoiceQuestion = findComponentById(lunaticQuestionnaire, enoUniqueChoiceQuestion.getId());
        if (uniqueChoiceQuestion.isEmpty()) {
            throw new MappingException("Cannot find Lunatic component for " + enoUniqueChoiceQuestion + ".");
        }
        if (uniqueChoiceQuestion.get() instanceof Radio radio)
            radio.getOptions().forEach(option -> processCleaningOption(option, radio.getResponse().getName()));
        if (uniqueChoiceQuestion.get() instanceof CheckboxOne checkboxOne)
            checkboxOne.getOptions().forEach(option -> processCleaningOption(option, checkboxOne.getResponse().getName()));
        if (uniqueChoiceQuestion.get() instanceof Dropdown dropdown)
            dropdown.getOptions().forEach(option -> processCleaningOption(option, dropdown.getResponse().getName()));
    }

    /**
     * Add a cleaning for filtered option, case of bodyCell (UCQ inside Table or RosterForLoop)
     */
    public void processCleaningUniqueChoiceQuestionOptionFiltersBodyCell(BodyCell bodyCell){
        if(bodyCell.getOptions() == null) return;
        bodyCell.getOptions().forEach(option -> processCleaningOption(option, bodyCell.getResponse().getName()));
    }


    /**
     * Add a cleaning for dynamic option (option based on variable in Loop)
     */
    public void processCleaningUniqueChoiceQuestionDynamicOption(UniqueChoiceQuestion enoUniqueChoiceQuestion) {
        String optionSource = enoUniqueChoiceQuestion.getOptionSource();
        if (optionSource == null) return;
        String responseName = enoUniqueChoiceQuestion.getResponse().getVariableName();
        processCleaningOptionSource(responseName, optionSource);
    }

    /**
     * Add a cleaning for dynamic option (option based on variable in Loop), case of bodyCell (UCQ inside Table or RosterForLoop)
     */
    public void processCleaningDynamicOptionBodyCell(BodyCell bodyCell){
        String optionSourceVariable = bodyCell.getOptionSource();
        if(optionSourceVariable == null) return;
        processCleaningOptionSource(bodyCell.getResponse().getName(), optionSourceVariable);
    }

    /**
     * Add a cleaning for clarification question that are displayed only when a specific option is checked.
     */
    public void processCleaningUniqueChoiceQuestionClarification(UniqueChoiceQuestion enoUniqueChoiceQuestion) {
        enoUniqueChoiceQuestion.getDetailResponses().forEach(detailResponse -> {
            String responseVariable = enoUniqueChoiceQuestion.getResponse().getVariableName();
            String clarificationVariable = detailResponse.getResponse().getVariableName();
            String clarificationValue = detailResponse.getValue();

            processCleaningForFilterExpression(lunaticQuestionnaire.getCleaning(), variableIndex, variableShapeFromIndex,
                    expressionEqualToOther(responseVariable, surroundByDoubleQuotes(clarificationValue)),
                    List.of(responseVariable),
                    List.of(clarificationVariable));
        });
    }


    private Optional<String> getResponseNameOfDetailResponse(Option option) {
        if (option.getDetail() != null) return Optional.of(option.getDetail().getResponse().getName());
        return Optional.empty();
    }

    private void processCleaningOption(Option option, String uniqueResponseVariableName) {
        CleaningType cleaning = lunaticQuestionnaire.getCleaning();
        ConditionFilterType conditionFilter = option.getConditionFilter();
        if (isConditionFilterActive(conditionFilter)) {
            Optional<String> detailResponseNameOfOption = getResponseNameOfDetailResponse(option);
            // cleaning detail response
            detailResponseNameOfOption.ifPresent(detailResponse -> processCleaningForFilterExpression(
                    cleaning, variableIndex, variableShapeFromIndex,
                    conditionFilter.getValue(),
                    getFinalBindingReferencesWithCalculatedVariables(conditionFilter, variableIndex),
                    List.of(detailResponse)
            ));
            // cleaning only if value is selected and filtered
            ConditionFilterType extraCondition = buildExpressionForCleaningQCU(option, uniqueResponseVariableName);
            processCleaningForFilterExpression(cleaning, variableIndex, variableShapeFromIndex,
                    extraCondition.getValue(),
                    getFinalBindingReferencesWithCalculatedVariables(extraCondition, variableIndex),
                    List.of(uniqueResponseVariableName)
            );
        }
    }

    private ConditionFilterType buildExpressionForCleaningQCU(Option option, String variableName) {
        // special step, add cleaning condition of Variable:
        // We have to clean if the variable if the codeValue is filtered and this codeValue is selected
        // The new condition is ( conditionFilter OR optionValue is not selected  i.e variable <> optionValue)
        // why ? expression of cleaning: if at least one is false -> should clean variable (brain fuck)
        ConditionFilterType extraConditionFilter = new ConditionFilterType();
        extraConditionFilter.setType(LabelTypeEnum.VTL);
        // build new condition
        String conditionOfCodeNotSelected = expressionNotEqualToOther(variableName, surroundByDoubleQuotes(option.getValue()));
        extraConditionFilter.setValue(joinByORLogicExpression(option.getConditionFilter().getValue(), conditionOfCodeNotSelected));
        // update bindingsDeps
        List<String> allVariablesThatInfluenceExpression = new ArrayList<>(getFinalBindingReferencesWithCalculatedVariables(option.getConditionFilter(), variableIndex));
        allVariablesThatInfluenceExpression.add(variableName);
        extraConditionFilter.setBindingDependencies(allVariablesThatInfluenceExpression);
        return extraConditionFilter;
    }

    private void processCleaningOptionSource(String responseVariableName, String optionSourceVariable) {
        CleaningType cleaning = lunaticQuestionnaire.getCleaning();

        processCleaningForFilterExpression(
                cleaning,
                variableIndex,
                variableShapeFromIndex,
                "false",
                List.of(optionSourceVariable),
                List.of(responseVariableName),
                true
        );
    }

}
