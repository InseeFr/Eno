package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.ConditionFilterType;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComponentFilterTest {

    private ComponentFilter enoComponentFilter;
    private ConditionFilterType lunaticConditionFilter;

    @BeforeEach
    void createObjects() {
         enoComponentFilter = new ComponentFilter();
         lunaticConditionFilter = new ConditionFilterType();
    }

    @Test
    void defaultComponentFilter() {
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoComponentFilter, lunaticConditionFilter);
        //
        assertEquals(ComponentFilter.DEFAULT_FILTER_VALUE, lunaticConditionFilter.getValue());
        assertEquals(Constant.LUNATIC_LABEL_VTL, lunaticConditionFilter.getType());
        assertTrue(lunaticConditionFilter.getBindingDependencies().isEmpty());
    }

    @Test
    void componentFilter_withBindingReferences() {
        //
        enoComponentFilter.setValue("(FOO_VARIABLE = 1)");
        enoComponentFilter.setType(Constant.LUNATIC_LABEL_VTL);
        enoComponentFilter.getBindingReferences().add(new BindingReference("foo-id", "FOO_VARIABLE"));
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoComponentFilter, lunaticConditionFilter);
        //
        assertEquals("(FOO_VARIABLE = 1)", lunaticConditionFilter.getValue());
        assertEquals(Constant.LUNATIC_LABEL_VTL, lunaticConditionFilter.getType());
        assertEquals(1, lunaticConditionFilter.getBindingDependencies().size());
        assertEquals("FOO_VARIABLE", lunaticConditionFilter.getBindingDependencies().get(0));
    }

    @Nested
    class IntegrationTestSimple {

        private static Questionnaire lunaticQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            lunaticQuestionnaire = DDIToLunatic.transform(
                    ComponentFilterTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-filters-simple.xml"),
                    EnoParameters.of(EnoParameters.Context.DEFAULT, Format.LUNATIC, EnoParameters.ModeParameter.CAWI));
        }

        @Test
        void componentsWithDefaultFilter() {
            List.of(0, 1, 2, 3, 6, 9).forEach(index ->
                    assertEquals("true",
                            lunaticQuestionnaire.getComponents().get(index).getConditionFilter().getValue()));
        }

        @Test
        void sequenceFilter() {
            // Sequence component
            assertEquals("(Q11)", lunaticQuestionnaire.getComponents().get(4).getConditionFilter().getValue());
            // Question in the sequence
            assertEquals("(Q11)", lunaticQuestionnaire.getComponents().get(5).getConditionFilter().getValue());
        }

        @Test
        void subsequenceFilter() {
            // Subsequence component
            assertEquals("(Q12)", lunaticQuestionnaire.getComponents().get(7).getConditionFilter().getValue());
            // Question in the subsequence
            assertEquals("(Q12)", lunaticQuestionnaire.getComponents().get(8).getConditionFilter().getValue());
        }

        @Test
        void questionFilter() {
            // Question component
            assertEquals("(Q13)", lunaticQuestionnaire.getComponents().get(10).getConditionFilter().getValue());
        }

        @Test
        void filterExpressionsType() {
            lunaticQuestionnaire.getComponents().stream()
                    .map(ComponentType::getConditionFilter)
                    .forEach(conditionFilterType ->
                            assertEquals(Constant.LUNATIC_LABEL_VTL, conditionFilterType.getType()));
        }

        @Test
        void bindingDependencies() {
            //
            List.of(0, 1, 2, 3, 6, 9).forEach(index ->
                    assertTrue(lunaticQuestionnaire.getComponents().get(index).getConditionFilter()
                            .getBindingDependencies().isEmpty()));
            //
            assertEquals(List.of("Q11"),
                    lunaticQuestionnaire.getComponents().get(4).getConditionFilter().getBindingDependencies());
            assertEquals(List.of("Q11"),
                    lunaticQuestionnaire.getComponents().get(5).getConditionFilter().getBindingDependencies());
            assertEquals(List.of("Q12"),
                    lunaticQuestionnaire.getComponents().get(7).getConditionFilter().getBindingDependencies());
            assertEquals(List.of("Q12"),
                    lunaticQuestionnaire.getComponents().get(8).getConditionFilter().getBindingDependencies());
            assertEquals(List.of("Q13"),
                    lunaticQuestionnaire.getComponents().get(10).getConditionFilter().getBindingDependencies());
        }

    }

    @Nested
    class IntegrationTestExtended {

        private static Questionnaire lunaticQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            lunaticQuestionnaire = DDIToLunatic.transform(
                    ComponentFilterTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-filters-extended.xml"),
                    EnoParameters.of(EnoParameters.Context.DEFAULT, Format.LUNATIC, EnoParameters.ModeParameter.CAWI));
        }

        @Test
        void componentsWithDefaultFilter() {
            List.of(0, 1, 2, 3, 10, 17).forEach(index ->
                    assertEquals("true",
                            lunaticQuestionnaire.getComponents().get(index).getConditionFilter().getValue()));
        }

        @Test
        void filterOnThreeSequence() {
            IntStream.range(4, 10).forEach(index ->
                    assertEquals("(Q11)",
                            lunaticQuestionnaire.getComponents().get(index).getConditionFilter().getValue()));
        }

        @Test
        void filterOnThreeSubsequences() {
            IntStream.range(11, 17).forEach(index ->
                    assertEquals("(Q12)",
                            lunaticQuestionnaire.getComponents().get(index).getConditionFilter().getValue()));
        }

        @Test
        void filterOnThreeQuestions() {
            IntStream.range(18, 21).forEach(index ->
                    assertEquals("(Q13)",
                            lunaticQuestionnaire.getComponents().get(index).getConditionFilter().getValue()));
        }

    }

    @Nested
    class IntegrationTestCalculated {

        private static Questionnaire lunaticQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            lunaticQuestionnaire = DDIToLunatic.transform(
                    ComponentFilterTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-filters-calculated.xml"),
                    EnoParameters.of(EnoParameters.Context.DEFAULT, Format.LUNATIC, EnoParameters.ModeParameter.CAWI));
        }

        @ParameterizedTest
        @ValueSource(ints = {4, 5})
        void filterWithCalculatedVariable(int index) {
            ConditionFilterType conditionFilter = lunaticQuestionnaire.getComponents().get(index).getConditionFilter();
            assertEquals("(SUM_Q11_Q12 < 10)", conditionFilter.getValue());
            assertEquals(Constant.LUNATIC_LABEL_VTL, conditionFilter.getType());
            assertEquals(3, conditionFilter.getBindingDependencies().size());
            assertTrue(conditionFilter.getBindingDependencies().containsAll(List.of("SUM_Q11_Q12", "Q11", "Q12")));
        }

    }

}
