package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.SequenceItem;
import fr.insee.eno.core.model.sequence.SequenceItem.SequenceItemType;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.EnoProcessing;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
        sequence.getSequenceItems().add(
                SequenceItem.builder().id(QUESTION_ID).type(SequenceItemType.QUESTION).build());
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
        SequenceType lunaticSequence = new SequenceType();
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
        LunaticLoopResolution2 lunaticLoopResolution = new LunaticLoopResolution2(enoQuestionnaire);
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
        standaloneLoop.setSequenceReference(SEQUENCE_ID);
        standaloneLoop.setMinIteration(new CalculatedExpression());
        standaloneLoop.setMaxIteration(new CalculatedExpression());
        enoQuestionnaire.getLoops().add(standaloneLoop);

        // When
        LunaticLoopResolution2 lunaticLoopResolution = new LunaticLoopResolution2(enoQuestionnaire);
        lunaticLoopResolution.apply(lunaticQuestionnaire);

        // Then
        assertEquals(1, lunaticQuestionnaire.getComponents().size());
        assertTrue(lunaticQuestionnaire.getComponents().get(0) instanceof Loop);
        assertEquals(LOOP_ID, lunaticQuestionnaire.getComponents().get(0).getId());
        assertEquals(2, ((Loop) lunaticQuestionnaire.getComponents().get(0)).getComponents().size());
        assertEquals(SEQUENCE_ID, ((Loop) lunaticQuestionnaire.getComponents().get(0)).getComponents().get(0).getId());
        assertTrue(((Loop) lunaticQuestionnaire.getComponents().get(0)).getComponents().get(1) instanceof Input);
    }

    static class IntegrationTests {

        @Test
        void largeCoverageQuestionnaire() throws DDIParsingException {
            // Given
            EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIParser.parse(this.getClass().getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-l20g2ba7.xml")),
                    enoQuestionnaire);
            EnoProcessing enoProcessing = new EnoProcessing();
            enoProcessing.applyProcessing(enoQuestionnaire, Format.DDI);
            Questionnaire lunaticQuestionnaire = new Questionnaire();
            LunaticMapper lunaticMapper = new LunaticMapper();
            lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);


            // When
            LunaticLoopResolution2 lunaticLoopResolution = new LunaticLoopResolution2(enoQuestionnaire);
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
