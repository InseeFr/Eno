package fr.insee.eno.core.demo;

import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.EnoProcessing;
import fr.insee.eno.core.writers.EnoWriter;
import instance33.DDIInstanceDocument;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

public class DDIToEnoTest {

    @Test
    public void writeEnoFileFromDDI() throws IOException {
        //
        //String fileName = "l10xmg2l";
        String fileName = "sandbox";

        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                this.getClass().getClassLoader().getResource("in/ddi/" + fileName + ".xml"));
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        DDIMapper ddiMapper = new DDIMapper(ddiInstanceDocument);
        ddiMapper.mapDDI(enoQuestionnaire);

        //
        EnoProcessing enoProcessing = new EnoProcessing();
        enoProcessing.applyProcessing(enoQuestionnaire);

        //
        EnoWriter.writeJson(enoQuestionnaire,
                Path.of("src/test/resources/out/eno/" + fileName + ".json"));
    }
}
