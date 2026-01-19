package fr.insee.eno.core.processing.out.steps.lunatic.loop;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.utils.vtl.VtlSyntaxUtils;
import fr.insee.lunatic.model.flat.*;

import java.util.List;


/**
 * {@code LunaticLoopFilter} is a utility class that handles the calculation of filters
 * applicable to loops (or roundabouts) in a questionnaire, considering the specific rules
 * of the Lunatic framework.
 *
 * <p>This class addresses a complex issue related to the combination of filters and loops:
 * filters must be correctly applied to loops to avoid scope errors, especially when VTL expressions
 * are only valid at the individual level or within a subset of the loop. The implemented logic ensures
 * that only filters strictly included within the loop's scope are considered.</p>
 *
 * <h2>General Algorithm</h2>
 * <ol>
 *   <li>For each filter in the questionnaire:
 *     <ul>
 *       <li>Check if the start and end elements of the loop are strictly included within the filter's scope
 *           (without coinciding with the filter's boundaries).</li>
 *       <li>If so, add this filter to the list of filters applicable to the loop.</li>
 *     </ul>
 *   </li>
 *   <li>If no filters are applicable, return a default VTL condition ({@code true}).</li>
 *   <li>Otherwise, combine the expressions of the applicable filters using AND logic.</li>
 * </ol>
 *
 * <h2>Special Cases</h2>
 * <ul>
 *   <li>If the start of the loop coincides with the start of the filter, the filter is not applied
 *       directly to the loop, but only to its child elements.</li>
 *   <li>Occurrence filters (whose ID matches the loop's filter ID) are excluded.</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * <pre>
 *   Loop enoLoop = ...; // Loop to process
 *   EnoQuestionnaire enoQuestionnaire = ...; // Questionnaire containing the filters
 *   ConditionFilterType condition = LunaticLoopFilter.computeConditionFilter(enoLoop, enoQuestionnaire);
 * </pre>
 */
public class LunaticLoopFilter {

    private LunaticLoopFilter(){
        throw new IllegalStateException("Utility class");
    }


    /**
     * Computes the filter condition to apply to a given loop, based on the filters
     * defined in the questionnaire.
     *
     * <p>This method:
     * <ol>
     *   <li>Filters the list of questionnaire filters to retain only those strictly included
     *       within the loop's scope.</li>
     *   <li>If no filters are applicable, returns a default VTL condition ({@code true}).</li>
     *   <li>Otherwise, combines the expressions of the applicable filters using AND logic.</li>
     * </ol>
     *
     * @param enoLoop The loop for which to compute the filter condition.
     * @param enoQuestionnaire The questionnaire containing the filters to evaluate.
     * @return An instance of {@code ConditionFilterType} representing the combined filter condition,
     *         or a default condition if no filters are applicable.
     */
    public static ConditionFilterType computeConditionFilter(Loop enoLoop, EnoQuestionnaire enoQuestionnaire) {

        List<Filter> filtersForLoop = enoQuestionnaire.getFilters().stream()
                .filter(filter -> isFilterIncludingLoop(filter, enoLoop))
                .toList();

        if(filtersForLoop.isEmpty()) {
            ConditionFilterType defaultConditionFilterType = new ConditionFilterType();
            defaultConditionFilterType.setValue(VtlSyntaxUtils.VTL_TRUE);
            defaultConditionFilterType.setType(LabelTypeEnum.VTL);
            return defaultConditionFilterType;
        }
        return computeLoopFilterExpression(filtersForLoop);
    }

    /**
     * Checks if a given filter strictly includes a loop.
     *
     * <p>A filter strictly includes a loop if:
     * <ul>
     *   <li>The start and end elements of the loop are present within the filter's scope.</li>
     *   <li>The start and end elements of the loop do not coincide with the filter's boundaries.</li>
     *   <li>The filter is not an occurrence filter (whose ID matches the loop's filter ID).</li>
     * </ul>
     *
     * @param filter The filter to check.
     * @param enoLoop The loop to verify.
     * @return {@code true} if the filter strictly includes the loop, {@code false} otherwise.
     */
    private static boolean isFilterIncludingLoop(Filter filter, Loop enoLoop){
        String occurrenceFilterId = enoLoop.getOccurrenceFilterId();


        // do not include occurrence filter
        if(occurrenceFilterId != null && occurrenceFilterId.equals(filter.getId())) return false;
        String startLoopElementId = enoLoop.getLoopScope().getFirst().getId();
        String endLoopElementId = enoLoop.getLoopScope().getLast().getId();

        String startFilterElementId = filter.getFilterScope().getFirst().getId();
        String endFilterElementId = filter.getFilterScope().getLast().getId();

        // prevent scope calculating error
        if(startLoopElementId.equals(startFilterElementId) || endLoopElementId.equals(endFilterElementId)) return false;

        boolean isStartOfLoopInsideFilter = false;
        boolean isEndOfLoopInsideFilter = false;
        for(StructureItemReference structureItemReference : filter.getFilterScope()){
            String referenceId = structureItemReference.getId();
            if(startLoopElementId.equals(referenceId)) isStartOfLoopInsideFilter = true;
            if(endLoopElementId.equals(referenceId)) isEndOfLoopInsideFilter = true;
            if(isStartOfLoopInsideFilter && isEndOfLoopInsideFilter) return true;
        }
        return false;
    }


    /**
     * Combines the VTL expressions of the filters applicable to a loop into a single expression,
     * using AND logic.
     *
     * <p>This method:
     * <ol>
     *   <li>Concatenates the VTL expressions of the filters with the {@code AND} operator.</li>
     *   <li>Extracts the binding dependencies (VTL variables) from the filters and adds them to the resulting condition.</li>
     * </ol>
     *
     * @param loopStructureFilters List of filters applicable to the loop.
     * @return An instance of {@code ConditionFilterType} containing the combined VTL expression
     *         and its binding dependencies.
     */
    private static ConditionFilterType computeLoopFilterExpression(List<Filter> loopStructureFilters) {
        ConditionFilterType loopFilter = new ConditionFilterType();
        String expression = VtlSyntaxUtils.joinByANDLogicExpression(
                loopStructureFilters.stream()
                        .map(Filter::getExpression)
                        .map(CalculatedExpression::getValue)
                        .toList()
        );
        loopFilter.setValue(expression);
        loopFilter.setType(LabelTypeEnum.VTL);
        List<String> bindingDependencies = loopStructureFilters.stream()
                .flatMap(filter -> filter.getExpression().getBindingReferences().stream().map(BindingReference::getVariableName))
                .distinct()
                .toList();
        loopFilter.setBindingDependencies(bindingDependencies);
        return loopFilter;
    }

}
