package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * In DDI, row-level controls (for dynamic tables lines or roundabout occurrences) are control construct references
 * listed in the control construct references list in loop objects (more precisely in the virtual sequence that is
 * associated with the loop).
 * This processing step identifies these controls and marks them as "row" controls.
 */
public class DDIMarkRowControls implements ProcessingStep<EnoQuestionnaire> {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        // index controls by id
        Map<String, Control> controls = mapQuestionnaireControls(enoQuestionnaire);
        // iterate on each loop controls and mark the as "row" controls
        enoQuestionnaire.getLoops().forEach(loop ->
                markControlsAsRow(getLoopControlReferences(loop, enoQuestionnaire), controls));
    }

    /**
     * Method to index questionnaire's controls to get them easily when needed.
     * @param enoQuestionnaire Eno questionnaire.
     * @return A map of controls indexed by id.
     */
    static Map<String, Control> mapQuestionnaireControls(EnoQuestionnaire enoQuestionnaire) {
        return enoQuestionnaire.getControls().stream().collect(Collectors.toMap(Control::getId, control -> control));
    }

    /**
     * Direct search method for filters, indexing seems not useful.
     * @param enoQuestionnaire Eno questionnaire.
     * @param filterId Identifier of a filter.
     * @return The corresponding filter object.
     * @throws MappingException if filter object is not found.
     */
    private static Filter getFilterById(EnoQuestionnaire enoQuestionnaire, String filterId) {
        return enoQuestionnaire.getFilters().stream()
                .filter(filter -> filterId.equals(filter.getId()))
                .findAny()
                .orElseThrow(() -> new MappingException("Didn't find filter object with id " + filterId));
    }

    private static void markControlsAsRow(Stream<ItemReference> controlReferences, Map<String, Control> controls) {
        controlReferences.forEach(itemReference -> controls.get(itemReference.getId()).setContext(Control.Context.ROW));
    }

    static Stream<ItemReference> getLoopControlReferences(Loop loop, EnoQuestionnaire enoQuestionnaire) {
        // if the loop contains an occurrence filter, return the control references of that filter
        Optional<ItemReference> filterReference = loop.getLoopItems().stream()
                .filter(itemReference -> ItemReference.ItemType.FILTER.equals(itemReference.getType()))
                .findAny();
        if (filterReference.isPresent()) {
            Filter filter = getFilterById(enoQuestionnaire, filterReference.get().getId());
            return controlReferencesStream(filter);
        }
        // otherwise return the loop control references
        return controlReferencesStream(loop);
    }

    // Note: we could add an interface over loop and filter for the items/scope properties

    private static Stream<ItemReference> controlReferencesStream(Loop loop) {
        return loop.getLoopItems().stream()
                .filter(itemReference -> ItemReference.ItemType.CONTROL.equals(itemReference.getType()));
    }

    private static Stream<ItemReference> controlReferencesStream(Filter filter) {
        return filter.getFilterItems().stream()
                .filter(itemReference -> ItemReference.ItemType.CONTROL.equals(itemReference.getType()));
    }

}
