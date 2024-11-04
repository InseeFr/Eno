package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.ControlCriticalityEnum;
import fr.insee.lunatic.model.flat.ControlTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ControlTest {

    private Map<String, ComponentType> lunaticComponents;

    @BeforeAll
    void ddiToLunaticMapping() throws DDIParsingException {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                ControlTest.class.getClassLoader().getResourceAsStream("integration/ddi/ddi-controls.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoQuestionnaire, lunaticQuestionnaire);
        //
        lunaticComponents = new HashMap<>();
        lunaticQuestionnaire.getComponents().forEach(component -> lunaticComponents.put(component.getId(), component));
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

}
