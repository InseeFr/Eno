package fr.insee.eno.core.demo;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.parameter.EnoParameters;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DDIToLunaticTest {

    @ParameterizedTest
    @ValueSource(strings = {
            //"l10xmg2l",
            //"sandbox",
            "l20g2ba7",
            //"sandbox_v2",
            //"DDI-tableau-2-colonnes",
            //"l5v3spn0",
            //"kx0a2hn8",
            //"lciyojcw",
    })
    void writeJsonLunaticFromDDI(String fileName) {
        assertDoesNotThrow(() -> {
            // DDI
            InputStream ddiInputStream = this.getClass().getClassLoader()
                    .getResourceAsStream("in/ddi/" + fileName + ".xml");
            // Parameters with mode filtering
            EnoParameters enoParameters = new EnoParameters();
            enoParameters.setSelectedModes(List.of(Mode.CAPI, Mode.CATI));

            //
            String result = DDIToLunatic.transform(ddiInputStream, enoParameters);

            //
            //Files.writeString(Path.of("src/test/resources/out/lunatic/" + fileName + ".json"), result);
        });
    }

    @Test
    @Disabled
    void ddiToLunatic_pairwise() {
        assertDoesNotThrow(() -> {
            String result = DDIToLunatic.transform(
                    this.getClass().getClassLoader().getResourceAsStream("pairwise/form-ddi-household-links.xml"),
                    new EnoParameters());

            //Files.writeString(Path.of("src/test/resources/out/lunatic/pairwise-test.json"), result);
        });
    }

}
