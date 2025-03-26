package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.model.response.CodeFilter;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

import java.util.List;
import java.util.Optional;

public class LunaticInsertCodeFilters implements ProcessingStep<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;
    private Questionnaire lunaticQuestionnaire;

    public LunaticInsertCodeFilters(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
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

            // Insert it in the right option (only Radio and CheckboxOne are concerned)
            boolean mappingError = false;
            if (lunaticComponent.get() instanceof Radio radio)
                mappingError = insertCodeFilterInOption(enoCodeFilter, radio.getOptions());
            if (lunaticComponent.get() instanceof CheckboxOne checkboxOne)
                mappingError = insertCodeFilterInOption(enoCodeFilter, checkboxOne.getOptions());
            if (mappingError)
                throw new MappingException(String.format(
                        "Cannot link detail field with an option of value '%s' in Lunatic unique choice component '%s'.",
                        enoCodeFilter.getCodeValue(), questionId));
        });
    }

    /**
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
