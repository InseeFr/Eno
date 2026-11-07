package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LunaticFilterVariablesDependenciesIntegrationTest {

    private static final String CALCULATED_POGUES = "integration/pogues/pogues-filters-calculated.json";
    private static final String CALCULATED_DDI = "integration/ddi/ddi-filters-calculated.xml";
    private static final String LOOP_POGUES = "integration/pogues/pogues-loop-filter.json";
    private static final String LOOP_DDI = "integration/ddi/ddi-loop-filter.xml";

    private final ClassLoader classLoader = this.getClass().getClassLoader();
    private final EnoParameters enoParameters =
            EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);

    // ----- Helpers -----

    private Questionnaire map(String poguesResource, String ddiResource) throws ParsingException {
        return PoguesDDIToLunatic
                .fromInputStreams(
                        classLoader.getResourceAsStream(poguesResource),
                        classLoader.getResourceAsStream(ddiResource))
                .transform(enoParameters);
    }

    /** All condition filters of the final questionnaire, including those held by Question components. */
    private static List<ConditionFilterType> allFilters(Questionnaire questionnaire) {
        List<ConditionFilterType> filters = new ArrayList<>();
        collectFilters(questionnaire.getComponents(), filters);
        return filters;
    }

    private static void collectFilters(List<ComponentType> components, List<ConditionFilterType> filters) {
        for (ComponentType component : components) {
            if (component.getConditionFilter() != null)
                filters.add(component.getConditionFilter());
            if (component instanceof ComponentNestingType nesting)
                collectFilters(nesting.getComponents(), filters);
            if (component instanceof Question question)
                collectFilters(question.getComponents(), filters);
        }
    }

    private static List<Loop> loops(Questionnaire questionnaire) {
        return questionnaire.getComponents().stream()
                .filter(Loop.class::isInstance).map(Loop.class::cast).toList();
    }

    /** Every non-null filter must have its variablesDependencies object with the three lists. */
    private static void assertEveryFilterHasThreeLists(Questionnaire questionnaire) {
        List<ConditionFilterType> filters = allFilters(questionnaire);
        assertFalse(filters.isEmpty());
        filters.forEach(filter -> {
            VariablesDependencies dependencies = filter.getVariablesDependencies();
            assertNotNull(dependencies, "Missing variablesDependencies on filter: " + filter.getValue());
            assertNotNull(dependencies.getCollected());
            assertNotNull(dependencies.getExternal());
            assertNotNull(dependencies.getCalculated());
        });
    }

    // ----- Questionnaire with a filter on a calculated variable -----

    @Test
    void calculatedGuardEveryFilterHasThreeLists() throws ParsingException {
        assertEveryFilterHasThreeLists(map(CALCULATED_POGUES, CALCULATED_DDI));
    }

    @Test
    void calculatedSequenceFilterNoTransitivity() throws Exception {
        Questionnaire questionnaire = map(CALCULATED_POGUES, CALCULATED_DDI);
        ConditionFilterType filter = allFilters(questionnaire).stream()
                .filter(f -> f.getValue() != null && f.getValue().contains("SUM_Q11_Q12"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No filter containing SUM_Q11_Q12"));
        VariablesDependencies dependencies = filter.getVariablesDependencies();

        assertEquals(List.of(), dependencies.getCollected());
        assertEquals(List.of(), dependencies.getExternal());
        // Q11 and Q12 must not appear: SUM_Q11_Q12 is not "unfolded"
        assertEquals(List.of("SUM_Q11_Q12"), dependencies.getCalculated());
    }

    @Test
    void calculatedFiltersWithoutVariablesHaveThreeEmptyLists() throws Exception {
        Questionnaire questionnaire = map(CALCULATED_POGUES, CALCULATED_DDI);
        allFilters(questionnaire).stream()
                .filter(f -> "true".equals(f.getValue()))
                .forEach(f -> {
                    assertEquals(List.of(), f.getVariablesDependencies().getCollected());
                    assertEquals(List.of(), f.getVariablesDependencies().getExternal());
                    assertEquals(List.of(), f.getVariablesDependencies().getCalculated());
                });
    }

    // ----- Questionnaire with loops -----

    @Test
    void loopsGuardEveryFilterHasThreeLists() throws ParsingException {
        assertEveryFilterHasThreeLists(map(LOOP_POGUES, LOOP_DDI));
    }

    @Test
    void loopsLoopFilterUsesItsEnclosingFilterVariables() throws Exception {
        Questionnaire questionnaire = map(LOOP_POGUES, LOOP_DDI);
        assertEquals(2, loops(questionnaire).size());
        // Exactly one loop is strictly enclosed by the "COMBIEN > 3" filter
        List<Loop> filteredLoops = loops(questionnaire).stream()
                .filter(loop -> List.of("COMBIEN").equals(
                        loop.getConditionFilter().getVariablesDependencies().getCollected()))
                .toList();
        assertEquals(1, filteredLoops.size());
        VariablesDependencies dependencies = filteredLoops.getFirst()
                .getConditionFilter().getVariablesDependencies();
        assertEquals(List.of(), dependencies.getExternal());
        assertEquals(List.of(), dependencies.getCalculated());
    }

    @Test
    void loopsLoopWithoutEnclosingFilterHasThreeEmptyLists() throws Exception {
        Questionnaire questionnaire = map(LOOP_POGUES, LOOP_DDI);
        List<Loop> unfilteredLoops = loops(questionnaire).stream()
                .filter(loop -> "true".equals(loop.getConditionFilter().getValue()))
                .toList();
        assertEquals(1, unfilteredLoops.size());
        VariablesDependencies dependencies = unfilteredLoops.getFirst()
                .getConditionFilter().getVariablesDependencies();
        assertEquals(List.of(), dependencies.getCollected());
        assertEquals(List.of(), dependencies.getExternal());
        assertEquals(List.of(), dependencies.getCalculated());
    }

    @Test
    void loopsOccurrenceFilterVariableIsNotInLoopDependencies() throws Exception {
        Questionnaire questionnaire = map(LOOP_POGUES, LOOP_DDI);
        // AGES2 appears only in the occurrence filter, which is excluded from the loop filter
        loops(questionnaire).forEach(loop -> assertFalse(
                loop.getConditionFilter().getVariablesDependencies().getCollected().contains("AGES2")));
    }

    // ----- Guard on all integration resources -----

    @ParameterizedTest
    @ValueSource(strings = {
            "filters-calculated", "filters-extended", "filters-nested", "filters-simple",
            "loop-except", "loops-extended-sequence", "loops-extended-subsequence",
            "loops-sequence", "loops-subsequence", "pairwise", "roundabout",
            "roundabout-controls", "roundabout-except", "roundabout-subsequence",
            "dynamic-table", "variables"
    })
    void guardEveryFilterHasThreeListsOnAllResources(String classifier) throws ParsingException {
        Questionnaire questionnaire = map(
                "integration/pogues/pogues-" + classifier + ".json",
                "integration/ddi/ddi-" + classifier + ".xml");
        assertEveryFilterHasThreeLists(questionnaire);
    }
}
