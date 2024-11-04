package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DDIMarkRowControlsTest {

    @Test
    void roundaboutLevelControls() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIInstanceDocument ddiInstance = DDIDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-roundabout-controls.xml"));
        //
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(ddiInstance, enoQuestionnaire);

        // When
        new DDIMarkRowControls().apply(enoQuestionnaire);

        // Then
        // The questionnaire should have two controls (that are created for a roundabout)
        assertEquals(2, enoQuestionnaire.getControls().size());
        assertTrue(enoQuestionnaire.getControls().stream().anyMatch(control ->
                Control.Context.SIMPLE.equals(control.getContext())
        ));
        assertTrue(enoQuestionnaire.getControls().stream().anyMatch(control ->
                Control.Context.ROW.equals(control.getContext())
        ));
    }

}
