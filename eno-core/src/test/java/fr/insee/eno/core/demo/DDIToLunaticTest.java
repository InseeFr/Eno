package fr.insee.eno.core.demo;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.parameter.EnoParameters;
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
            "sandbox_v2",
            "DDI-tableau-2-colonnes",
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
            String result = DDIToLunatic.transformToJson(ddiInputStream, enoParameters);

            //
            //Files.writeString(Path.of("src/test/resources/out/lunatic/" + fileName + ".json"), result);
        });
    }

    @Test
    void ddiToLunatic_pairwise() {
        assertDoesNotThrow(() -> {
            String result = DDIToLunatic.transformToJson(
                    this.getClass().getClassLoader().getResourceAsStream("pairwise/form-ddi-household-links.xml"),
                    new EnoParameters());

            //Files.writeString(Path.of("src/test/resources/out/lunatic/pairwise-test.json"), result);
        });
    }

}
