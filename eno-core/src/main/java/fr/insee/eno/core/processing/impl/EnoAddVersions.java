package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.EnoProcessingInterface;
import fr.insee.eno.core.version.ModelVersions;

public class EnoAddVersions implements EnoProcessingInterface {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.setEnoVersion(ModelVersions.enoVersion());
        enoQuestionnaire.setLunaticModelVersion(ModelVersions.lunaticModelVersion());
    }

}
