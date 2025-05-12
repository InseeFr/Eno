package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

import java.util.List;

/** When in "management mode", filters of the Lunatic questionnaire are disabled.
 * Thus, "filter description" components are added to visually represent which components are concerned by filters.
 * Note: This processing must be called BEFORE the pagination step.
 * @see fr.insee.eno.core.processing.out.steps.lunatic.pagination.LunaticAddPageNumbers */
public class LunaticAddFilterDescription implements ProcessingStep<Questionnaire> {

    private static final String FILTER_DESCRIPTION_ID_SUFFIX = "-description";

    private final List<Filter> enoFilters;

    /**
     * @param enoFilters List of Eno filter objects to generate descriptions for.
     */
    public LunaticAddFilterDescription(List<Filter> enoFilters) {
        this.enoFilters = enoFilters;
    }

    /** Inserts a "filter description" component at the start of each filter
     * (just before the first filtered component). */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        enoFilters.stream()
                .filter(enoFilter -> enoFilter.getDescription() != null && !enoFilter.getDescription().isEmpty())
                    // No description => no filter description component
                .forEach(enoFilter -> {

            // Search in questionnaire-level components
            if (insertFilterDescription(enoFilter, lunaticQuestionnaire.getComponents()))
                return;

            // If not found, search in loops
            List<Loop> lunaticLoops = lunaticQuestionnaire.getComponents().stream()
                    .filter(Loop.class::isInstance).map(Loop.class::cast).toList();
            for (Loop lunaticLoop : lunaticLoops) {
                if (insertFilterDescription(enoFilter, lunaticLoop.getComponents()))
                    return;
            }

            // If still not found, something got wrong
            throw new MappingException("Cannot find first element of filter " + enoFilter);
        });
    }

    private boolean insertFilterDescription(Filter enoFilter, List<ComponentType> lunaticComponents) {
        String startId = enoFilter.getFilterScope().getFirst().getId();
        int index = lunaticComponents.stream().map(ComponentType::getId).toList().indexOf(startId);
        if (index != -1) {
            FilterDescription filterDescriptionComponent = generateFilterDescriptionComponent(
                    enoFilter, lunaticComponents.get(index).getConditionFilter());
            lunaticComponents.add(index, filterDescriptionComponent);
            return true;
        }
        return false;
    }

    /**
     * Generates the filter description component from given data.
     * @param enoFilter Eno filter object (contains the user "description" of the filter).
     * @param lunaticComponentFilter Filtered applied on the Lunatic component before which the filter description
     *                               should be inserted.
     * @return Lunatic filter description component.
     */
    private FilterDescription generateFilterDescriptionComponent(
            Filter enoFilter, ConditionFilterType lunaticComponentFilter) {
        FilterDescription lunaticFilterDescription = new FilterDescription();
        lunaticFilterDescription.setId(enoFilter.getId() + FILTER_DESCRIPTION_ID_SUFFIX);
        // Note: page number is added by the pagination processing step
        lunaticFilterDescription.setLabel(new LabelType());
        lunaticFilterDescription.getLabel().setValue(enoFilter.getDescription());
        lunaticFilterDescription.getLabel().setType(LabelTypeEnum.TXT);
        // A filter description share the same filter as the component it is associated with
        lunaticFilterDescription.setConditionFilter(lunaticComponentFilter);
        return lunaticFilterDescription;
    }

}
