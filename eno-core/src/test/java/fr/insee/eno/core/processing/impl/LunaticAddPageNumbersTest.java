package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.Loop;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.SequenceType;
import fr.insee.lunatic.model.flat.Textarea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LunaticAddPageNumbersTest {

    private Questionnaire questionnaire;

    @BeforeEach
    void setup() {
        questionnaire = new Questionnaire();
        questionnaire.getComponents().add(new SequenceType());
        questionnaire.getComponents().add(new Textarea());
    }

    @Test
    void simplestCase_noPagination() {
        //
        new LunaticAddPageNumbers(EnoParameters.LunaticPaginationMode.NONE).apply(questionnaire);
        //
        assertNull(questionnaire.getComponents().get(0).getPage());
        assertNull(questionnaire.getComponents().get(1).getPage());
        assertNull(questionnaire.getMaxPage());
    }

    @Test
    void simplestCase_sequenceMode() {
        //
        new LunaticAddPageNumbers(EnoParameters.LunaticPaginationMode.SEQUENCE).apply(questionnaire);
        //
        assertEquals("1", questionnaire.getComponents().get(0).getPage());
        assertEquals("1", questionnaire.getComponents().get(1).getPage());
        assertEquals("1", questionnaire.getMaxPage());
    }

    @Test
    void simplestCase_questionMode() {
        //
        new LunaticAddPageNumbers(EnoParameters.LunaticPaginationMode.QUESTION).apply(questionnaire);
        //
        assertEquals("1", questionnaire.getComponents().get(0).getPage());
        assertEquals("2", questionnaire.getComponents().get(1).getPage());
        assertEquals("2", questionnaire.getMaxPage());
    }

    @Nested
    class LoopCase {

        private Questionnaire questionnaire;

        @BeforeEach
        void setup_withLoop() {
            questionnaire = new Questionnaire();
            Loop loop = new Loop();
            loop.getComponents().add(new SequenceType());
            loop.getComponents().add(new Textarea());
            loop.setPaginatedLoop(true);
            questionnaire.getComponents().add(loop);
        }

        @Test
        void singleLoop_questionMode() {
            //
            new LunaticAddPageNumbers(EnoParameters.LunaticPaginationMode.QUESTION).apply(questionnaire);
            //
            Loop loop = (Loop) questionnaire.getComponents().get(0);
            assertEquals("1.1", loop.getComponents().get(0).getPage());
            assertEquals("1.2", loop.getComponents().get(1).getPage());
            assertEquals("2", loop.getMaxPage());
            assertEquals("1", questionnaire.getMaxPage());
        }

        @Test
        void singleLoop_sequenceMode() {
            //
            new LunaticAddPageNumbers(EnoParameters.LunaticPaginationMode.SEQUENCE).apply(questionnaire);
            //
            Loop loop = (Loop) questionnaire.getComponents().get(0);
            assertEquals("1.1", loop.getComponents().get(0).getPage());
            assertEquals("1.1", loop.getComponents().get(1).getPage());
            assertEquals("1", loop.getMaxPage());
            assertEquals("1", questionnaire.getMaxPage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"SEQUENCE", "QUESTION"})
        void singleLoop_notPaginated(String paginationMode) {
            //
            ((Loop) questionnaire.getComponents().get(0)).setPaginatedLoop(false);
            // Pagination mode shouldn't
            new LunaticAddPageNumbers(EnoParameters.LunaticPaginationMode.valueOf(paginationMode)).apply(questionnaire);
            //
            Loop loop = (Loop) questionnaire.getComponents().get(0);
            assertEquals("1.1", loop.getComponents().get(0).getPage());
            assertEquals("1.1", loop.getComponents().get(1).getPage());
            assertEquals("1", loop.getMaxPage());
            assertEquals("1", questionnaire.getMaxPage());
        }

    }

}
