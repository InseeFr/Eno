package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.core.reference.LunaticCatalog;
import fr.insee.lunatic.model.flat.*;
import lombok.AllArgsConstructor;

import java.util.Iterator;

@AllArgsConstructor
public class LunaticAddHierarchy implements OutProcessingInterface<Questionnaire> {

    /**
     * Fill the 'hierarchy' field in Lunatic components.
     * Warning: this method supposes that components are sorted and that pagination has been done.
     */
    public void apply(Questionnaire lunaticQuestionnaire) {
        SequenceDescription currentSequenceDescription = null;
        SequenceDescription currentSubsequenceDescription = null;
        for (ComponentType component : lunaticQuestionnaire.getComponents()) {
            Hierarchy hierarchy = new Hierarchy();
            if (component instanceof SequenceType sequence) {
                currentSequenceDescription = createDescription(sequence);
                currentSubsequenceDescription = null;
            } else if (component instanceof Subsequence subsequence) {
                currentSubsequenceDescription = createDescription(subsequence);
            }
            hierarchy.setSequence(currentSequenceDescription);
            hierarchy.setSubSequence(currentSubsequenceDescription);
            component.setHierarchy(hierarchy);
        }
    }

    private static SequenceDescription createDescription(SequenceType sequence) {
        SequenceDescription sequenceDescription = new SequenceDescription();
        sequenceDescription.setId(sequence.getId());
        sequenceDescription.setLabel(sequence.getLabel());
        sequenceDescription.setPage(sequence.getPage());
        return sequenceDescription;
    }

    private static SequenceDescription createDescription(Subsequence subsequence) {
        SequenceDescription sequenceDescription = new SequenceDescription();
        sequenceDescription.setId(subsequence.getId());
        sequenceDescription.setLabel(subsequence.getLabel());
        sequenceDescription.setPage(subsequence.getGoToPage());
        return sequenceDescription;
    }

}
