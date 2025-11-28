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

            components.replaceAll(component ->
                    component instanceof Question question ?
                            turnIntoSuggester(question.getComponents().getFirst(), enoSuggester) :
                            turnIntoSuggester(component, enoSuggester)
                    );

            components.stream()
                    .filter(Loop.class::isInstance)
                    .map(Loop.class::cast)
                    .forEach(loop -> transformComponentsToSuggesters(loop.getComponents()));
            components.stream()
                    .filter(Roundabout.class::isInstance)
                    .map(Roundabout.class::cast)
                    .forEach(roundabout -> transformComponentsToSuggesters(roundabout.getComponents()));
        });
    }

    private static ComponentType turnIntoSuggester(ComponentType component, EnoSuggesterType enoSuggester) {
        if (component instanceof ComponentSimpleResponseType simpleResponse) {
            String responseName = simpleResponse.getResponse().getName();
            if (enoSuggester.responseNames().contains(responseName))
                return copyToSuggester(component, enoSuggester);
        }
        return component;
    }

    private static ComponentType copyToSuggester(ComponentType component, EnoSuggesterType enoSuggester) {
        Suggester suggesterComponent = new Suggester();
        // copy general component properties
        suggesterComponent.setId(component.getId());
        suggesterComponent.setPage(component.getPage());
        suggesterComponent.setLabel(component.getLabel());
        suggesterComponent.setDescription(component.getDescription());
        suggesterComponent.setDeclarations(component.getDeclarations());
        suggesterComponent.setConditionFilter(component.getConditionFilter());
        suggesterComponent.setControls(component.getControls());
        // copy simple response component properties
        if (! (component instanceof ComponentSimpleResponseType componentSimpleResponse))
            throw new IllegalArgumentException("Only simple response components can be turned into suggesters.");
        suggesterComponent.setMandatory(componentSimpleResponse.getMandatory());
        suggesterComponent.setResponse(componentSimpleResponse.getResponse());
        // set suggester store name
        suggesterComponent.setStoreName(enoSuggester.name());
        return suggesterComponent;
    }

}
