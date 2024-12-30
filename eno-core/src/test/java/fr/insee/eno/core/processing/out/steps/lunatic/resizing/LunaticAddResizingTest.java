package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticLoopResolution;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSortComponents;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.ResizingPairwiseEntry;
import fr.insee.lunatic.model.flat.ResizingType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LunaticAddResizingTest {

    private ResizingType resizingType;

    @BeforeAll
    void init() throws DDIParsingException {

        // Given
        EnoQuestionnaire enoQuestionnaire = new DDIToEno().transform(
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
        resizingType = lunaticQuestionnaire.getResizing();
        // -> tests
    }

    @Test
    void resizingEntriesCount() {
        assertEquals(3, resizingType.countResizingEntries());
    }

    @Test
    void loopResizing() {
        //
        assertEquals("nvl(NUMBER, 1)", resizingType.getResizingEntry("NUMBER").getSize());
        assertThat(resizingType.getResizingEntry("NUMBER").getVariables())
                .containsExactlyInAnyOrderElementsOf(List.of("Q2", "PAIRWISE_SOURCE"));
        //
        assertEquals("count(Q2)", resizingType.getResizingEntry("Q2").getSize());
        assertEquals(List.of("Q3"), resizingType.getResizingEntry("Q2").getVariables());
    }

    @Test
    void pairwiseResizing() {
        //
        ResizingPairwiseEntry resizingPairwiseEntry = assertInstanceOf(ResizingPairwiseEntry.class,
                resizingType.getResizingEntry("PAIRWISE_SOURCE"));
        assertEquals("count(PAIRWISE_SOURCE)", resizingPairwiseEntry.getSizeForLinksVariables().getXAxisSize());
        assertEquals("count(PAIRWISE_SOURCE)", resizingPairwiseEntry.getSizeForLinksVariables().getYAxisSize());
        assertEquals(List.of("LINKS"), resizingPairwiseEntry.getLinksVariables());
    }

}
