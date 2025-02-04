package fr.insee.eno.core.mapping.in;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.reference.EnoIndex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeclarationTest {

    private Map<String, EnoIndex> indices;

    @BeforeAll
    void init() throws ParsingException {
        indices = new HashMap<>();
        indices.put("Pogues", loadIndex("integration/pogues/pogues-declarations.json", true));
        indices.put("DDI", loadIndex("integration/ddi/ddi-declarations.xml", false));
    }

    static EnoIndex loadIndex(String path, boolean isPogues) throws ParsingException {
        InputStream stream = DeclarationTest.class.getClassLoader().getResourceAsStream(path);
        return (isPogues ? PoguesToEno.fromInputStream(stream) : DDIToEno.fromInputStream(stream))
                .transform(EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.PROCESS))
                .getIndex();
    }

    @Test
    void testQuestionHasCorrectDeclarationsAndInstructionsCount() {
        assertForAllIndices((index, isDDI ) -> {
            Question q = (Question) index.get("lk6zkkfr");
            assertEquals(3, q.getDeclarations().size());
            assertEquals(3, q.getInstructions().size());
        });
    }

    @Test
    void testDeclarationLabelAndModes() {
        assertForAllIndices((index, isDDI) -> {
            String id = isDDI ? "lk706b3k-SI" : "lk706b3k"; // Ajout du "-SI" pour DDI
            Declaration d = (Declaration) index.get(id);
            assertEquals("\"Static label 'Aide' before the question\"", d.getLabel().getValue());
            assertEquals(Set.of(Mode.CAPI, Mode.CATI, Mode.CAWI, Mode.PAPI), Set.copyOf(d.getModes()));
        });
    }

    @Test
    void testInstructionPosition() {
        assertForAllIndices((index, isDDI) -> {
            Instruction i = (Instruction) index.get("lk6zp25f");
            assertEquals("AFTER_QUESTION_TEXT", i.getPosition());
        });
    }

    @Test
    void testSequenceHasCorrectDeclarationsAndInstructionsCount() {
        assertForAllIndices((index, isDDI) -> {
            Sequence s = (Sequence) index.get("lk6zlgzm");
            assertEquals(0, s.getDeclarations().size());
            assertEquals(3, s.getInstructions().size());
        });
    }

    private void assertForAllIndices(BiConsumer<EnoIndex, Boolean> assertion) {
        indices.forEach((key, index) -> assertion.accept(index, key.equals("DDI")));
    }
}

