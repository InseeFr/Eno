package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.model.fo.Questionnaire;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Functional tests for the DDI to Fo transformation.
 */
class DDIToFoTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "l5v3spn0",
    })
    @DisplayName("Large questionnaires, DDI to Fo, transformation should succeed")
    void transformQuestionnaire_nonNullOutput(String questionnaireId) throws DDIParsingException, TransformerException {
        //
        EnoParameters enoParameters = EnoParameters.of(Context.DEFAULT, ModeParameter.PAPI, Format.FO);
        Questionnaire foQuestionnaire = new DDIToFO().transform(
                this.getClass().getClassLoader().getResourceAsStream("functional/ddi/ddi-" +questionnaireId+".xml"),
                enoParameters);
        System.out.println(foQuestionnaire.getRootElement().toString());
        //
        assertNotNull(foQuestionnaire);
    }
}
