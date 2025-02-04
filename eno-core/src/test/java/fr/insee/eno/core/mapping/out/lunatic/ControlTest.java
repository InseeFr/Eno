package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.InToEno;
import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ControlTest {

    private Map<String, ComponentType> lunaticComponents;

    @BeforeAll
    void ddiToLunaticMapping() throws DDIParsingException {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                ControlTest.class.getClassLoader().getResourceAsStream("integration/ddi/ddi-controls.xml"))
                .transform(EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
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

    private static Stream<Arguments> integrationTest() throws ParsingException {
        ClassLoader classLoader = ControlTest.class.getClassLoader();
        return Stream.of(
                Arguments.of(PoguesToEno.fromInputStream(classLoader.getResourceAsStream(
                                "integration/pogues/pogues-controls.json")),
                        Arguments.of(DDIToEno.fromInputStream(classLoader.getResourceAsStream(
                                "integration/ddi/ddi-controls.xml")))
                ));
    }

    @ParameterizedTest
    @MethodSource
    void integrationTest(InToEno inToEno) {
        //
        EnoQuestionnaire enoQuestionnaire = inToEno
                .transform(EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
        fr.insee.lunatic.model.flat.Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        //
        Input lunaticInput = assertInstanceOf(Input.class, lunaticQuestionnaire.getComponents().get(4));
        assertEquals("lu6y5e4z", lunaticInput.getId());
        assertEquals(1, lunaticInput.getControls().size());
        assertEquals("lu6xusai", lunaticInput.getControls().getFirst().getId());
        assertEquals("WARN", lunaticInput.getControls().getFirst().getCriticality().name());
        assertEquals("\"Erreur \" || $INPUT_NONOBE$ || \"doit être différente de E\"", lunaticInput.getControls().getFirst().getErrorMessage().getValue());
        assertEquals("nvl($INPUT_NONOBE$,\"\") = \"E\"", lunaticInput.getControls().getFirst().getControl().getValue());
        assertEquals(ControlTypeEnum.CONSISTENCY, lunaticInput.getControls().getFirst().getTypeOfControl());
    }
}
