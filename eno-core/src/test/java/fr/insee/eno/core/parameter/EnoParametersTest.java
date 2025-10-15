package fr.insee.eno.core.parameter;

import fr.insee.eno.core.exceptions.business.EnoParametersException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EnoParametersTest {

    @Test
    void parsedParameters_questionWrappingProp() throws EnoParametersException, IOException {
        String jsonParameters = """
                {
                  "lunatic": {}
                }""";

        EnoParameters enoParameters = EnoParameters.parse(new ByteArrayInputStream(jsonParameters.getBytes()));

        assertTrue(enoParameters.getLunaticParameters().isQuestionWrapping());
    }

}
