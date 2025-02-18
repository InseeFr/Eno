package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.PoguesDDIToEno;
import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ControlTest {

    final ClassLoader classLoader = this.getClass().getClassLoader();
    private Map<String, ComponentType> lunaticComponents;

    @BeforeAll
    void ddiToLunaticMapping() throws ParsingException {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        EnoQuestionnaire enoQuestionnaire = mapInput();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        //
        lunaticComponents = new HashMap<>();
        lunaticQuestionnaire.getComponents().forEach(component -> lunaticComponents.put(component.getId(), component));
    }

    abstract EnoQuestionnaire mapInput() throws ParsingException;

    static class DDITest extends ControlTest {
        @Override
        EnoQuestionnaire mapInput() throws DDIParsingException {
            return DDIToEno.fromInputStream(
                    classLoader.getResourceAsStream("integration/ddi/ddi-controls.xml"))
                    .transform(EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        }
    }

    static class PoguesTest extends ControlTest {
        @Override
        EnoQuestionnaire mapInput() throws PoguesDeserializationException {
            return PoguesToEno.fromInputStream(
                    classLoader.getResourceAsStream("integration/pogues/pogues-controls.json"))
                    .transform(EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        }
    }

    static class PoguesDDITest extends ControlTest {
        @Override
        EnoQuestionnaire mapInput() throws ParsingException {
            return PoguesDDIToEno.fromInputStreams(
                            classLoader.getResourceAsStream("integration/pogues/pogues-controls.json"),
                            classLoader.getResourceAsStream("integration/ddi/ddi-controls.xml"))
                    .transform(EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        }
    }

    @Test
    void controlsCount() {
        assertTrue(lunaticComponents.get("ltx6oc58").getControls().isEmpty());
        assertEquals(1, lunaticComponents.get("ltx6cof9").getControls().size());
        assertEquals(1, lunaticComponents.get("lu6xrmto").getControls().size());
        assertEquals(1, lunaticComponents.get("lu6y5e4z").getControls().size());
        assertTrue(lunaticComponents.get("lu71az37").getControls().isEmpty());
    }

    @Test
    void typeOfControlTest() {
        List.of("ltx6cof9", "lu6xrmto", "lu6y5e4z").forEach(componentId ->
                assertEquals(ControlTypeEnum.CONSISTENCY,
                        lunaticComponents.get(componentId).getControls().getFirst().getTypeOfControl()));
    }

    @Test
    void controlCriticalityTest() {
        assertEquals(ControlCriticalityEnum.INFO,
                lunaticComponents.get("ltx6cof9").getControls().getFirst().getCriticality());
        assertEquals(ControlCriticalityEnum.WARN,
                lunaticComponents.get("lu6xrmto").getControls().getFirst().getCriticality());
        assertEquals(ControlCriticalityEnum.WARN,
                lunaticComponents.get("lu6y5e4z").getControls().getFirst().getCriticality());
    }

    @Test
    void integrationTest() {
        //
        Input lunaticInput = assertInstanceOf(Input.class, lunaticComponents.get("lu6y5e4z"));
        assertEquals(1, lunaticInput.getControls().size());
        assertNotEquals("lu6y5e4z", lunaticInput.getControls().getFirst().getId());
        assertEquals("WARN", lunaticInput.getControls().getFirst().getCriticality().name());
        assertTrue(lunaticInput.getControls().getFirst().getErrorMessage().getValue().startsWith("\"Erreur \" || "));
        assertTrue(lunaticInput.getControls().getFirst().getControl().getValue().startsWith("nvl("));
        assertEquals(ControlTypeEnum.CONSISTENCY, lunaticInput.getControls().getFirst().getTypeOfControl());
    }

}
