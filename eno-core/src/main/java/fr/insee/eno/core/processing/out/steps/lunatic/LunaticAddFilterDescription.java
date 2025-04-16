package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.FilterDescription;
import fr.insee.lunatic.model.flat.LabelType;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;

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
        enoFilters.forEach(enoFilter -> {
            // TODO: manage case when the filter starts on a loop component
            // TODO: manage 'special' filter objects (if any)
            int index = findStartIndex(enoFilter, lunaticQuestionnaire);
            FilterDescription filterDescriptionComponent = generateFilterDescriptionComponent(enoFilter);
            lunaticQuestionnaire.getComponents().add(index, filterDescriptionComponent);
        });
    }

    private int findStartIndex(Filter enoFilter, Questionnaire lunaticQuestionnaire) {
        // TODO
        return -1;
    }

    private FilterDescription generateFilterDescriptionComponent(Filter enoFilter) {
        FilterDescription lunaticFilterDescription = new FilterDescription();
        lunaticFilterDescription.setId(enoFilter.getId() + FILTER_DESCRIPTION_ID_SUFFIX);
        // Note: page number is added by the pagination processing step
        lunaticFilterDescription.setLabel(new LabelType());
        lunaticFilterDescription.getLabel().setValue(enoFilter.getDescription());
        lunaticFilterDescription.getLabel().setType(LabelTypeEnum.TXT);
        return lunaticFilterDescription;
    }

}
