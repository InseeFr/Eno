package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.sequence.*;
import fr.insee.eno.core.processing.ProcessingStep;
import lombok.extern.slf4j.Slf4j;

/** Sequence objects contain "sequence items" lists, that are designed to keep the structure of the questionnaire.
 * These lists are mapped from the DDI, and contain the reference of components in the right order.
 * Yet, this information does not allow to retrieve questionnaire's structure easily.
 * Thus, this processing class fills the "sequence structure" property of sequence objects.
 * */
@Slf4j
public class DDIResolveSequencesStructure implements ProcessingStep<EnoQuestionnaire> {

    private EnoQuestionnaire enoQuestionnaire;

    /** Fills the "sequence structure" list in each sequence of the eno questionnaire given,
     * using the "sequence items" list mapped from DDI.
     * @param enoQuestionnaire Eno questionnaire mapped from a DDI.
     * */
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            resolveSequenceStructure(sequence);
        }
    }

    /** Iterates on sequence items of the given sequence and resolve them.
     * See <code>resolveStructure</code> method.
     * @param sequence A sequence or subsequence object.
     * */
    private void resolveSequenceStructure(AbstractSequence sequence) {
        for (ItemReference sequenceItem : sequence.getSequenceItems()) {
            resolveStructure(sequence, sequenceItem);
        }
    }

    /** Iterates on items listed in the filter object, and resolve them in the sequence object.
     * See <code>resolveStructure</code> method.
     * @param filter A filter object.
     * @param sequence A sequence or subsequence.
     * */
    private void resolveFilterStructure(Filter filter, AbstractSequence sequence) {
        for (ItemReference filterItem : filter.getFilterItems()) {
            resolveStructure(sequence, filterItem);
        }
    }

    /** Iterates on items listed in the loop object, and resolve them in the sequence object.
     * See <code>resolveStructure</code> method.
     * @param loop A filter object.
     * @param sequence A sequence or subsequence.
     * */
    private void resolveLoopStructure(Loop loop, AbstractSequence sequence) {
        for (ItemReference loopItem : loop.getLoopItems()) {
            resolveStructure(sequence, loopItem);
        }
    }

    /** <p>Resolves the sequence item given, recursively if needed. "Resolve" means inserting sequence items in the
     * "sequence structure" of given sequence. It is done by:</p>
     * <ul>
     *   <li>filtering sequence items that are not a part of the questionnaire's structure (e.g. controls,
     *   declarations)</li>
     *   <li>replacing items that encapsulate other items (loops and filters).</li>
     * </ul>
     * <p>Note: (!) Nested loop is not supported here yet. (!)</p>
     * @param sequence A sequence or subsequence.
     * @param sequenceItem A sequence item.
     * */
    private void resolveStructure(AbstractSequence sequence, ItemReference sequenceItem) {
        switch (sequenceItem.getType()) {
            case SEQUENCE -> {
                log.error("Sequences should not contain filter or loop references that have a sequence in their scope");
                throw new MappingException("Error when resolving structure of sequence "+sequence);
            }
            case SUBSEQUENCE -> {
                sequence.getSequenceStructure().add(StructureItemReference.from(sequenceItem));
                Subsequence subsequence = (Subsequence) enoQuestionnaire.get(sequenceItem.getId());
                resolveSequenceStructure(subsequence);
            }
            case QUESTION ->
                    sequence.getSequenceStructure().add(StructureItemReference.from(sequenceItem));
            case LOOP -> {
                Loop loop = (Loop) enoQuestionnaire.get(sequenceItem.getId());
                resolveLoopStructure(loop, sequence);
            }
            case FILTER -> {
                Filter filter = (Filter) enoQuestionnaire.get(sequenceItem.getId());
                resolveFilterStructure(filter, sequence);
            }
            case CONTROL, DECLARATION ->
                    log.debug("Control and declaration are ignored while resolving sequence structure.");
        }
    }

}
