package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.RoundaboutSequence;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.vtl.VtlSyntaxUtils;

import java.util.Optional;

/**
 * Temporary processing step to mark filters that correspond to a roundabout filter.
 * This processing also inverts the roundabout occurrence filter expression (which is in the opposite way in DDI
 * compared to Pogues and Lunatic).
 * @see fr.insee.eno.core.model.navigation.Filter
 */
public class DDIMarkRoundaboutFilters implements ProcessingStep<EnoQuestionnaire> {

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getRoundaboutSequences().forEach(roundaboutSequence ->
                markRoundaboutFilter(enoQuestionnaire, roundaboutSequence));
    }

    private static void markRoundaboutFilter(EnoQuestionnaire enoQuestionnaire, RoundaboutSequence roundaboutSequence) {
        /* The trick is, in DDI: roundabout sequence references a loop which references a filter if there is an
        occurrence filter defined for the roundabout. */
        Loop enoLoop = findRoundaboutLoop(enoQuestionnaire, roundaboutSequence);
        ItemReference itemReference = enoLoop.getLoopItems().getFirst();
        if (itemReference.getType() != ItemReference.ItemType.FILTER)
            return;
        enoQuestionnaire.getFilters().stream()
                .filter(enoFilter -> itemReference.getId().equals(enoFilter.getId()))
                .forEach(enoFilter -> {
                    invertFilterExpression(enoFilter);
                    enoFilter.setRoundaboutFilter(true);
                });
    }

    private static Loop findRoundaboutLoop(EnoQuestionnaire enoQuestionnaire, RoundaboutSequence roundaboutSequence) {
        Optional<Loop> enoLoop = enoQuestionnaire.getLoops().stream()
                .filter(loop -> roundaboutSequence.getLoopReference().equals(loop.getId())).findAny();
        if (enoLoop.isEmpty())
            throw new MappingException(String.format(
                    "Unable to find loop '%s' associated to DDI roundabout sequence '%s'.",
                    roundaboutSequence.getLoopReference(), roundaboutSequence.getId()));
        return enoLoop.get();
    }

    private static void invertFilterExpression(Filter enoFilter) {
        if (enoFilter.getExpression() == null)
            throw new MappingException("Eno filter object '" + enoFilter.getId() + "' has no expression.");
        String stringExpression = enoFilter.getExpression().getValue();
        enoFilter.getExpression().setValue(VtlSyntaxUtils.invertBooleanExpression(stringExpression));
    }

}
