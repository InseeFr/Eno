package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.EnoProcessing;
import fr.insee.eno.core.processing.impl.EnoAddCommentSection;
import instance33.DDIInstanceDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Functional tests on the mapping from DDI to Eno-model questionnaire.
 * These could be re-written using Cucumber.
 * */
class DDIToEnoTest {

    private EnoParameters enoParameters;

    /** The focus is on the mapping part here, so business processing (that may be enabled by default) are disabled. */
    @BeforeEach
    void parametersWithNoBusinessProcessing() {
        enoParameters = new EnoParameters();
        enoParameters.setSequenceNumbering(false);
        enoParameters.setQuestionNumberingMode(EnoParameters.QuestionNumberingMode.NONE);
        enoParameters.setArrowCharInQuestions(false);
        enoParameters.setIdentificationQuestion(false);
        enoParameters.setCommentSection(false);
        enoParameters.setResponseTimeQuestion(false);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "l20g2ba7",
            "l5v3spn0",
            "kx0a2hn8",
            "kzy5kbtl",
            "l8x6fhtd",
            //"ldodefpq",
    })
    @DisplayName("Many questionnaires, non null output")
    void testAll(String questionnaireId) throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-"+questionnaireId+".xml"),
                enoParameters);
        //
        assertNotNull(enoQuestionnaire);
    }

    @Test
    @DisplayName("DDI 'l20g2ba7' (acceptance test)")
    void test01() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-l20g2ba7.xml"),
                enoParameters);

        //
        assertNotNull(enoQuestionnaire);

        // Variables
        assertEquals(127, enoQuestionnaire.getVariables().size());

        // Code lists
        //assertEquals(16, enoQuestionnaire.getCodeLists().size()); // Code lists should be refactored at questionnaire level

        // First sequence
        assertEquals(3, enoQuestionnaire.getSequences().get(0).getSequenceItems().size());

        // Questions
        assertEquals(30, enoQuestionnaire.getSingleResponseQuestions().size());
        assertEquals(9, enoQuestionnaire.getMultipleResponseQuestions().size());
    }

    @Test
    @DisplayName("DDI 'l5v3spn0' (contains loops)")
    void test02() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-l5v3spn0.xml"),
                enoParameters);
        //
        assertNotNull(enoQuestionnaire);
    }

    @Test
    @DisplayName("DDI 'kx0a2hn8' (contains loops)")
    void test03() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-kx0a2hn8.xml"),
                enoParameters);
        //
        assertNotNull(enoQuestionnaire);
    }

    @Test
    @DisplayName("DDI 'kzy5kbtl' (contains tables)")
    void test04() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-kzy5kbtl.xml"),
                enoParameters);
        //
        assertNotNull(enoQuestionnaire);
    }

    @Test
    @DisplayName("DDI 'l8x6fhtd' ('sandbox' questionnaire)")
    void test05() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-l8x6fhtd.xml"),
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
                this.getClass().getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-ldodefpq.xml"),
                enoParameters);
        //
        assertNotNull(enoQuestionnaire);
    }

}
