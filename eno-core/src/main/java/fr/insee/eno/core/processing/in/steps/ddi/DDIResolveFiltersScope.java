package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.processing.ProcessingStep;
import lombok.extern.slf4j.Slf4j;

/** Filter objects contain a "filter items" list, mapped from the DDI, that contain the reference of components
 * that are in the scope of the filter, in the right order.
 * This processing class converts this raw DDI information into the proper "filter scope" property of the Eno model.
 * */
@Slf4j
public class DDIResolveFiltersScope implements ProcessingStep<EnoQuestionnaire> {

    private EnoQuestionnaire enoQuestionnaire;

    /** Fills the "filter scope" in each filter of the eno questionnaire given,
     * using the "filter items" list mapped from DDI.
     * @param enoQuestionnaire Eno questionnaire mapped from a DDI.
     * */
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        for (Filter filter : enoQuestionnaire.getFilters()) {
            resolveFilterScope(filter);
        }
    }

    private void resolveFilterScope(Filter filter) {
        for (ItemReference itemReference : filter.getFilterItems()) {
            resolveStructure(filter, itemReference);
        }
    }

    /**
     * Iterates on the sub-filter item references to resolve the filter object scope.
     * @param subFilter A filter object (unchanged), its "filter items" are read.
     * @param filter The filter whose scope is being resolved.
     */
    private void resolveScopeFrom(Filter subFilter, Filter filter) {
        for (ItemReference filterItem : subFilter.getFilterItems()) {
            resolveStructure(filter, filterItem);
        }
    }

    /**
     * Iterates on the loop item references to resolve the filter object scope.
     * @param loop A loop object, its "loop items" are read.
     * @param filter The filter whose scope is being resolved.
     * */
    private void resolveScopeFrom(Loop loop, Filter filter) {
        for (ItemReference loopItem : loop.getLoopItems()) {
            resolveStructure(filter, loopItem);
        }
    }

    private void resolveStructure(Filter filter, ItemReference itemReference) {
        switch (itemReference.getType()) {
            case SEQUENCE, SUBSEQUENCE, QUESTION ->
                    filter.getFilterScope().add(StructureItemReference.from(itemReference));
            case LOOP -> {
                Loop loop = (Loop) enoQuestionnaire.get(itemReference.getId());
                resolveScopeFrom(loop, filter);
            }
            case FILTER -> { // (nested filters)
                Filter subFilter = (Filter) enoQuestionnaire.get(itemReference.getId());
                resolveScopeFrom(subFilter, filter);
            }
            case CONTROL, DECLARATION ->
                    log.debug("Control and declaration are ignored while resolving filter scope.");
        }
    }

}

