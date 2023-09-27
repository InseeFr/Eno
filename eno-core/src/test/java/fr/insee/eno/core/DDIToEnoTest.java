package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static fr.insee.eno.core.model.sequence.ItemReference.ItemType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Functional tests on the mapping from DDI to Eno-model questionnaire.
 * These could be re-written using Cucumber.
 * */
class DDIToEnoTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "l20g2ba7", // acceptance questionnaire
            //"l5v3spn0", // contains loops
            "kx0a2hn8", // contains loops
            "kzy5kbtl", // contains tables
            //"l8x6fhtd", // 'sandbox' questionnaire
            //"ldodefpq", // contains pairwise question
            "lhpz68wp", // designed to test question grouping
            "li49zxju" // 'vpp' survey
    })
    @DisplayName("Large questionnaires, DDI to Eno, transformation should succeed")
    void transformQuestionnaire_nonNullOutput(String questionnaireId) throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-"+questionnaireId+".xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, ModeParameter.PROCESS));
        //
        assertNotNull(enoQuestionnaire);
    }

    @Nested
    @DisplayName("DDI to Eno, functional test with 'l20g2ba7'")
    class FunctionalTest1 {

        private static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapDDI() throws DDIParsingException {
            // The focus is on the mapping part here, so business processing (that may be enabled by default) are disabled.
            EnoParameters enoParameters = EnoParameters.emptyValues();
            enoParameters.setModeParameter(ModeParameter.PROCESS);
            enoParameters.setSequenceNumbering(false);
            enoParameters.setQuestionNumberingMode(EnoParameters.QuestionNumberingMode.NONE);
            enoParameters.setArrowCharInQuestions(false);
            enoParameters.setIdentificationQuestion(false);
            enoParameters.setCommentSection(false);
            enoParameters.setResponseTimeQuestion(false);
            //
            enoQuestionnaire = DDIToEno.transform(
                    DDIToEnoTest.class.getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-l20g2ba7.xml"),
                    enoParameters);
        }

        @Test
        void enoQuestionnaireIsNotNull() {
            assertNotNull(enoQuestionnaire);
        }
        @Test
        void variablesCount() {
            assertEquals(127, enoQuestionnaire.getVariables().size());
        }
        @Test
        void codeLists() {
            assertEquals(16, enoQuestionnaire.getCodeLists().size()); // Code lists should be refactored at questionnaire level
        }
        @Test
        void sequencesCount() {
            // Sequences
            assertEquals(6, enoQuestionnaire.getSequences().size());
        }
        @Test
        void sequenceSizes() {
            assertEquals(3, enoQuestionnaire.getSequences().get(0).getSequenceItems().size());
            assertEquals(2, enoQuestionnaire.getSequences().get(1).getSequenceItems().size());
            assertEquals(9, enoQuestionnaire.getSequences().get(2).getSequenceItems().size());
            assertEquals(5, enoQuestionnaire.getSequences().get(3).getSequenceItems().size());
            assertEquals(3, enoQuestionnaire.getSequences().get(4).getSequenceItems().size());
            assertEquals(0, enoQuestionnaire.getSequences().get(5).getSequenceItems().size());
        }
        @Test
        void sequenceItemTypes() {
            //
            assertEquals(SUBSEQUENCE, enoQuestionnaire.getSequences().get(0).getSequenceItems().get(0).getType());
            assertEquals(SUBSEQUENCE, enoQuestionnaire.getSequences().get(0).getSequenceItems().get(1).getType());
            assertEquals(SUBSEQUENCE, enoQuestionnaire.getSequences().get(0).getSequenceItems().get(2).getType());
            //
            assertEquals(QUESTION, enoQuestionnaire.getSequences().get(3).getSequenceItems().get(0).getType());
            assertEquals(CONTROL, enoQuestionnaire.getSequences().get(3).getSequenceItems().get(1).getType());
            assertEquals(QUESTION, enoQuestionnaire.getSequences().get(3).getSequenceItems().get(2).getType());
            assertEquals(SUBSEQUENCE, enoQuestionnaire.getSequences().get(3).getSequenceItems().get(3).getType());
            assertEquals(FILTER, enoQuestionnaire.getSequences().get(3).getSequenceItems().get(4).getType());
            //
            assertEquals(SUBSEQUENCE, enoQuestionnaire.getSequences().get(4).getSequenceItems().get(0).getType());
            assertEquals(LOOP, enoQuestionnaire.getSequences().get(4).getSequenceItems().get(1).getType());
            assertEquals(LOOP, enoQuestionnaire.getSequences().get(4).getSequenceItems().get(2).getType());
        }
        @Test
        void sequenceItemIds() {
            //
            assertEquals("jfazsitt", enoQuestionnaire.getSequences().get(0).getSequenceItems().get(0).getId());
            assertEquals("jfjhggkx", enoQuestionnaire.getSequences().get(0).getSequenceItems().get(1).getId());
            assertEquals("jfjeuskc", enoQuestionnaire.getSequences().get(0).getSequenceItems().get(2).getId());
            //
            assertEquals("k6c3ia6n", enoQuestionnaire.getSequences().get(3).getSequenceItems().get(0).getId());
            assertEquals("k6c3ia6n-CI-0", enoQuestionnaire.getSequences().get(3).getSequenceItems().get(1).getId());
            assertEquals("k6gjzooe", enoQuestionnaire.getSequences().get(3).getSequenceItems().get(2).getId());
            assertEquals("k3opeux2", enoQuestionnaire.getSequences().get(3).getSequenceItems().get(3).getId());
            assertEquals("kzf8xhgq", enoQuestionnaire.getSequences().get(3).getSequenceItems().get(4).getId());
            //
            assertEquals("k6c75pyx", enoQuestionnaire.getSequences().get(4).getSequenceItems().get(0).getId());
            assertEquals("l8uayz0h", enoQuestionnaire.getSequences().get(4).getSequenceItems().get(1).getId());
            assertEquals("kfs6ox4i", enoQuestionnaire.getSequences().get(4).getSequenceItems().get(2).getId());
        }
        @Test
        void questionsCount() {
            assertEquals(30, enoQuestionnaire.getSingleResponseQuestions().size());
            assertEquals(9, enoQuestionnaire.getMultipleResponseQuestions().size());
        }

    }

}
