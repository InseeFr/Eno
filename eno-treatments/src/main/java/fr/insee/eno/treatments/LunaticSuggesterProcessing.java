package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.treatments.dto.EnoSuggesterType;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Input;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.SuggesterType;
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
                    .filter(component -> component.getComponentType().equals(ComponentTypeEnum.INPUT))
                    .map(Input.class::cast)
                    .filter(inputComponent -> enoSuggester.getResponseNames().contains(inputComponent.getResponse().getName()))
                    .forEach(inputComponent -> {
                        inputComponent.setComponentType(ComponentTypeEnum.SUGGESTER);
                        inputComponent.setStoreName(enoSuggester.getName());
                    }));
    }
}