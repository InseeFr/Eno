package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.RoundaboutSequence;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DDIInsertControls implements ProcessingStep<EnoQuestionnaire> {


    /** Controls are mapped directly in a flat list in the questionnaire object.
     * This processing is intended to insert them into the objects to which they belong.
     * (Controls are placed after the object they belong to in the sequence items lists.)
     * Concerned objects : sequences, subsequences and questions. */
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            List<ItemReference> sequenceItems = sequence.getSequenceItems();
            insertControlsInReferencedItems(enoQuestionnaire, sequenceItems);
        }
        Map<String, Control> controlMap = DDIMarkRowControls.mapQuestionnaireControls(enoQuestionnaire);
        for (RoundaboutSequence roundaboutSequence : enoQuestionnaire.getRoundaboutSequences()) {
            insertControlsInRoundabout(roundaboutSequence, enoQuestionnaire, controlMap);
        }
    }

    private static void insertControlsInReferencedItems(EnoQuestionnaire enoQuestionnaire, List<ItemReference> itemReferences) {
        if (itemReferences.isEmpty())
            return;
        int bound = itemReferences.size();
        int i = 0;
        while (i < bound) {
            ItemReference sequenceItem = itemReferences.get(i);
            if (sequenceItem.getType() == ItemReference.ItemType.QUESTION) {
                i = insertControlInQuestion(enoQuestionnaire, itemReferences, bound, i, sequenceItem);
            }
            else if (sequenceItem.getType() == ItemReference.ItemType.SUBSEQUENCE) {
                Subsequence subsequence = (Subsequence) enoQuestionnaire.get(sequenceItem.getId());
                List<ItemReference> subsequenceItems = subsequence.getSequenceItems();
                insertControlsInReferencedItems(enoQuestionnaire, subsequenceItems);
                i ++;
            }
            else if (sequenceItem.getType() == ItemReference.ItemType.LOOP) {
                Loop loop = (Loop) enoQuestionnaire.get(sequenceItem.getId());
                List<ItemReference> loopItems = loop.getLoopItems();
                insertControlsInReferencedItems(enoQuestionnaire, loopItems);
                i ++;
            }
            else if (sequenceItem.getType() == ItemReference.ItemType.FILTER) {
                Filter filter = (Filter) enoQuestionnaire.get(sequenceItem.getId());
                List<ItemReference> filterItems = filter.getFilterItems();
                insertControlsInReferencedItems(enoQuestionnaire, filterItems);
                i ++;
            }
            else { // skip other elements
                i ++;
            }
        }
    }

    private static int insertControlInQuestion(EnoQuestionnaire enoQuestionnaire, List<ItemReference> itemReferences,
                                               int bound, int i, ItemReference questionReferenceItem) {
        Question question = (Question) enoQuestionnaire.get(questionReferenceItem.getId());
        i++;
        while (i < bound && itemReferences.get(i).getType() == ItemReference.ItemType.CONTROL) {
            question.getControls().add(
                    (Control) enoQuestionnaire.get(itemReferences.get(i).getId()));
            i++;
        }
        return i;
    }

    private void insertControlsInRoundabout(RoundaboutSequence roundaboutSequence,
                                            EnoQuestionnaire enoQuestionnaire,
                                            Map<String, Control> controlMap) {
        insertRoundaboutLevelControls(roundaboutSequence, controlMap);
        insertRowLevelControls(roundaboutSequence, enoQuestionnaire, controlMap);
    }

    private static void insertRoundaboutLevelControls(RoundaboutSequence roundaboutSequence, Map<String, Control> controlMap) {
        roundaboutSequence.getSequenceItems().stream()
                .filter(itemReference -> ItemReference.ItemType.CONTROL.equals(itemReference.getType()))
                .forEach(itemReference -> roundaboutSequence.getControls().add(controlMap.get(itemReference.getId())));
    }

    private static void insertRowLevelControls(RoundaboutSequence roundaboutSequence, EnoQuestionnaire enoQuestionnaire, Map<String, Control> controlMap) {
        // Check that loop reference is not null to avoid null pointer exception
        if (roundaboutSequence.getLoopReference() == null)
            throw new MappingException(
                    "Loop reference of roundabout sequence '" + roundaboutSequence.getId() + "' is null.");
        // Get the corresponding loop
        Optional<Loop> roundaboutLoop = enoQuestionnaire.getLoops().stream()
                .filter(loop -> roundaboutSequence.getLoopReference().equals(loop.getId()))
                .findAny();
        // Make sure it is present
        if (roundaboutLoop.isEmpty())
            throw new MappingException("Cannot find loop '" + roundaboutSequence.getLoopReference() + "' " +
                    "referenced in roundabout sequence '" + roundaboutSequence.getId() + "'");
        // Insert all the controls that are referenced in the loop in the roundabout sequence object
        DDIMarkRowControls.getLoopControlReferences(roundaboutLoop.get(), enoQuestionnaire)
                .forEach(itemReference -> roundaboutSequence.getControls().add(controlMap.get(itemReference.getId())));
    }

}
