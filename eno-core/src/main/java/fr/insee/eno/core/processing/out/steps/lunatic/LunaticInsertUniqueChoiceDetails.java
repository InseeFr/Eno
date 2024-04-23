package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

import java.util.List;
import java.util.Optional;

/**
 * Unique choice modality details are at the question level in Eno model.
 * This processing step is aimed to insert the detail response of unique choice questions at the right place.
 */
public class LunaticInsertUniqueChoiceDetails implements ProcessingStep<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;
    private Questionnaire lunaticQuestionnaire;
    private final LunaticMapper lunaticMapper = new LunaticMapper();

    public LunaticInsertUniqueChoiceDetails(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
    }

    /**
     * Inserts detail responses in options of unique choice components (Radio and CheckboxOne).
     * @param lunaticQuestionnaire Lunatic questionnaire.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(UniqueChoiceQuestion.class::isInstance)
                .map(UniqueChoiceQuestion.class::cast)
                .forEach(this::insertDetailInOptions);
    }

    /**
     * From Eno unique choice question object given, retrieves the corresponding Lunatic component,
     * and insert detail responses in its options.
     * @param enoUniqueChoiceQuestion Eno unique choice question.
     */
    private void insertDetailInOptions(UniqueChoiceQuestion enoUniqueChoiceQuestion) {
        // Find corresponding Lunatic component
        String questionId = enoUniqueChoiceQuestion.getId();
        Optional<ComponentType> lunaticComponent = findComponentById(lunaticQuestionnaire, questionId);
        if (lunaticComponent.isEmpty())
            throw new MappingException("Cannot find Lunatic component for " + enoUniqueChoiceQuestion + ".");
        enoUniqueChoiceQuestion.getDetailResponses().forEach(enoDetailResponse -> {
            // Map Eno detail response object to a Lunatic detail response object
            DetailResponse lunaticDetailResponse = new DetailResponse();
            lunaticMapper.mapEnoObject(enoDetailResponse, lunaticDetailResponse);
            // Insert it in the right option (only Radio and CheckboxOne are concerned)
            boolean mappingError = false;
            if (lunaticComponent.get() instanceof Radio radio)
                mappingError = insertDetailInOption(lunaticDetailResponse, enoDetailResponse.getValue(), radio.getOptions());
            if (lunaticComponent.get() instanceof CheckboxOne checkboxOne)
                mappingError = insertDetailInOption(lunaticDetailResponse, enoDetailResponse.getValue(), checkboxOne.getOptions());
            if (mappingError)
                throw new MappingException(String.format(
                        "Cannot link detail field with an option of value '%s' in Lunatic unique choice component '%s'.",
                        enoDetailResponse.getValue(), questionId));
        });
    }

    /**
     * Inserts the detail response object in the right option, using the value to determine which.
     * This method doesn't have sufficient information to throw an exception with a precise message. So it returns a
     * boolean to indicate if insertion failed.
     * @param detailResponse Lunatic detail response object.
     * @param value Value of the corresponding option.
     * @param optionsList List of Lunatic unique choice options.
     * @return True if insertion failed.
     */
    private static boolean insertDetailInOption(DetailResponse detailResponse, String value, List<Options> optionsList) {
        // Note: 'Options' class name should be singular in Lunatic-Model...
        Optional<Options> correspondingOption = optionsList.stream()
                .filter(options -> value.equals(options.getValue()))
                .findAny();
        if (correspondingOption.isEmpty())
            return true;
        correspondingOption.get().setDetail(detailResponse);
        return false;
    }

    // May be refactored in Lunatic utils at some point,
    // yet it is a bit too specific as it's currently written so better let it private here.
    private static Optional<ComponentType> findComponentById(Questionnaire lunaticQuestionnaire, String id) {
        // Search in questionnaire components
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        Optional<ComponentType> searchedComponent = findComponentInList(id, components);
        if (searchedComponent.isPresent())
            return searchedComponent;
        // If not found, may be in a loop
        List<Loop> loops = lunaticQuestionnaire.getComponents().stream()
                .filter(Loop.class::isInstance)
                .map(Loop.class::cast)
                .toList();
        for (Loop loop : loops) {
            List<ComponentType> loopComponents = loop.getComponents();
            searchedComponent = findComponentInList(id, loopComponents);
            if (searchedComponent.isPresent())
                return searchedComponent;
        }
        // If still not found, return empty
        return Optional.empty();
    }

    private static Optional<ComponentType> findComponentInList(String id, List<ComponentType> loopComponents) {
        return loopComponents.stream()
                .filter(component -> id.equals(component.getId())).findAny();
    }

}
