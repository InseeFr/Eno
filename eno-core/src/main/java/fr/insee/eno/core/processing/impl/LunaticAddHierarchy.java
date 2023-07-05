package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.lunatic.model.flat.*;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class LunaticAddHierarchy implements OutProcessingInterface<Questionnaire> {

    /**
     * Fill the 'hierarchy' field in Lunatic components.
     * Warning: this method supposes that components are sorted and that pagination has been done.
     */
    public void apply(Questionnaire lunaticQuestionnaire) {
        generateHierarchy(lunaticQuestionnaire.getComponents(), null, null);
    }

    /** Create hierarchy objects and put them in components.
     * Recursive call when the component is a loop. */
    private static void generateHierarchy(List<ComponentType> components,
                                          SequenceDescription currentSequenceDescription,
                                          SequenceDescription currentSubsequenceDescription) {
        for (ComponentType component : components) {
            Hierarchy hierarchy = new Hierarchy();
            if (component instanceof Loop loop) {
                generateHierarchy(loop.getComponents(), currentSequenceDescription, currentSubsequenceDescription);
            }
            if (component instanceof Sequence sequence) {
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

    private static SequenceDescription createDescription(Sequence sequence) {
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
        if (subsequence.getDeclarations().isEmpty())
            sequenceDescription.setPage(subsequence.getGoToPage());
        else
            sequenceDescription.setPage(subsequence.getPage());
        return sequenceDescription;
    }

}
