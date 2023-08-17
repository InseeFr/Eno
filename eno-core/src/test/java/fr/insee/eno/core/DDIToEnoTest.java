package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.insee.eno.core.model.sequence.ItemReference.ItemType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Functional tests on the mapping from DDI to Eno-model questionnaire.
 * These could be re-written using Cucumber.
 * */
class DDIToEnoTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();
    private EnoParameters enoParameters;

    /** The focus is on the mapping part here, so business processing (that may be enabled by default) are disabled. */
    @BeforeEach
    void parametersWithNoBusinessProcessing() {
        enoParameters = EnoParameters.emptyValues();
        enoParameters.setModeParameter(ModeParameter.PROCESS);
        enoParameters.setSequenceNumbering(false);
        enoParameters.setQuestionNumberingMode(EnoParameters.QuestionNumberingMode.NONE);
        enoParameters.setArrowCharInQuestions(false);
        enoParameters.setIdentificationQuestion(false);
        enoParameters.setCommentSection(false);
        enoParameters.setResponseTimeQuestion(false);
    }

    @Test
    @DisplayName("DDI 'l20g2ba7' (acceptance test)")
    void test01() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                classLoader.getResourceAsStream("end-to-end/ddi/ddi-l20g2ba7.xml"),
                enoParameters);

        //
        assertNotNull(enoQuestionnaire);

        // Variables
        assertEquals(127, enoQuestionnaire.getVariables().size());

        // Code lists
        //assertEquals(16, enoQuestionnaire.getCodeLists().size()); // Code lists should be refactored at questionnaire level

        // Sequences
        assertEquals(6, enoQuestionnaire.getSequences().size());
        // First sequence
        assertEquals(3, enoQuestionnaire.getSequences().get(0).getSequenceItems().size());
        assertEquals("jfazsitt", enoQuestionnaire.getSequences().get(0).getSequenceItems().get(0).getId());
        assertEquals("jfjhggkx", enoQuestionnaire.getSequences().get(0).getSequenceItems().get(1).getId());
        assertEquals("jfjeuskc", enoQuestionnaire.getSequences().get(0).getSequenceItems().get(2).getId());
        assertEquals(SUBSEQUENCE, enoQuestionnaire.getSequences().get(0).getSequenceItems().get(0).getType());
        assertEquals(SUBSEQUENCE, enoQuestionnaire.getSequences().get(0).getSequenceItems().get(1).getType());
        assertEquals(SUBSEQUENCE, enoQuestionnaire.getSequences().get(0).getSequenceItems().get(2).getType());
        //
        assertEquals(2, enoQuestionnaire.getSequences().get(1).getSequenceItems().size());
        //
        assertEquals(9, enoQuestionnaire.getSequences().get(2).getSequenceItems().size());
        //
        assertEquals(5, enoQuestionnaire.getSequences().get(3).getSequenceItems().size());
        assertEquals("k6c3ia6n", enoQuestionnaire.getSequences().get(3).getSequenceItems().get(0).getId());
        assertEquals("k6c3ia6n-CI-0", enoQuestionnaire.getSequences().get(3).getSequenceItems().get(1).getId());
        assertEquals("k6gjzooe", enoQuestionnaire.getSequences().get(3).getSequenceItems().get(2).getId());
        assertEquals("k3opeux2", enoQuestionnaire.getSequences().get(3).getSequenceItems().get(3).getId());
        assertEquals("kzf8xhgq", enoQuestionnaire.getSequences().get(3).getSequenceItems().get(4).getId());
        assertEquals(QUESTION, enoQuestionnaire.getSequences().get(3).getSequenceItems().get(0).getType());
        assertEquals(CONTROL, enoQuestionnaire.getSequences().get(3).getSequenceItems().get(1).getType());
        assertEquals(QUESTION, enoQuestionnaire.getSequences().get(3).getSequenceItems().get(2).getType());
        assertEquals(SUBSEQUENCE, enoQuestionnaire.getSequences().get(3).getSequenceItems().get(3).getType());
        assertEquals(FILTER, enoQuestionnaire.getSequences().get(3).getSequenceItems().get(4).getType());
        //
        assertEquals(3, enoQuestionnaire.getSequences().get(4).getSequenceItems().size());
        assertEquals("k6c75pyx", enoQuestionnaire.getSequences().get(4).getSequenceItems().get(0).getId());
        assertEquals("l8uayz0h", enoQuestionnaire.getSequences().get(4).getSequenceItems().get(1).getId());
        assertEquals("kfs6ox4i", enoQuestionnaire.getSequences().get(4).getSequenceItems().get(2).getId());
        assertEquals(SUBSEQUENCE, enoQuestionnaire.getSequences().get(4).getSequenceItems().get(0).getType());
        assertEquals(LOOP, enoQuestionnaire.getSequences().get(4).getSequenceItems().get(1).getType());
        assertEquals(LOOP, enoQuestionnaire.getSequences().get(4).getSequenceItems().get(2).getType());
        // Last sequence
        assertEquals(0, enoQuestionnaire.getSequences().get(5).getSequenceItems().size());

        // Questions
        assertEquals(30, enoQuestionnaire.getSingleResponseQuestions().size());
        assertEquals(9, enoQuestionnaire.getMultipleResponseQuestions().size());
    }

    @Test
    @DisplayName("DDI 'l5v3spn0' (contains loops)")
    void test02() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                classLoader.getResourceAsStream("end-to-end/ddi/ddi-l5v3spn0.xml"),
                enoParameters);
        //
        assertNotNull(enoQuestionnaire);
    }

    @Test
    @DisplayName("DDI 'kx0a2hn8' (contains loops)")
    void test03() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                classLoader.getResourceAsStream("end-to-end/ddi/ddi-kx0a2hn8.xml"),
                enoParameters);
        //
        assertNotNull(enoQuestionnaire);
    }

    @Test
    @DisplayName("DDI 'kzy5kbtl' (contains tables)")
    void test04() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                classLoader.getResourceAsStream("end-to-end/ddi/ddi-kzy5kbtl.xml"),
                enoParameters);
        //
        assertNotNull(enoQuestionnaire);
    }

    @Test
    @DisplayName("DDI 'l8x6fhtd' ('sandbox' questionnaire)")
    void test05() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                classLoader.getResourceAsStream("end-to-end/ddi/ddi-l8x6fhtd.xml"),
                enoParameters);
        //
        assertNotNull(enoQuestionnaire);
    }

    @Test
    @DisplayName("DDI 'ldodefpq' (contains pairwise question)")
    @Disabled("Bug identified on the date type of question conversion.")
    void test06() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                classLoader.getResourceAsStream("end-to-end/ddi/ddi-ldodefpq.xml"),
                enoParameters);
        //
        assertNotNull(enoQuestionnaire);
    }

}
