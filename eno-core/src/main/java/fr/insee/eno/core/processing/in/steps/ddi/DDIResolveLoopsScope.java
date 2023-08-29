package fr.insee.eno.core.processing.in.steps.ddi;

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

    /**
     * Iterates on the sub-loop item references to resolve the loop object scope.
     * @param subLoop A loop object (unchanged), its "loop items" are read.
     * @param loop The loop whose scope is being resolved.
     * */
    private void resolveScopeFrom(Loop subLoop, Loop loop) {
        for (ItemReference loopItem : subLoop.getLoopItems()) {
            resolveStructure(loop, loopItem);
        }
    }

    private void resolveStructure(Loop loop, ItemReference itemReference) {
        switch (itemReference.getType()) {
            case SEQUENCE, SUBSEQUENCE, QUESTION ->
                    loop.getLoopScope().add(StructureItemReference.from(itemReference));
            case LOOP -> { // (nested loops)
                log.warn("Nested loops is not completely supported for now."); // (supported here but not everywhere)
                Loop subLoop = (Loop) enoQuestionnaire.get(itemReference.getId());
                resolveScopeFrom(subLoop, loop);
            }
            case FILTER -> { // (filter inside loop)
                Filter filter = (Filter) enoQuestionnaire.get(itemReference.getId());
                resolveScopeFrom(filter, loop);
            }
            case CONTROL, DECLARATION ->
                    log.debug("Control and declaration are ignored while resolving filter scope.");
        }
    }

}
