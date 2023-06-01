package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.treatments.dto.EnoSuggesterType;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Processing for suggesters
 */
@Slf4j
public class LunaticSuggesterProcessing implements OutProcessingInterface<Questionnaire> {

    private final List<EnoSuggesterType> enoSuggesters;

    /**
     * Initialize eno suggesters from input
     * @param suggesters suggester list
     */
    public LunaticSuggesterProcessing(List<EnoSuggesterType> suggesters) {
        this.enoSuggesters = suggesters;
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        log.info("Processing suggesters on Lunatic Questionnaire");
        List<SuggesterType> suggesters = EnoSuggesterType.toLunaticModelList(enoSuggesters);

        // set suggesters to lunatic model
        lunaticQuestionnaire.getSuggesters().addAll(suggesters);

        // change corresponding components type to suggester type
        enoSuggesters.forEach(enoSuggester ->
            lunaticQuestionnaire.getComponents().stream()
                    .filter(component -> shouldApplySuggester(component, enoSuggester))
                    .forEach(component -> {
                        component.setComponentType(ComponentTypeEnum.SUGGESTER);
                        component.setStoreName(enoSuggester.getName());
                    }));
    }

    /**
     * check if suggester can be applied to specific component.
     * @param component component to check
     * @param suggester suggester to apply
     */
    private boolean shouldApplySuggester(ComponentType component, EnoSuggesterType suggester) {
        Optional<String> responseName = LunaticPostProcessingUtils.getResponseName(component);

        if(responseName.isEmpty()) {
            return false;
        }

        return suggester.getResponseNames().contains(responseName.get());
    }
}