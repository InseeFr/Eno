package fr.insee.eno.core.mapping.in;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.InToEno;
import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeclarationTest {

    private static Stream<Arguments> integrationTest() throws ParsingException {
        ClassLoader classLoader = DeclarationTest.class.getClassLoader();
        return Stream.of(
                Arguments.of(Format.POGUES, PoguesToEno.fromInputStream(classLoader.getResourceAsStream(
                        "integration/pogues/pogues-declarations.json"))),
                Arguments.of(Format.DDI, DDIToEno.fromInputStream(classLoader.getResourceAsStream(
                        "integration/ddi/ddi-declarations.xml"))));
    }

    @ParameterizedTest
    @MethodSource
    void integrationTest(Format format, InToEno inToEno) {
        //
        EnoQuestionnaire enoQuestionnaire = inToEno
                .transform(EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.PROCESS));
        EnoIndex index = enoQuestionnaire.getIndex();
        //
        Question question = assertInstanceOf(Question.class,
                enoQuestionnaire.getSingleResponseQuestions().getFirst());
        assertEquals(3, question.getDeclarations().size());
        assertEquals(3, question.getInstructions().size());
        //
        if (format == Format.POGUES) {
            Declaration declaration = (Declaration) index.get("lk706b3k");
            assertEquals("\"Static label 'Aide' before the question\"", declaration.getLabel().getValue());
            assertEquals(Set.of(Mode.CAPI, Mode.CATI, Mode.CAWI, Mode.PAPI), Set.copyOf(declaration.getModes()));
        }
        if (format == Format.DDI){Declaration declaration = (Declaration) index.get("lk706b3k-SI");
            assertEquals("\"Static label 'Aide' before the question\"", declaration.getLabel().getValue());
            assertEquals(Set.of(Mode.CAPI, Mode.CATI, Mode.CAWI, Mode.PAPI), Set.copyOf(declaration.getModes()));
        }
        //
        Instruction instruction = (Instruction) index.get("lk6zp25f");
        assertEquals("AFTER_QUESTION_TEXT", instruction.getPosition());
        //
        Sequence sequence = assertInstanceOf(Sequence.class,
                enoQuestionnaire.getSequences().getFirst());
        assertEquals(0, sequence.getDeclarations().size());
        assertEquals(3, sequence.getInstructions().size());
    }
}


