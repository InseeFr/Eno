package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.parameter.EnoParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnoAddNumberingInSequencesTest {

    private EnoQuestionnaire enoQuestionnaire;

    @BeforeEach
    void createEnoQuestionnaire() {
        enoQuestionnaire = new EnoQuestionnaire();
        Sequence sequence = new Sequence();
        sequence.setLabel(new Label());
        sequence.getLabel().setValue("\"Sequence label\"");
        enoQuestionnaire.getSequences().add(sequence);
    }

    @Test
    void addNumbering() {
        //
        new EnoAddNumberingInSequences(EnoParameters.ModeParameter.CAWI).apply(enoQuestionnaire);
        //
        assertEquals("\"I - \" || \"Sequence label\"",
                enoQuestionnaire.getSequences().get(0).getLabel().getValue());
    }

    @Test
    void addNumbering_staticMode() {
        //
        new EnoAddNumberingInSequences(EnoParameters.ModeParameter.PAPI).apply(enoQuestionnaire);
        //
        assertEquals("I - Sequence label",
                enoQuestionnaire.getSequences().get(0).getLabel().getValue());
    }

}
