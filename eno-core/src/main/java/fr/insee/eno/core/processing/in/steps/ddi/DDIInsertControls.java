package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.List;

public class DDIInsertControls implements ProcessingStep<EnoQuestionnaire> {

    /** Controls are mapped directly in a flat list in the questionnaire object.
     * This processing is intended to insert them into the objects to which they belong.
     * (Controls are placed after the object they belong to in the sequence items lists.)
     * Concerned objects : sequences, subsequences and questions. */
    public void apply(EnoQuestionnaire enoQuestionnaire) { // Note: code is a bit clumsy but works
        //
        assert enoQuestionnaire.getIndex() != null;
        //
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            List<ItemReference> sequenceItems = sequence.getSequenceItems();
            insertControlsInReferencedItems(enoQuestionnaire, sequenceItems);
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

}
