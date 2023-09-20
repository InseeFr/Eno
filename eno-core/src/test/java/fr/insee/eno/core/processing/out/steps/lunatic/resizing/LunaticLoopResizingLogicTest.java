package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class LunaticLoopResizingLogicTest {

    @Test
    void noLoop_noResizing() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        EnoIndex enoIndex = new EnoIndex();
        //
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        //
        assertNull(lunaticQuestionnaire.getResizing());
    }

}
