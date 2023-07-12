package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.lunatic.model.flat.ConditionFilterType;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    class IntegrationTest {

        private static Questionnaire lunaticQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            lunaticQuestionnaire = DDIToLunatic.transform(
                    ComponentFilterTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-filters-simple.xml"));
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

    }

}
