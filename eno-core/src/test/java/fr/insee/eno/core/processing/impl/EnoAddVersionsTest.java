package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnoAddVersionsTest {

    @Test
    void addVersions_resultNotNull() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        new EnoAddVersions().apply(enoQuestionnaire);
        //
        assertNotNull(enoQuestionnaire.getEnoVersion());
        assertNotNull(enoQuestionnaire.getLunaticModelVersion());
    }

}
