package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.treatments.dto.EnoSuggesterType;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
        transformComponentsToSuggesters(lunaticQuestionnaire.getComponents());
    }

    /**
     * recursive method which permits to transforms components in suggesters if needed
     * @param components component list
     */
    private void transformComponentsToSuggesters(List<ComponentType> components) {
        enoSuggesters.forEach(enoSuggester -> {
            components.stream()
                    .filter(component -> shouldApplySuggester(component, enoSuggester))
                    .forEach(component -> {
                        component.setComponentType(ComponentTypeEnum.SUGGESTER);
                        component.setStoreName(enoSuggester.getName());
                    });

            components.stream()
                    .filter(component -> component.getComponentType().equals(ComponentTypeEnum.LOOP))
                    .map(Loop.class::cast)
                    .forEach(loop -> transformComponentsToSuggesters(loop.getComponents()));
        });
    }


    /**
     * check if suggester can be applied to specific component.
     * @param component component to check
     * @param suggester suggester to apply
     */
    private boolean shouldApplySuggester(ComponentType component, EnoSuggesterType suggester) {
        if(component instanceof ComponentSimpleResponseType simpleResponse) {
            String responseName = simpleResponse.getResponse().getName();
            return suggester.getResponseNames().contains(responseName);
        }
        return false;
    }
}