package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.InToEno;
import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.ControlTypeEnum;
import fr.insee.lunatic.model.flat.Input;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ControlTest {

        private EnoIndex index;

        @BeforeAll
        void init() throws ParsingException {
            InputStream poguesStream = this.getClass().getClassLoader().getResourceAsStream("integration/pogues/pogues-controls.json");
            EnoQuestionnaire questionnaire = new PoguesToEno().transform(poguesStream,
                    EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.PROCESS));
            index = questionnaire.getIndex();
        }

        @Test
        void testInputHasCorrectControlsCount(){
            Question question4 = (Question) index.get("lu6y5e4z");
            Question question1 = (Question) index.get("ltx6oc58");
            assertEquals(1, question4.getControls().size());
            assertEquals(0, question1.getControls().size());
        }

        @Test
        void testInputCriticality(){
            Control control = (Control) index.get("lu6xusai");
            assertEquals("ERROR", control.getCriticality().name());
        }

    @Test
    void testInputFailMessage(){
        Control control = (Control) index.get("lu6xusai");
        assertEquals("\"Erreur \" || $INPUT_NONOBE$ || \"doit être différente de E\"", control.getMessage().getValue());
    }

    @Test
    void testInputExpression(){
        Control control = (Control) index.get("lu6xusai");
        assertEquals("nvl($INPUT_NONOBE$,\"\") = \"E\"", control.getExpression().getValue());
    }

    @Test
    void testInputDescription(){
        Control control = (Control) index.get("lu6xusai");
        assertEquals("description du controle erreur", control.getLabel());
    }


private static Stream<Arguments> integrationTest() {
    return Stream.of(
            Arguments.of(new PoguesToEno(), "integration/pogues/pogues-controls.json")
    );
}


@ParameterizedTest
@MethodSource
void integrationTest(InToEno inToEno, String resourcePath) throws ParsingException {
    //
    EnoQuestionnaire enoQuestionnaire = inToEno.transform(
            this.getClass().getClassLoader().getResourceAsStream(resourcePath),
            EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
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

