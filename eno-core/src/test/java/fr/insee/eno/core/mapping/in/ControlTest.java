package fr.insee.eno.core.mapping.in;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.InToEno;
import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
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

    private static Stream<Arguments> integrationTest() throws ParsingException {
        ClassLoader classLoader = ControlTest.class.getClassLoader();
        return Stream.of(
                Arguments.of(Format.POGUES, PoguesToEno.fromInputStream(classLoader.getResourceAsStream(
                        "integration/pogues/pogues-controls.json"))),
                Arguments.of(Format.DDI, DDIToEno.fromInputStream(classLoader.getResourceAsStream(
                        "integration/ddi/ddi-controls.xml"))));
    }

    @ParameterizedTest
    @MethodSource
    void integrationTest(Format format, InToEno inToEno) {
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
        assertEquals("ERROR", control.getCriticality().name());
        if(format == Format.POGUES){
        assertEquals("\"Erreur \" || $INPUT_NONOBE$ || \"doit être différente de E\"", control.getMessage().getValue());
        assertEquals("nvl($INPUT_NONOBE$,\"\") = \"E\"", control.getExpression().getValue());}
        if(format == Format.DDI){
            assertEquals("\"Erreur \" || INPUT_NONOBE || \"doit être différente de E\" ", control.getMessage().getValue());
            assertEquals("nvl(INPUT_NONOBE,\"\") = \"E\"", control.getExpression().getValue());}
        assertEquals("description du controle erreur", control.getLabel());
    }

}
