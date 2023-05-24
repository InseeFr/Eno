package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.treatments.dto.EnoSuggesterType;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;

/**
 * Processing for suggesters
 */
@Slf4j
public class LunaticSuggesterProcessing implements OutProcessingInterface<Questionnaire> {

    private final List<EnoSuggesterType> enoSuggesters;

    /**
     * Initialize eno suggesters from input
     * @param jsonSuggestersStream input stream of json suggesters
     */
    public LunaticSuggesterProcessing(InputStream jsonSuggestersStream) {
        SuggesterDeserializer deserializer = new SuggesterDeserializer();
        this.enoSuggesters = deserializer.deserializeSuggesters(jsonSuggestersStream);
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
     * ugly method to check if suggester can be applied to specific component. Maybe need more abstraction in lunatic model ?
     * @param component component to check
     * @param suggester suggester to apply
     */
    private boolean shouldApplySuggester(ComponentType component, EnoSuggesterType suggester) {
        String responseName;

        switch (component.getComponentType()) {
            case INPUT -> {
                Input componentType = (Input) component;
                responseName = componentType.getResponse().getName();
            }
            case INPUT_NUMBER -> {
                InputNumber componentType = (InputNumber) component;
                responseName = componentType.getResponse().getName();
            }
            case TEXTAREA -> {
                Textarea componentType = (Textarea) component;
                responseName = componentType.getResponse().getName();
            }
            case CHECKBOX_ONE -> {
                CheckboxOne componentType = (CheckboxOne) component;
                responseName = componentType.getResponse().getName();
            }
            case CHECKBOX_BOOLEAN -> {
                CheckboxBoolean componentType = (CheckboxBoolean) component;
                responseName = componentType.getResponse().getName();
            }
            case DATEPICKER -> {
                Datepicker componentType = (Datepicker) component;
                responseName = componentType.getResponse().getName();
            }
            case RADIO -> {
                Radio componentType = (Radio) component;
                responseName = componentType.getResponse().getName();
            }
            case DROPDOWN -> {
                Dropdown componentType = (Dropdown) component;
                responseName = componentType.getResponse().getName();
            }
            default -> {
                return false;
            }
        }

        return suggester.getResponseNames().contains(responseName);
    }
}