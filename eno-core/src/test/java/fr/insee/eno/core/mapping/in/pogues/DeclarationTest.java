package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.InToEno;
import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.Input;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeclarationTest {

    private EnoIndex index;

    @BeforeAll
    void init() throws ParsingException {
        InputStream poguesStream = this.getClass().getClassLoader().getResourceAsStream("integration/pogues/pogues-declarations.json");
        EnoQuestionnaire questionnaire = PoguesToEno.fromInputStream(poguesStream)
                .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.PROCESS));
        index = questionnaire.getIndex();
    }

    @Test
    void testQuestionHasCorrectDeclarationsAndInstructionsCount() {
        Question question1 = (Question) index.get("lk6zkkfr");
        assertEquals(3, question1.getDeclarations().size());
        assertEquals(3, question1.getInstructions().size());
    }

    @Test
    void testDeclarationLabelAndModes() {
        Declaration declaration1 = (Declaration) index.get("lk706b3k");
        assertEquals("\"Static label 'Aide' before the question\"", declaration1.getLabel().getValue());
        assertEquals(List.of(Mode.CAPI, Mode.CATI, Mode.CAWI, Mode.PAPI), declaration1.getModes());
    }

    @Test
    void testInstructionPosition() {
        Instruction instruction1 = (Instruction) index.get("lk6zp25f");
        assertEquals("AFTER_QUESTION_TEXT", instruction1.getPosition());
    }

    @Test
    void testSequenceHasCorrectDeclarationsAndInstructionsCount() {
        Sequence sequence1 = (Sequence) index.get("lk6zlgzm");
        assertEquals(0, sequence1.getDeclarations().size());
        assertEquals(3, sequence1.getInstructions().size());

    }

    private static Stream<Arguments> integrationTest() throws ParsingException {
        ClassLoader classLoader = DeclarationTest.class.getClassLoader();
        return Stream.of(
                Arguments.of(PoguesToEno.fromInputStream(classLoader.getResourceAsStream(
                        "integration/pogues/pogues-declarations.json")))
        );
    }

    @ParameterizedTest
    @MethodSource
    void integrationTest(InToEno inToEno) {
        //
        EnoQuestionnaire enoQuestionnaire = inToEno.transform(
                EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));
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
