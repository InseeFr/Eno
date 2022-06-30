package fr.insee.eno.core.demo;

import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.writers.EnoWriter;
import fr.insee.eno.core.writers.LunaticWriter;
import fr.insee.lunatic.model.flat.Questionnaire;
import instance33.DDIInstanceDocument;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;

public class DDIToLunatic {

    @ParameterizedTest
    @ValueSource(strings = {
            "l10xmg2l",
            //"questionnaire-avec-filtre-eno-java",
    })
    public void writeJsonLunaticFromDDI(String fileName) throws IOException, JAXBException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                this.getClass().getClassLoader().getResource("in/ddi/" + fileName + ".xml"));
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        DDIMapper ddiMapper = new DDIMapper(ddiInstanceDocument);
        ddiMapper.mapDDI(enoQuestionnaire);

        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        LunaticMapper.map(enoQuestionnaire, lunaticQuestionnaire);

        //
        EnoWriter.writeJson(enoQuestionnaire,
                Path.of("src/test/resources/out/eno/" + fileName + ".json"));
        //
        LunaticWriter.writeJsonQuestionnaire(lunaticQuestionnaire,
               Path.of("src/test/resources/out/lunatic/" + fileName + ".json"));

    }
}
