package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.InToEno;
import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Input;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DeclarationTest {

    private static Stream<Arguments> integrationTest() throws ParsingException {
        ClassLoader classLoader = DeclarationTest.class.getClassLoader();
        return Stream.of(Arguments.of(DDIToEno.fromInputStream(classLoader.getResourceAsStream(
                                "integration/ddi/ddi-declarations.xml"))),
                Arguments.of(PoguesToEno.fromInputStream(classLoader.getResourceAsStream(
                        "integration/pogues/pogues-declarations.json")))
        );
    }

    @ParameterizedTest
    @MethodSource
    void integrationTest(InToEno inToEno) {
        //
        EnoQuestionnaire enoQuestionnaire = inToEno.transform(
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
        fr.insee.lunatic.model.flat.Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        //
        Input lunaticInput1 = assertInstanceOf(Input.class, lunaticQuestionnaire.getComponents().get(2));
        assertEquals("lk6zkkfr", lunaticInput1.getId());
        assertEquals(4, lunaticInput1.getDeclarations().size());
        assertEquals("BEFORE_QUESTION_TEXT", lunaticInput1.getDeclarations().getFirst().getPosition().value());
        assertEquals("\"Static label 'Aide' before the question\"", lunaticInput1.getDeclarations().getFirst().getLabel().getValue());
    }
}
