package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static fr.insee.lunatic.model.flat.ComponentTypeEnum.*;
import static org.junit.jupiter.api.Assertions.*;

class LunaticAddFilterDescriptionTest {


        /* Note: cases to handle:
        - filter on sequence
        - filter on subsequence
        - filter on question
        - filter in a loop
        - make sure occurrence filters does not generate filter description components
         */

    @Test
    void unitTest_sequence() {
        // Given
        Filter enoFilter = new Filter();
        enoFilter.setId("filter-id");
        enoFilter.getFilterScope().add(StructureItemReference.builder().id("sequence-id").build());
        enoFilter.setDescription("Foo filter description");
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Sequence lunaticSequence = new Sequence();
        lunaticSequence.setId("sequence-id");
        lunaticQuestionnaire.getComponents().add(lunaticSequence);

        // When
        new LunaticAddFilterDescription(List.of(enoFilter)).apply(lunaticQuestionnaire);

        // Then
        FilterDescription filterDescription = assertInstanceOf(FilterDescription.class,
                lunaticQuestionnaire.getComponents().getFirst());
        assertEquals("filter-id-description", filterDescription.getId());
        assertEquals("Foo filter description", filterDescription.getLabel().getValue());
    }

    @Test
    void unitTest_subsequence() {
        // Given
        Filter enoFilter = new Filter();
        enoFilter.setId("filter-id");
        enoFilter.getFilterScope().add(StructureItemReference.builder().id("subsequence-id").build());
        enoFilter.setDescription("Foo filter description");
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Subsequence lunaticSubsequence = new Subsequence();
        lunaticSubsequence.setId("subsequence-id");
        lunaticQuestionnaire.getComponents().add(lunaticSubsequence);

        // When
        new LunaticAddFilterDescription(List.of(enoFilter)).apply(lunaticQuestionnaire);

        // Then
        FilterDescription filterDescription = assertInstanceOf(FilterDescription.class,
                lunaticQuestionnaire.getComponents().getFirst());
        assertEquals("filter-id-description", filterDescription.getId());
        assertEquals("Foo filter description", filterDescription.getLabel().getValue());
    }

    @Test
    void unitTest_question() {
        // Given
        Filter enoFilter = new Filter();
        enoFilter.setId("filter-id");
        enoFilter.getFilterScope().add(StructureItemReference.builder().id("question-id").build());
        enoFilter.setDescription("Foo filter description");
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Question lunaticQuestion = new Question();
        lunaticQuestion.setId("question-id");
        lunaticQuestionnaire.getComponents().add(lunaticQuestion);

        // When
        new LunaticAddFilterDescription(List.of(enoFilter)).apply(lunaticQuestionnaire);

        // Then
        FilterDescription filterDescription = assertInstanceOf(FilterDescription.class,
                lunaticQuestionnaire.getComponents().getFirst());
        assertEquals("filter-id-description", filterDescription.getId());
        assertEquals("Foo filter description", filterDescription.getLabel().getValue());
    }

    // Question: what if there is several filters that start on the same component?
    // (if it is possible)

    @Test
    void unitTest_loop() {
        // Given
        Filter enoFilter = new Filter();
        enoFilter.setId("filter-id");
        enoFilter.getFilterScope().add(StructureItemReference.builder().id("sequence-id").build());
        enoFilter.setDescription("Foo filter description");
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Loop lunaticLoop = new Loop();
        Sequence lunaticSequence = new Sequence();
        lunaticSequence.setId("sequence-id");
        lunaticLoop.getComponents().add(lunaticSequence);
        lunaticQuestionnaire.getComponents().add(lunaticLoop);

        // When
        new LunaticAddFilterDescription(List.of(enoFilter)).apply(lunaticQuestionnaire);

        // Then
        FilterDescription filterDescription = assertInstanceOf(FilterDescription.class,
                lunaticLoop.getComponents().getFirst());
        assertEquals("filter-id-description", filterDescription.getId());
        assertEquals("Foo filter description", filterDescription.getLabel().getValue());
    }

    @Test
    void unitTest_mappingError() {
        // Given
        Filter enoFilter = new Filter();
        enoFilter.setId("filter-id");
        enoFilter.getFilterScope().add(StructureItemReference.builder().id("component-id").build());
        enoFilter.setDescription("Foo filter description");
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();

        // When + Then
        LunaticAddFilterDescription processing = new LunaticAddFilterDescription(List.of(enoFilter));
        assertThrows(MappingException.class, () -> processing.apply(lunaticQuestionnaire));
    }

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    @Test
    void integrationTest_fromPoguesDDI() throws ParsingException {
        // Given + When
        EnoParameters enoParameters = EnoParameters.of(EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        Questionnaire lunaticQuestionnaire = PoguesDDIToLunatic.fromInputStreams(
                classLoader.getResourceAsStream("integration/pogues/pogues-filter-description.json"),
                classLoader.getResourceAsStream("integration/ddi/ddi-filter-description.xml"))
                .transform(enoParameters);
        //
        assertEquals(
                List.of(
                        SEQUENCE, QUESTION, QUESTION,
                        SEQUENCE, FILTER_DESCRIPTION, QUESTION, FILTER_DESCRIPTION, QUESTION, QUESTION,
                        SEQUENCE),
                lunaticQuestionnaire.getComponents().stream().map(ComponentType::getComponentType).toList()
        );
        FilterDescription filterDescription1 = assertInstanceOf(FilterDescription.class,
                lunaticQuestionnaire.getComponents().get(4));
        FilterDescription filterDescription2 = assertInstanceOf(FilterDescription.class,
                lunaticQuestionnaire.getComponents().get(6));
        assertEquals("Filter for questions 1 to 3", filterDescription1.getLabel().getValue());
        assertEquals("Filter for question 2", filterDescription2.getLabel().getValue());
        assertEquals(LabelTypeEnum.TXT, filterDescription1.getLabel().getType());
        assertEquals(LabelTypeEnum.TXT, filterDescription2.getLabel().getType());
    }

    @ParameterizedTest
    @ValueSource(strings = {"loop-except", "roundabout-except"})
    void integrationTest_loopWithExcept(String classifier) throws ParsingException {
        // Given + When
        EnoParameters enoParameters = EnoParameters.of(EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        Questionnaire lunaticQuestionnaire = PoguesDDIToLunatic.fromInputStreams(
                        classLoader.getResourceAsStream("integration/pogues/pogues-" + classifier + ".json"),
                        classLoader.getResourceAsStream("integration/ddi/ddi-" + classifier + ".xml"))
                .transform(enoParameters);
        // Then
        // No filter description neither in questionnaire's components...
        assertTrue(lunaticQuestionnaire.getComponents().stream().noneMatch(FilterDescription.class::isInstance));
        // ... nor in any loop.
        lunaticQuestionnaire.getComponents().stream().filter(Loop.class::isInstance).map(Loop.class::cast).forEach(lunaticLoop ->
                assertTrue(lunaticLoop.getComponents().stream().noneMatch(FilterDescription.class::isInstance)));
    }

}
