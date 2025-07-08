package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.processing.ProcessingStep;

/**
 * In Pogues, roundabout are described as sequence objects.
 * This processing step creates the loop object associated with roundabout sequences mapped from Pogues.
 */
public class PoguesRoundaboutLoops implements ProcessingStep<EnoQuestionnaire> {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getRoundaboutSequences().forEach(roundaboutSequence -> {
            LinkedLoop linkedLoop = roundaboutSequence.getInnerLoop();
            linkedLoop.setId(roundaboutSequence.getLoopReference());
            enoQuestionnaire.getLoops().add(linkedLoop);
        });
    }

}
