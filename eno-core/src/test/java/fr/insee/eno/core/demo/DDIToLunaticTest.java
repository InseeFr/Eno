package fr.insee.eno.core.demo;

import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.Mode;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.EnoProcessing;
import fr.insee.eno.core.processing.LunaticProcessing;
import fr.insee.eno.core.writers.EnoWriter;
import fr.insee.eno.core.writers.LunaticWriter;
import fr.insee.lunatic.model.flat.Questionnaire;
import instance33.DDIInstanceDocument;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class DDIToLunaticTest {

    @ParameterizedTest
    @ValueSource(strings = {
            //"l10xmg2l",
            //"sandbox",
            "l20g2ba7",
            "sandbox_v2",
            "DDI-tableau-2-colonnes",
    })
    public void writeJsonLunaticFromDDI(String fileName) throws IOException, JAXBException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                this.getClass().getClassLoader().getResource("in/ddi/" + fileName + ".xml"));
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(ddiInstanceDocument, enoQuestionnaire);

        // Mode filtering
        EnoParameters enoParameters = new EnoParameters();
        enoParameters.setSelectedModes(List.of(Mode.CAPI, Mode.CATI));
        //
        EnoProcessing enoProcessing = new EnoProcessing(enoParameters);
        enoProcessing.applyProcessing(enoQuestionnaire, Format.DDI);

        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);

        //
        LunaticProcessing lunaticProcessing = new LunaticProcessing();
        lunaticProcessing.applyProcessing(lunaticQuestionnaire, enoQuestionnaire);

        //
        EnoWriter.writeJson(enoQuestionnaire,
                Path.of("src/test/resources/out/eno/" + fileName + ".json"));
        //
        LunaticWriter.writeJsonQuestionnaire(lunaticQuestionnaire,
               Path.of("src/test/resources/out/lunatic/" + fileName + ".json"));
    }

}
