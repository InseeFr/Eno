package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.lunatic.model.flat.Input;
import fr.insee.lunatic.model.flat.Loop;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.SequenceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticLoopResolutionTest {

    /** Simple questionnaire without any loop. */
    @Test
    void testLoopResolution_noLoop() {
        //
        String sequenceId = "sequence-id";
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        Sequence sequence = new Sequence();
        sequence.setId(sequenceId);
        enoQuestionnaire.getSequences().add(sequence);
        TextQuestion textQuestion = new TextQuestion();
        enoQuestionnaire.getSingleResponseQuestions().add(textQuestion);
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        SequenceType lunaticSequence = new SequenceType();
        lunaticSequence.setId(sequenceId);
        Input lunaticTextQuestion = new Input();
        lunaticQuestionnaire.getComponents().add(lunaticSequence);
        lunaticQuestionnaire.getComponents().add(lunaticTextQuestion);;

        //
        LunaticLoopResolution lunaticLoopResolution = new LunaticLoopResolution(enoQuestionnaire);
        lunaticLoopResolution.apply(lunaticQuestionnaire);

        //
        assertEquals(2, lunaticQuestionnaire.getComponents().size());
    }

    /** Questionnaire with single sequence with a loop. */
    @Test
    void testLoopResolution_simplestCase() {
        //
        String sequenceId = "sequence-id";
        String loopId = "loop-id";
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        Sequence sequence = new Sequence();
        sequence.setId(sequenceId);
        enoQuestionnaire.getSequences().add(sequence);
        TextQuestion textQuestion = new TextQuestion();
        enoQuestionnaire.getSingleResponseQuestions().add(textQuestion);
        StandaloneLoop standaloneLoop = new StandaloneLoop();
        standaloneLoop.setId(loopId);
        standaloneLoop.setSequenceReference(sequenceId);
        standaloneLoop.setMinIteration(new CalculatedExpression());
        standaloneLoop.setMaxIteration(new CalculatedExpression());
        enoQuestionnaire.getLoops().add(standaloneLoop);
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        SequenceType lunaticSequence = new SequenceType();
        lunaticSequence.setId(sequenceId);
        Input lunaticTextQuestion = new Input();
        lunaticQuestionnaire.getComponents().add(lunaticSequence);
        lunaticQuestionnaire.getComponents().add(lunaticTextQuestion);

        //
        LunaticLoopResolution lunaticLoopResolution = new LunaticLoopResolution(enoQuestionnaire);
        lunaticLoopResolution.apply(lunaticQuestionnaire);

        //
        assertEquals(1, lunaticQuestionnaire.getComponents().size());
        assertTrue(lunaticQuestionnaire.getComponents().get(0) instanceof Loop);
        assertEquals(2, ((Loop) lunaticQuestionnaire.getComponents().get(0)).getComponents().size());
        assertEquals(sequenceId, ((Loop) lunaticQuestionnaire.getComponents().get(0)).getComponents().get(0).getId());
        assertTrue(((Loop) lunaticQuestionnaire.getComponents().get(0)).getComponents().get(1) instanceof Input);
    }

}
