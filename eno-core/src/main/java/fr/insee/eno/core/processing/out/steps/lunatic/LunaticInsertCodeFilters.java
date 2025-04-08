package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.model.response.CodeFilter;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoVariableIndex;
import fr.insee.eno.core.reference.VariableIndex;
import fr.insee.lunatic.model.flat.*;

import java.util.*;

import static fr.insee.eno.core.model.calculated.CalculatedExpression.extractBindingReferences;
import static fr.insee.eno.core.model.calculated.CalculatedExpression.removeSurroundingDollarSigns;
import static fr.insee.eno.core.utils.LunaticUtils.findComponentById;

/**
 * The filters (for the modalities of QCU and QCM) are at the question level in the Eno model.
 * This processing step aims to insert these filters in the right place.
 */
public class LunaticInsertCodeFilters implements ProcessingStep<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;
    private Questionnaire lunaticQuestionnaire;
    private final Map<String, CodeList> codeListIndex = new HashMap<>();
    private final VariableIndex variableIndex;

    public LunaticInsertCodeFilters(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        EnoVariableIndex enoVariableIndex = new EnoVariableIndex();
        enoVariableIndex.indexVariables(enoQuestionnaire);
        this.variableIndex = enoVariableIndex;
        enoQuestionnaire.getCodeLists().forEach( codeList -> codeListIndex.put(codeList.getId(), codeList));
    }

    /**
     * Inserts filters into the options of unique-choice components (Radio, CheckboxOne, and Dropdown).
     * Inserts filters into the responses of multiple-choice components (CheckboxGroup).
     * @param lunaticQuestionnaire Lunatic questionnaire.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(UniqueChoiceQuestion.class::isInstance)
                .map(UniqueChoiceQuestion.class::cast)
                .filter(uniqueChoiceQuestion -> !uniqueChoiceQuestion.getCodeFilters().isEmpty())
                .forEach(this::insertFilterInOptions);

        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(SimpleMultipleChoiceQuestion.class::isInstance)
                .map(SimpleMultipleChoiceQuestion.class::cast)
                .filter(simpleMultipleChoiceQuestion -> !simpleMultipleChoiceQuestion.getCodeFilters().isEmpty())
                .forEach(this::insertFilterInCheckbox);
    }

    /**
     * From the given Eno unique-choice question object, retrieves the corresponding Lunatic component,
     * and inserts code filters into its options.
     * @param enoUniqueChoiceQuestion Eno unique-choice question.
     */
    private void insertFilterInOptions(UniqueChoiceQuestion enoUniqueChoiceQuestion) {
        // Find corresponding Lunatic component
        String questionId = enoUniqueChoiceQuestion.getId();
        Optional<ComponentType> lunaticComponent = findComponentById(lunaticQuestionnaire, questionId);
        if (lunaticComponent.isEmpty())
            throw new MappingException("Cannot find Lunatic component for " + enoUniqueChoiceQuestion + ".");
        enoUniqueChoiceQuestion.getCodeFilters().forEach(enoCodeFilter -> {

            // Insert it in the right option (only Radio, CheckboxOne and Dropdown are concerned)
            boolean mappingError = false;
            if (lunaticComponent.get() instanceof Radio radio)
                mappingError = insertCodeFilterInOption(enoCodeFilter, radio.getOptions());
            if (lunaticComponent.get() instanceof CheckboxOne checkboxOne)
                mappingError = insertCodeFilterInOption(enoCodeFilter, checkboxOne.getOptions());
            if (lunaticComponent.get() instanceof Dropdown dropdown)
                mappingError = insertCodeFilterInOption(enoCodeFilter, dropdown.getOptions());
            if (mappingError)
                throw new MappingException(String.format(
                        "Cannot link detail field with an option of value '%s' in Lunatic unique choice component '%s'.",
                        enoCodeFilter.getCodeValue(), questionId));
        });
    }

    /**
     * Inserts the code filter object into the correct option, using its value to determine the target.
     * This method does not have enough information to throw an exception with a precise message,
     * so it returns a boolean to indicate whether the insertion failed.
     * @param codeFilter Lunatic code filter object.
     * @param optionsList List of Lunatic unique-choice options.
     * @return True if the insertion failed.
     */
    private boolean insertCodeFilterInOption(CodeFilter codeFilter, List<Option> optionsList) {
        // Note: 'Options' class name should be singular in Lunatic-Model...
        Optional<Option> correspondingOption = optionsList.stream()
                .filter(option -> codeFilter.getCodeValue().equals(option.getValue()))
                .findAny();
        if (correspondingOption.isEmpty())
            return true;
        ConditionFilterType conditionFilter = new ConditionFilterType();
        conditionFilter.setValue(removeSurroundingDollarSigns(codeFilter.getConditionFilter()));
        Set<BindingReference> binds = extractBindingReferences(conditionFilter.getValue(),variableIndex);
        conditionFilter.setBindingDependencies(binds.stream().map(BindingReference::getVariableName).toList());
        conditionFilter.setType(LabelTypeEnum.VTL);
        correspondingOption.get().setConditionFilter(conditionFilter);
        return false;
    }

    /**
     * This method is useful for finding the position of a codeValue in the codeList.
     * We need this position because, in the LunaticModel, there is no codeValue in the ResponseList.
     * We assume that the codeList remains in the same order throughout the generation process.
     * We use this position to add the corresponding conditionFilter to the response in the ResponseList.
     * @param simpleMultipleChoiceQuestion Eno multiple choices question.
     * @param codeValue Code value of the modality in the list.
     * @return Always returns the position of the codeValue in the corresponding codeList,
     * or null if the codeValue does not exist in the codeList (this should not happen).
     */
    private Integer findIndexOfCodeValue(SimpleMultipleChoiceQuestion simpleMultipleChoiceQuestion, String codeValue){
        CodeList codeListOfQuestion = codeListIndex.get(simpleMultipleChoiceQuestion.getCodeListReference());
        for(int position=0; position < codeListOfQuestion.getCodeItems().size(); position++){
            if(codeValue.equals(codeListOfQuestion.getCodeItems().get(position).getValue())) return position;
        }
        return null;
    }

    /**
     * From the given Eno multiple-choice question object, retrieves the corresponding Lunatic component
     * and inserts code filters into its responses.
     * @param enoSimpleMultipleChoiceQuestion Eno multiple-choice question.
     */
    private void insertFilterInCheckbox(SimpleMultipleChoiceQuestion enoSimpleMultipleChoiceQuestion) {
        // Find corresponding Lunatic component
        String questionId = enoSimpleMultipleChoiceQuestion.getId();
        Optional<ComponentType> lunaticComponent = findComponentById(lunaticQuestionnaire, questionId);
        if (lunaticComponent.isEmpty())
            throw new MappingException("Cannot find Lunatic component for " + enoSimpleMultipleChoiceQuestion + ".");
        enoSimpleMultipleChoiceQuestion.getCodeFilters().forEach(enoCodeFilter -> {
            // Insert it in the right response (only CheckboxGroup ie concerned)
            boolean mappingError = false;
            Integer positionOfCodeValueInCodeList = findIndexOfCodeValue(enoSimpleMultipleChoiceQuestion, enoCodeFilter.getCodeValue());
            if (lunaticComponent.get() instanceof CheckboxGroup checkboxGroup)
                mappingError = insertCodeFilterInResponse(enoCodeFilter.getConditionFilter(), checkboxGroup.getResponses(), positionOfCodeValueInCodeList);
            if (mappingError)
                throw new MappingException(String.format(
                        "Cannot link detail field with an option of value '%s' in Lunatic unique choice component '%s'.",
                        enoCodeFilter.getCodeValue(), questionId));
        });
    }

    /**
     * Inserts a code filter into the corresponding response of a CheckboxGroup, based on the provided index.
     * The filter is applied to the response at the specified index in the responseCheckboxGroupList.
     * The filter value is sanitized by removing surrounding dollar signs before being set.
     *
     * @param conditionFilterOfCodeFilter The condition filter value to be applied.
     * @param responseCheckboxGroupList The list of ResponseCheckboxGroup objects to which the filter will be applied.
     * @param indexOfCodeValueInCodeList The index in the code list that determines which response to apply the filter to.
     * @return {@code true} if the index is out of bounds (greater than or equal to the size of responseCheckboxGroupList);
     *         {@code false} otherwise (filter successfully inserted).
     */
    private boolean insertCodeFilterInResponse(String conditionFilterOfCodeFilter,
                                                      List<ResponseCheckboxGroup> responseCheckboxGroupList,
                                                      Integer indexOfCodeValueInCodeList) {
        // Note: 'Options' class name should be singular in Lunatic-Model...
        if(indexOfCodeValueInCodeList >= responseCheckboxGroupList.size()) return true;
        ResponseCheckboxGroup correspondingResponseCheckboxGroup = responseCheckboxGroupList.get(indexOfCodeValueInCodeList);
        ConditionFilterType conditionFilter = new ConditionFilterType();
        conditionFilter.setValue(removeSurroundingDollarSigns(conditionFilterOfCodeFilter));
        Set<BindingReference> binds = extractBindingReferences(conditionFilter.getValue(), variableIndex);
        conditionFilter.setBindingDependencies(binds.stream().map(BindingReference::getVariableName).toList());
        conditionFilter.setType(LabelTypeEnum.VTL);
        correspondingResponseCheckboxGroup.setConditionFilter(conditionFilter);
        return false;
    }
}
