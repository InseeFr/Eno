package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.*;

/**
 * Sort loops that have them in order of appearance in the questionnaire.
 * Note: this step is required for Pogues+DDI mapping: loops are ordered this way in DDI.
 */
public class PoguesSortLoops implements ProcessingStep<EnoQuestionnaire> {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {

        // Put identifiers of sequences and subsequences in a flat list
        List<String> orderedSequenceIds = flatQuestionnaireStructure(enoQuestionnaire);

        // Index this list
        Map<String, Integer> idIndex = new HashMap<>();
        for (int i = 0; i < orderedSequenceIds.size(); i++) {
            idIndex.put(orderedSequenceIds.get(i), i);
        }

        // Sort loops by the first element in their scope (using the index created above)
        enoQuestionnaire.getLoops().sort(Comparator.comparingInt(loop ->
                idIndex.getOrDefault(loop.getPoguesStartReference(), Integer.MAX_VALUE)));
    }

    private static List<String> flatQuestionnaireStructure(EnoQuestionnaire enoQuestionnaire) {
        List<String> result = new ArrayList<>();
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            result.add(sequence.getId());
            for (StructureItemReference reference : sequence.getSequenceStructure()) {
                switch (reference.getType()) {
                    case SUBSEQUENCE -> result.add(reference.getId());
                    case QUESTION -> doNothing(); // Loops can only be defined on sequences or subsequences
                    case SEQUENCE -> throw new MappingException(
                            "Sequence (id=%s) referenced within another sequence (id=%s)."
                                    .formatted(reference.getId(), sequence.getId())); // (Should not happen)
                }
            }
        }
        return result;
    }
    private static void doNothing() {
        /* No-op method */
    }

}
