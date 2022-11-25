package fr.insee.eno.core.demo;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.exceptions.DDIParsingException;
import fr.insee.eno.core.exceptions.LunaticSerializationException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.EnoProcessing;
import fr.insee.eno.core.processing.LunaticProcessing;
import fr.insee.eno.core.output.EnoWriter;
import fr.insee.eno.core.output.LunaticWriter;
import fr.insee.lunatic.model.flat.Questionnaire;
import instance33.DDIInstanceDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.xml.bind.JAXBException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DDIToLunaticTest {

    /*@ParameterizedTest
    @ValueSource(strings = {
            //"l10xmg2l",
            //"sandbox",
            "l20g2ba7",
            "sandbox_v2",
            "DDI-tableau-2-colonnes",
    })*/
    public void writeJsonLunaticFromDDI(String fileName)
            throws IOException, DDIParsingException, LunaticSerializationException {
        // DDI
        InputStream ddiInputStream = this.getClass().getClassLoader()
                .getResourceAsStream("in/ddi/" + fileName + ".xml");
        // Parameters with mode filtering
        EnoParameters enoParameters = new EnoParameters();
        enoParameters.setSelectedModes(List.of(Mode.CAPI, Mode.CATI));

        //
        Files.writeString(Path.of("src/test/resources/out/lunatic/" + fileName + ".json"),
                DDIToLunatic.transform(ddiInputStream, enoParameters));
    }

    @Test
    public void ddiToLunatic_pairwise() throws DDIParsingException, IOException, LunaticSerializationException {
        Files.writeString(Path.of("src/test/resources/out/lunatic/pairwise-test.json"),
                DDIToLunatic.transform(
                        this.getClass().getClassLoader().getResourceAsStream("pairwise/form-ddi-household-links.xml"),
                        new EnoParameters()));
    }

}
