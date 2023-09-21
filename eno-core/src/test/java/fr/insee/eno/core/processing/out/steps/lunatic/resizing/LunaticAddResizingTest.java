package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.lunatic.LunaticResizingEntry;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairwiseEntry;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticLoopResolution;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSortComponents;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LunaticAddResizingTest {

    private static List<Object> resizingList;

    @BeforeAll
    static void init() throws DDIParsingException {

        // Given
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                LunaticAddResizingTest.class.getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-resizing.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);

        // When
        new LunaticAddResizing(enoQuestionnaire).apply(lunaticQuestionnaire);

        // Then
        resizingList = lunaticQuestionnaire.getResizing().getAny();
        // -> tests
    }

    @Test
    void resizingListIsNotEmpty() {
        assertFalse(resizingList.isEmpty());
    }

    @Test
    void loopResizing() {
        List<LunaticResizingEntry> resizingEntries = resizingList.stream()
                .filter(LunaticResizingEntry.class::isInstance)
                .map(LunaticResizingEntry.class::cast)
                .toList();
        //
        assertEquals(2, resizingEntries.size());
        //
        Optional<LunaticResizingEntry> loopResizingEntry = resizingEntries.stream()
                .filter(resizingEntry -> "NUMBER".equals(resizingEntry.getName()))
                .findAny();
        assertTrue(loopResizingEntry.isPresent());
        assertEquals("nvl(NUMBER, 1)", loopResizingEntry.get().getSize());
        assertThat(loopResizingEntry.get().getVariables()).containsExactlyInAnyOrderElementsOf(
                List.of("Q2", "PAIRWISE_SOURCE"));
        //
        Optional<LunaticResizingEntry> linkedLoopResizingEntry = resizingEntries.stream()
                .filter(resizingEntry -> "Q2".equals(resizingEntry.getName()))
                .findAny();
        assertTrue(linkedLoopResizingEntry.isPresent());
        assertEquals("count(Q2)", linkedLoopResizingEntry.get().getSize());
        assertEquals(List.of("Q3"), linkedLoopResizingEntry.get().getVariables());
    }

    @Test
    void pairwiseResizing() {
        List<LunaticResizingPairwiseEntry> resizingPairwiseEntries = resizingList.stream()
                .filter(LunaticResizingPairwiseEntry.class::isInstance)
                .map(LunaticResizingPairwiseEntry.class::cast)
                .toList();
        //
        assertEquals(1, resizingPairwiseEntries.size());
        //
        LunaticResizingPairwiseEntry pairwiseResizingEntry = resizingPairwiseEntries.get(0);
        assertEquals("PAIRWISE_SOURCE", pairwiseResizingEntry.getName());
        assertEquals(List.of("count(PAIRWISE_SOURCE)", "count(PAIRWISE_SOURCE)"),
                pairwiseResizingEntry.getSizeForLinksVariables());
        assertEquals(List.of("LINKS"), pairwiseResizingEntry.getLinksVariables());
    }

}
