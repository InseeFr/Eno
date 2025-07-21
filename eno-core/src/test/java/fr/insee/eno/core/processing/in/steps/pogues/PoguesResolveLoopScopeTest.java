package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.model.EnoQuestionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PoguesResolveLoopScopeTest {

    @Test
    void notImplementedYet() {
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        var processing = new PoguesResolveLoopScope();
        assertThrows(UnsupportedOperationException.class, () -> processing.apply(enoQuestionnaire));
    }

}
