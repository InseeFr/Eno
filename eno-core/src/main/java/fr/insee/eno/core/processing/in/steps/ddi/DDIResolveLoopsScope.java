package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.processing.ProcessingStep;
import lombok.extern.slf4j.Slf4j;

/** Loop objects contain a "loop items" list, mapped from the DDI, that contain the reference of components
 * that are in the scope of the loop, in the right order.
 * This processing class converts this raw DDI information into the proper "loop scope" property of the Eno model.
 * */
@Slf4j
public class DDIResolveLoopsScope implements ProcessingStep<EnoQuestionnaire> {

    private EnoQuestionnaire enoQuestionnaire;

    /** Fills the "loop scope" in each loop of the eno questionnaire given,
     * using the "loop items" list mapped from DDI.
     * @param enoQuestionnaire Eno questionnaire mapped from a DDI.
     * */
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        for (Loop loop : enoQuestionnaire.getLoops()) {
            resolveLoopScope(loop);
        }
    }

    private void resolveLoopScope(Loop loop) {
        for (ItemReference itemReference : loop.getLoopItems()) {
            resolveStructure(loop, itemReference);
        }
    }

    /**
     * Iterates on the filter item references to resolve the loop object scope.
     * @param filter A filter object, its "filter items" are read.
     * @param loop The loop whose scope is being resolved.
     */
    private void resolveScopeFrom(Filter filter, Loop loop) {
        for (ItemReference filterItem : filter.getFilterItems()) {
            resolveStructure(loop, filterItem);
        }
    }

    private void resolveStructure(Loop loop, ItemReference itemReference) {
        switch (itemReference.getType()) {
            case SEQUENCE, SUBSEQUENCE ->
                    loop.getLoopScope().add(StructureItemReference.from(itemReference));
            case QUESTION ->
                    throw new MappingException(String.format(
                            "%s has the question of id '%s' in its scope. " +
                                    "The scope of a loop should be either sequence(s) or subsequence(s).",
                            loop, itemReference.getId()));
            case LOOP ->
                    throw new UnsupportedOperationException("Nested loops are not supported.");
            case FILTER -> { // (filter inside loop)
                Filter filter = (Filter) enoQuestionnaire.get(itemReference.getId());
                resolveScopeFrom(filter, loop);
            }
            case CONTROL, DECLARATION ->
                    log.debug("Control and declaration are ignored while resolving filter scope.");
        }
    }

}
