package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the DDI to Lunatic transformation.
 */
class DDIToLunaticTest {

    private static final String DDI_TEST_FOLDER = "in/ddi/";
    private final ClassLoader classLoader = this.getClass().getClassLoader();

    @Test
    void ddiToLunatic_modeFiltering() throws DDIParsingException {
        // DDI
        InputStream ddiInputStream = classLoader.getResourceAsStream(DDI_TEST_FOLDER + "l10xmg2l.xml");
        // Parameters with mode filtering
        EnoParameters enoParameters = new EnoParameters();
        enoParameters.setSelectedModes(List.of(Mode.CAPI, Mode.CATI));

        //
        Questionnaire result = DDIToLunatic.transform(ddiInputStream, enoParameters);

        //
        assertNotNull(result);
    }

    @Test
    void ddiToLunatic_sandboxQuestionnaire() throws DDIParsingException {
        //
        Questionnaire result = DDIToLunatic.transform(
                classLoader.getResourceAsStream(DDI_TEST_FOLDER + "sandbox_v2.xml"));

        //
        assertNotNull(result);
        //
        assertEquals("INSEE-l8x6fhtd", result.getId());
    }

    @Test
    void ddiToLunatic_largeQuestionnaire1() throws DDIParsingException {
        //
        Questionnaire result = DDIToLunatic.transform(
                classLoader.getResourceAsStream(DDI_TEST_FOLDER + "l10xmg2l.xml"));

        //
        assertNotNull(result);
        //
        assertEquals("INSEE-l10xmg2l", result.getId());
    }

    @Test
    void ddiToLunatic_largeQuestionnaire2() throws DDIParsingException {
        //
        Questionnaire result = DDIToLunatic.transform(
                classLoader.getResourceAsStream(DDI_TEST_FOLDER + "l20g2ba7.xml"));

        //
        assertNotNull(result);
        //
        assertEquals("INSEE-l20g2ba7", result.getId());
    }

    @Test
    void ddiToLunatic_pairwise() throws DDIParsingException {
        //
        Questionnaire result = DDIToLunatic.transform(
                classLoader.getResourceAsStream("pairwise/form-ddi-household-links.xml"));
        //
        assertNotNull(result);
    }

}
