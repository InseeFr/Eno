package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.treatments.dto.EnoSuggesterType;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Processing for suggesters
 */
@Slf4j
public class LunaticSuggesterSpecificTreatment implements ProcessingStep<Questionnaire> {

    private final List<EnoSuggesterType> enoSuggesters;

    /**
     * Initialize eno suggesters from input
     * @param suggesters suggester list
     */
    public LunaticSuggesterSpecificTreatment(List<EnoSuggesterType> suggesters) {
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

            components.forEach(component -> applySuggesterProperties(component, enoSuggester));

            components.stream()
                    .filter(component -> component.getComponentType().equals(ComponentTypeEnum.LOOP))
                    .map(Loop.class::cast)
                    .forEach(loop -> transformComponentsToSuggesters(loop.getComponents()));
        });
    }


    /**
     * Checks if suggester can be applied to specific component,
     * and sets suggester properties to this component if so.
     * @param component component to check
     * @param enoSuggester suggester to apply
     */
    private static void applySuggesterProperties(ComponentType component, EnoSuggesterType enoSuggester) {
        // Component to be replaced by a suggester component must be a simple response component
        if (component instanceof ComponentSimpleResponseType simpleResponse) {
            String responseName = simpleResponse.getResponse().getName();
            if (enoSuggester.responseNames().contains(responseName)) {
                component.setComponentType(ComponentTypeEnum.SUGGESTER);
                component.setStoreName(enoSuggester.name());
            }
        }
        // If Lunatic V3 question processing has been applied, look at the component within the question
        if (component instanceof Question question)
            applySuggesterProperties(question.getComponents().getFirst(), enoSuggester);
    }

}