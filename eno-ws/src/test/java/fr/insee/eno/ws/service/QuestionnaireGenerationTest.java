package fr.insee.eno.ws.service;

import fr.insee.eno.parameters.Context;
import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.Mode;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.service.ParameterizedGenerationService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionnaireGenerationTest {

    @Test
    void ddiToXformsTest_businessCAWI() throws Exception {
        // Given
        ParameterService parameterService = new ParameterService();
        ENOParameters parameters = parameterService.getDefaultCustomParameters(
                Context.BUSINESS, OutFormat.XFORMS, Mode.CAWI);
        try (InputStream ddiInputStream = this.getClass().getClassLoader().getResourceAsStream(
                "transforms/simple-ddi.xml")) {
            // When
            ParameterizedGenerationService generationService = new ParameterizedGenerationService();
            ByteArrayOutputStream outputStream = generationService.generateQuestionnaire(
                    ddiInputStream, parameters, null, null, null);
            // Then
            String result = outputStream.toString();
            assertTrue(result.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
            assertTrue(result.contains("orbeon.org"));
            assertTrue(result.contains("Unique question"));
        }
    }

}
