package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.lunatic.model.flat.Questionnaire;

public class LunaticFiltersResolution implements OutProcessingInterface<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;

    public LunaticFiltersResolution(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.getComponents().forEach(componentType -> {
            String expression = componentType.getConditionFilter().getValue();
            componentType.getConditionFilter().setValue("("+expression+")");
            //
            String componentId = componentType.getId();

        });
    }


}
