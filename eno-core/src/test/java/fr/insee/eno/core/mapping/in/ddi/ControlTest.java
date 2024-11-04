package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControlTest {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class IntegrationTest {
        private Map<String, Control> controls;

        @BeforeAll
        void mapDDI() throws DDIParsingException {
            //
            EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
            DDIInstanceDocument ddiInstance = DDIDeserializer.deserialize(
                    ControlTest.class.getClassLoader().getResourceAsStream("integration/ddi/ddi-controls.xml"));
            //
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(ddiInstance, enoQuestionnaire);
            //
            controls = new HashMap<>();
            enoQuestionnaire.getControls().forEach(control -> controls.put(control.getId(), control));
        }

        @Test
        void controlsCount() {
            assertEquals(3 , controls.size());
        }

        @Test
        void typeOfControlTest() {
            controls.values().forEach(control ->
                    assertEquals(Control.TypeOfControl.CONSISTENCY, control.getTypeOfControl()));
        }

        @Test
        void controlCriticalityTest() {
            assertEquals(Control.Criticality.INFO, controls.get("ltx6cof9-CI-0").getCriticality());
            assertEquals(Control.Criticality.WARN, controls.get("lu6xrmto-CI-0").getCriticality());
            assertEquals(Control.Criticality.ERROR, controls.get("lu6y5e4z-CI-0").getCriticality());
        }
    }

}
