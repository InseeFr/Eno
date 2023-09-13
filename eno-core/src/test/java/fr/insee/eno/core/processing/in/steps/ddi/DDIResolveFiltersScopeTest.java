package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.sequence.StructureItemReference.StructureItemType;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DDIResolveFiltersScopeTest {

    /*
    Note: these tests are using the fact that filters are well-ordered in DDI.
    (There is no easy way to find a specific filter otherwise.)
     */

    @Nested
    @DisplayName("Simple filters")
    class IntegrationsTest1 {

        static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapTestQuestionnaire() throws DDIParsingException {
            // Given
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIDeserializer.deserialize(DDIResolveFiltersScopeTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-filters-simple.xml")),
                    enoQuestionnaire);
            // When
            DDIResolveFiltersScope processing = new DDIResolveFiltersScope();
            processing.apply(enoQuestionnaire);
            // Then
            // -> test methods
        }

        @Test
        @DisplayName("We should find 3 filters")
        void filtersCount() {
            assertEquals(3, enoQuestionnaire.getFilters().size());
        }

        @Test
        @DisplayName("Filter on sequence")
        void simpleFilter_sequence() {
            Filter filter = enoQuestionnaire.getFilters().get(0);
            assertEquals(1, filter.getFilterScope().size());
            assertEquals(StructureItemType.SEQUENCE, filter.getFilterScope().get(0).getType());
        }

        @Test
        @DisplayName("Filter on subsequence")
        void simpleFilter_subsequence() {
            Filter filter = enoQuestionnaire.getFilters().get(1);
            assertEquals(1, filter.getFilterScope().size());
            assertEquals(StructureItemType.SUBSEQUENCE, filter.getFilterScope().get(0).getType());
        }

        @Test
        @DisplayName("Filter on question")
        void simpleFilter_question() {
            Filter filter = enoQuestionnaire.getFilters().get(2);
            assertEquals(1, filter.getFilterScope().size());
            assertEquals(StructureItemType.QUESTION, filter.getFilterScope().get(0).getType());
        }

    }

    @Nested
    @DisplayName("Filters on several items")
    class IntegrationsTest2 {

        static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapTestQuestionnaire() throws DDIParsingException {
            // Given
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIDeserializer.deserialize(DDIResolveFiltersScopeTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-filters-extended.xml")),
                    enoQuestionnaire);
            // When
            DDIResolveFiltersScope processing = new DDIResolveFiltersScope();
            processing.apply(enoQuestionnaire);
            // Then
            // -> test methods
        }

        @Test
        @DisplayName("We should find 3 filters")
        void filtersCount() {
            assertEquals(3, enoQuestionnaire.getFilters().size());
        }

        @Test
        @DisplayName("Filter on sequences")
        void extendedFilter_sequence() {
            Filter filter = enoQuestionnaire.getFilters().get(0);
            assertEquals(3, filter.getFilterScope().size());
            assertEquals(StructureItemType.SEQUENCE, filter.getFilterScope().get(0).getType());
            assertEquals(StructureItemType.SEQUENCE, filter.getFilterScope().get(1).getType());
            assertEquals(StructureItemType.SEQUENCE, filter.getFilterScope().get(2).getType());
        }

        @Test
        @DisplayName("Filter on subsequences")
        void extendedFilter_subsequence() {
            Filter filter = enoQuestionnaire.getFilters().get(1);
            assertEquals(3, filter.getFilterScope().size());
            assertEquals(StructureItemType.SUBSEQUENCE, filter.getFilterScope().get(0).getType());
            assertEquals(StructureItemType.SUBSEQUENCE, filter.getFilterScope().get(1).getType());
            assertEquals(StructureItemType.SUBSEQUENCE, filter.getFilterScope().get(2).getType());
        }

        @Test
        @DisplayName("Filter on questions")
        void extendedFilter_question() {
            Filter filter = enoQuestionnaire.getFilters().get(2);
            assertEquals(3, filter.getFilterScope().size());
            assertEquals(StructureItemType.QUESTION, filter.getFilterScope().get(0).getType());
            assertEquals(StructureItemType.QUESTION, filter.getFilterScope().get(1).getType());
            assertEquals(StructureItemType.QUESTION, filter.getFilterScope().get(2).getType());
        }

    }

    @Nested
    @DisplayName("Nested filters")
    class IntegrationsTest3 {

        static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapTestQuestionnaire() throws DDIParsingException {
            // Given
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIDeserializer.deserialize(DDIResolveFiltersScopeTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-filters-nested.xml")),
                    enoQuestionnaire);
            // When
            DDIResolveFiltersScope processing = new DDIResolveFiltersScope();
            processing.apply(enoQuestionnaire);
            // Then
            // -> test methods
        }

        @Test
        @DisplayName("We should find 17 filters")
        void filtersCount() {
            assertEquals(17, enoQuestionnaire.getFilters().size());
        }

        @Test
        @DisplayName("Sequence and question")
        void nestedFilters01() {
            //
            Filter sequenceFilter = enoQuestionnaire.getFilters().get(0);
            assertEquals(1, sequenceFilter.getFilterScope().size());
            assertEquals(StructureItemType.SEQUENCE, sequenceFilter.getFilterScope().get(0).getType());
            //
            Filter questionFilter = enoQuestionnaire.getFilters().get(1);
            assertEquals(1, questionFilter.getFilterScope().size());
            assertEquals(StructureItemType.QUESTION, questionFilter.getFilterScope().get(0).getType());
        }

        /** Nested filters on three consecutive questions.
         * Two cases are identical here (differences on the expressions that is not the focus here) */
        @ParameterizedTest
        @ValueSource(ints = {2, 5})
        @DisplayName("Questions")
        void nestedFilters02(int index) {
            //
            Filter filter1 = enoQuestionnaire.getFilters().get(index);
            assertEquals(3, filter1.getFilterScope().size());
            assertEquals(StructureItemType.QUESTION, filter1.getFilterScope().get(0).getType());
            assertEquals(StructureItemType.QUESTION, filter1.getFilterScope().get(1).getType());
            assertEquals(StructureItemType.QUESTION, filter1.getFilterScope().get(2).getType());
            //
            Filter filter2 = enoQuestionnaire.getFilters().get(index+1);
            assertEquals(2, filter2.getFilterScope().size());
            assertEquals(StructureItemType.QUESTION, filter2.getFilterScope().get(0).getType());
            assertEquals(StructureItemType.QUESTION, filter2.getFilterScope().get(1).getType());
            //
            Filter filter3 = enoQuestionnaire.getFilters().get(index+2);
            assertEquals(1, filter3.getFilterScope().size());
            assertEquals(StructureItemType.QUESTION, filter3.getFilterScope().get(0).getType());
        }

        @Test
        @DisplayName("Sequence (containing questions and subsequences)")
        void nestedFilters03() {
            Filter filer = enoQuestionnaire.getFilters().get(8);
            assertEquals(1, filer.getFilterScope().size());
            assertEquals(StructureItemType.SEQUENCE, filer.getFilterScope().get(0).getType());
        }

        /** Filter on a subsequence nesting a filter on one of its questions.
         * Two cases are identical here (differences on the expressions that is not the focus here) */
        @ParameterizedTest
        @ValueSource(ints = {9, 11})
        @DisplayName("Subsequence and question")
        void nestedFilters04(int index) {
            //
            Filter subsequenceFilter = enoQuestionnaire.getFilters().get(index);
            assertEquals(1, subsequenceFilter.getFilterScope().size());
            assertEquals(StructureItemType.SUBSEQUENCE, subsequenceFilter.getFilterScope().get(0).getType());
            //
            Filter questionFilter = enoQuestionnaire.getFilters().get(index+1);
            assertEquals(1, questionFilter.getFilterScope().size());
            assertEquals(StructureItemType.QUESTION, questionFilter.getFilterScope().get(0).getType());
        }

        @Test
        @DisplayName("Subsequence and subsequence")
        void nestedFilters05() {
            //
            Filter filter1 = enoQuestionnaire.getFilters().get(13);
            assertEquals(2, filter1.getFilterScope().size());
            assertEquals(StructureItemType.SUBSEQUENCE, filter1.getFilterScope().get(0).getType());
            assertEquals(StructureItemType.SUBSEQUENCE, filter1.getFilterScope().get(1).getType());
            //
            Filter filter2 = enoQuestionnaire.getFilters().get(14);
            assertEquals(1, filter2.getFilterScope().size());
            assertEquals(StructureItemType.SUBSEQUENCE, filter2.getFilterScope().get(0).getType());
        }

        @Test
        @DisplayName("Sequence and sequence")
        void nestedFilters06() {
            //
            Filter filter1 = enoQuestionnaire.getFilters().get(15);
            assertEquals(2, filter1.getFilterScope().size());
            assertEquals(StructureItemType.SEQUENCE, filter1.getFilterScope().get(0).getType());
            assertEquals(StructureItemType.SEQUENCE, filter1.getFilterScope().get(1).getType());
            //
            Filter filter2 = enoQuestionnaire.getFilters().get(16);
            assertEquals(1, filter2.getFilterScope().size());
            assertEquals(StructureItemType.SEQUENCE, filter2.getFilterScope().get(0).getType());
        }

    }

}
