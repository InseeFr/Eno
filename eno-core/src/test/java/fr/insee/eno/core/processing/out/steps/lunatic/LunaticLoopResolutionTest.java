package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference.StructureItemType;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticLoopResolutionTest {

    private static final String SEQUENCE_ID = "sequence-id";
    private static final String QUESTION_ID = "question-id";
    private static final String LOOP_ID = "loop-id";

    private EnoQuestionnaire enoQuestionnaire;
    private Questionnaire lunaticQuestionnaire;

    @BeforeEach
    void createSimpleQuestionnaire() {
        // Given
        enoQuestionnaire = new EnoQuestionnaire();
        Sequence sequence = new Sequence();
        sequence.setId(SEQUENCE_ID);
        sequence.getSequenceStructure().add(
                StructureItemReference.builder().id(QUESTION_ID).type(StructureItemType.QUESTION).build());
        enoQuestionnaire.getSequences().add(sequence);
        TextQuestion textQuestion = new TextQuestion();
        textQuestion.setId(QUESTION_ID);
        enoQuestionnaire.getSingleResponseQuestions().add(textQuestion);
        //
        EnoIndex enoIndex = new EnoIndex();
        enoIndex.put(SEQUENCE_ID, sequence);
        enoIndex.put(QUESTION_ID, textQuestion);
        enoQuestionnaire.setIndex(enoIndex);
        //
        lunaticQuestionnaire = new Questionnaire();
        fr.insee.lunatic.model.flat.Sequence lunaticSequence = new fr.insee.lunatic.model.flat.Sequence();
        lunaticSequence.setId(SEQUENCE_ID);
        Input lunaticTextQuestion = new Input();
        lunaticTextQuestion.setId(QUESTION_ID);
        lunaticQuestionnaire.getComponents().add(lunaticSequence);
        lunaticQuestionnaire.getComponents().add(lunaticTextQuestion);
    }

    /** Simple questionnaire without any loop. */
    @Test
    void testLoopResolution_noLoop() {
        // When
        LunaticLoopResolution lunaticLoopResolution = new LunaticLoopResolution(enoQuestionnaire);
        lunaticLoopResolution.apply(lunaticQuestionnaire);
        // Then
        assertEquals(2, lunaticQuestionnaire.getComponents().size());
    }

    /** Questionnaire with single sequence with a loop. */
    @Test
    void testLoopResolution_simplestCase() {
        // Given: adding a standalone loop in Eno questionnaire
        StandaloneLoop standaloneLoop = new StandaloneLoop();
        standaloneLoop.setId(LOOP_ID);
        standaloneLoop.getLoopScope().add(
                StructureItemReference.builder().id(SEQUENCE_ID).type(StructureItemType.SEQUENCE).build());
        standaloneLoop.setMinIteration(new CalculatedExpression());
        standaloneLoop.setMaxIteration(new CalculatedExpression());
        enoQuestionnaire.getLoops().add(standaloneLoop);

        // When
        LunaticLoopResolution lunaticLoopResolution = new LunaticLoopResolution(enoQuestionnaire);
        lunaticLoopResolution.apply(lunaticQuestionnaire);

        // Then
        assertEquals(1, lunaticQuestionnaire.getComponents().size());
        assertTrue(lunaticQuestionnaire.getComponents().get(0) instanceof Loop);
        assertEquals(LOOP_ID, lunaticQuestionnaire.getComponents().get(0).getId());
        assertEquals(2, ((Loop) lunaticQuestionnaire.getComponents().get(0)).getComponents().size());
        assertEquals(SEQUENCE_ID, ((Loop) lunaticQuestionnaire.getComponents().get(0)).getComponents().get(0).getId());
        assertTrue(((Loop) lunaticQuestionnaire.getComponents().get(0)).getComponents().get(1) instanceof Input);
    }

    @Nested
    class IntegrationTests {

        @Test
        @DisplayName("Questionnaire 'l20g2ba7': loops are inserted and contain the right components")
        void largeCoverageQuestionnaire() throws DDIParsingException {
            // Given
            EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                    this.getClass().getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-l20g2ba7.xml"),
                    EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));
            Questionnaire lunaticQuestionnaire = new Questionnaire();
            LunaticMapper lunaticMapper = new LunaticMapper();
            lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
            LunaticSortComponents lunaticSortComponents = new LunaticSortComponents(enoQuestionnaire);
            lunaticSortComponents.apply(lunaticQuestionnaire);

            // When
            LunaticLoopResolution lunaticLoopResolution = new LunaticLoopResolution(enoQuestionnaire);
            lunaticLoopResolution.apply(lunaticQuestionnaire);

            // Then
            List<Loop> loops = lunaticQuestionnaire.getComponents().stream()
                    .filter(componentType -> componentType instanceof Loop)
                    .map(Loop.class::cast)
                    .toList();
            assertEquals(2, loops.size());
            Optional<Loop> loop1 = loops.stream().filter(loop -> loop.getComponents().size() == 3).findAny();
            Optional<Loop> loop2 = loops.stream().filter(loop -> loop.getComponents().size() == 2).findAny();
            assertTrue(loop1.isPresent());
            assertTrue(loop2.isPresent());
            assertTrue(loop1.get().getComponents().get(0) instanceof Subsequence);
            assertTrue(loop1.get().getComponents().get(1) instanceof Input);
            assertTrue(loop1.get().getComponents().get(2) instanceof InputNumber);
            assertTrue(loop2.get().getComponents().get(0) instanceof Subsequence);
            assertTrue(loop2.get().getComponents().get(1) instanceof InputNumber);
        }

    }

}
