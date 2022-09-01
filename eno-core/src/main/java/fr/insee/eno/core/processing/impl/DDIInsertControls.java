package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.*;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.processing.InProcessingInterface;

import java.util.List;

public class DDIInsertControls implements InProcessingInterface {

    /** Controls are mapped directly in a flat list in the questionnaire object.
     * This processing is intended to insert them into the objects to which they belong.
     * (Controls are placed after the object they belong to in the sequence items lists.)
     * Concerned objects : sequences, subsequences and questions. */
    public void apply(EnoQuestionnaire enoQuestionnaire) { // TODO: code is a bit clumsy but works
        //
        assert enoQuestionnaire.getIndex() != null;
        //
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            List<SequenceItem> sequenceItems = sequence.getSequenceItems();
            if (! sequenceItems.isEmpty()) {
                int bound = sequenceItems.size();
                // Sequence controls
                int i = 0;
                while (i<bound && sequenceItems.get(i).getType() == SequenceItem.SequenceItemType.CONTROL) {
                    sequence.getControls().add(
                            (Control) enoQuestionnaire.get(sequenceItems.get(i).getId()));
                    i ++;
                }
                // Elements (questions, subsequences) in sequence
                while (i < bound) {
                    SequenceItem sequenceItem = sequenceItems.get(i);
                    if (sequenceItem.getType() == SequenceItem.SequenceItemType.QUESTION) {
                        Question question = (Question) enoQuestionnaire.get(sequenceItem.getId());
                        i ++;
                        while (i<bound && sequenceItems.get(i).getType() == SequenceItem.SequenceItemType.CONTROL) {
                            question.getControls().add(
                                    (Control) enoQuestionnaire.get(sequenceItems.get(i).getId()));
                            i ++;
                        }
                    }
                    else if (sequenceItem.getType() == SequenceItem.SequenceItemType.SUBSEQUENCE) {
                        Subsequence subsequence = (Subsequence) enoQuestionnaire.get(sequenceItem.getId());
                        List<SequenceItem> subsequenceItems = subsequence.getSequenceItems();
                        if (! subsequenceItems.isEmpty()) {
                            int bound2 = subsequenceItems.size();
                            // Subsequence controls
                            int j = 0;
                            while (j<bound2 && subsequenceItems.get(j).getType() == SequenceItem.SequenceItemType.CONTROL) {
                                subsequence.getControls().add(
                                        (Control) enoQuestionnaire.get(subsequenceItems.get(j).getId()));
                                j ++;
                            }
                            // Elements (questions) in subsequence
                            while (j < bound2) {
                                SequenceItem subsequenceItem = subsequenceItems.get(j);
                                if (subsequenceItem.getType() == SequenceItem.SequenceItemType.QUESTION) {
                                    Question question = (Question) enoQuestionnaire.get(subsequenceItem.getId());
                                    j ++;
                                    while (j<bound2 && subsequenceItems.get(j).getType() == SequenceItem.SequenceItemType.CONTROL) {
                                        question.getControls().add(
                                                (Control) enoQuestionnaire.get(subsequenceItems.get(j).getId()));
                                        j ++;
                                    }
                                }
                                else { // skip other elements
                                    j ++;
                                }
                            }
                        }
                        i ++;
                    }
                    else { // skip other elements
                        i ++;
                    }
                }
            }
        }
    }

}
