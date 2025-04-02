package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.model.response.CodeFilter;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fr.insee.eno.core.model.calculated.CalculatedExpression.removeSurroundingDollarSigns;

public class LunaticInsertCodeFilters implements ProcessingStep<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;
    private Questionnaire lunaticQuestionnaire;
    private final Map<String, CodeList> codeListIndex = new HashMap<>();

    public LunaticInsertCodeFilters(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        enoQuestionnaire.getCodeLists().forEach( codeList -> codeListIndex.put(codeList.getId(), codeList));

    }

    /**
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
     *
     * @param codeFilter
     * @param optionsList
     * @return
     */
    private static boolean insertCodeFilterInOption(CodeFilter codeFilter, List<Option> optionsList) {
        // Note: 'Options' class name should be singular in Lunatic-Model...
        Optional<Option> correspondingOption = optionsList.stream()
                .filter(option -> codeFilter.getCodeValue().equals(option.getValue()))
                .findAny();
        if (correspondingOption.isEmpty())
            return true;
        ConditionFilterType conditionFilter = new ConditionFilterType();
        conditionFilter.setValue(codeFilter.getConditionFilter());
        conditionFilter.setType(LabelTypeEnum.VTL);
        correspondingOption.get().setConditionFilter(conditionFilter);
        correspondingOption.get().getConditionFilter().setValue(removeSurroundingDollarSigns(conditionFilter.getValue()));
        return false;
    }

    /**
     * This method is usefully to find position of codeValue in codeList.
     * Whe need this position because in LunaticModel, there is not codeValue in ResponseList
     * We assume that codeList remain in same order all along generation.
     * We use this position to add the corresponding conditionFilter in the response of responseList
     * @param simpleMultipleChoiceQuestion
     * @param codeValue
     * @return always position of codeValue in corresponding codeList, null if codeValue doesn't exist in codeList (should not happen),
     */
    private Integer findIndexOfCodeValue(SimpleMultipleChoiceQuestion simpleMultipleChoiceQuestion, String codeValue){
        CodeList codeListOfQuestion = codeListIndex.get(simpleMultipleChoiceQuestion.getCodeListReference());
        for(int position=0; position < codeListOfQuestion.getCodeItems().size(); position++){
            if(codeValue.equals(codeListOfQuestion.getCodeItems().get(position).getValue())) return position;
        }
        return null;
    }

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

    private static boolean insertCodeFilterInResponse(String conditionFilterOfCodeFilter,
                                                      List<ResponseCheckboxGroup> responseCheckboxGroupList,
                                                      Integer indexOfCodeValueInCodeList) {
        // Note: 'Options' class name should be singular in Lunatic-Model...
        if(indexOfCodeValueInCodeList >= responseCheckboxGroupList.size()) return true;
        ResponseCheckboxGroup correspondingResponseCheckboxGroup = responseCheckboxGroupList.get(indexOfCodeValueInCodeList);
        ConditionFilterType conditionFilter = new ConditionFilterType();
        conditionFilter.setValue(conditionFilterOfCodeFilter);
        conditionFilter.setType(LabelTypeEnum.VTL);
        correspondingResponseCheckboxGroup.setConditionFilter(conditionFilter);
        correspondingResponseCheckboxGroup.getConditionFilter().setValue(removeSurroundingDollarSigns(conditionFilter.getValue()));
        return false;
    }

    // May be refactored in Lunatic utils at some point
    private static Optional<ComponentType> findComponentById(Questionnaire lunaticQuestionnaire, String id) {
        // Search in questionnaire components
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        Optional<ComponentType> searchedComponent = findComponentInList(id, components);
        if (searchedComponent.isPresent())
            return searchedComponent;
        // If not found, may be in a nesting component (such as loop, roundabout, pairwise)
        return lunaticQuestionnaire.getComponents().stream()
                .filter(ComponentNestingType.class::isInstance)
                .map(ComponentNestingType.class::cast)
                .map(ComponentNestingType::getComponents)
                .map(componentList -> findComponentInList(id, componentList))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
    }

    private static Optional<ComponentType> findComponentInList(String id, List<ComponentType> componentList) {
        return componentList.stream().filter(component -> id.equals(component.getId())).findAny();
    }
}
