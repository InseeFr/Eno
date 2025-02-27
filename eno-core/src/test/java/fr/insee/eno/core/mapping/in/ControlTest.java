package fr.insee.eno.core.mapping.in;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.InToEno;
import fr.insee.eno.core.PoguesDDIToEno;
import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ControlTest {

    private static Stream<Arguments> integrationTest() throws ParsingException {
        ClassLoader classLoader = ControlTest.class.getClassLoader();
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(classLoader.getResourceAsStream(
                "integration/pogues/pogues-controls.json"));
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(classLoader.getResourceAsStream(
                "integration/ddi/ddi-controls.xml"));
        return Stream.of(
                Arguments.of(PoguesToEno.fromObject(poguesQuestionnaire)),
                Arguments.of(DDIToEno.fromObject(ddiQuestionnaire)),
                Arguments.of(PoguesDDIToEno.fromObjects(poguesQuestionnaire, ddiQuestionnaire))
        );
    }
    @ParameterizedTest
    @MethodSource
    void integrationTest(InToEno inToEno) {
        //
        EnoQuestionnaire enoQuestionnaire = inToEno
                .transform(EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.PROCESS));
        //
        Question question1 = assertInstanceOf(Question.class,
                enoQuestionnaire.getSingleResponseQuestions().getFirst());
        Question question4 = assertInstanceOf(Question.class,
                enoQuestionnaire.getSingleResponseQuestions().get(3));
        assertEquals(0, question1.getControls().size());
        assertEquals(1, question4.getControls().size());
        //
        Control control = question4.getControls().getFirst();
        assertEquals(Control.Criticality.ERROR, control.getCriticality());
        assertTrue(control.getMessage().getValue().startsWith("\"Erreur \" || "));
        assertTrue(control.getExpression().getValue().startsWith("nvl("));
        assertEquals("description du controle erreur", control.getLabel());
    }

}
