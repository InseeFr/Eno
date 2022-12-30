package fr.insee.eno.core.output;

import fr.insee.lunatic.conversion.JSONSerializer;
import fr.insee.lunatic.model.flat.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class LunaticWriter {

    private LunaticWriter() {}

    /**
     * Write a Lunatic questionnaire as a json file
     * (temporary method to test the application).
     * @param lunaticQuestionnaire A Lunatic questionnaire instance.
     * @param outPath Path of the output file.
     * @throws JAXBException if the questionnaire given cannot be serialized.
     * @throws IOException if the out file cannot be written.
     */
    public static void writeJsonQuestionnaire(Questionnaire lunaticQuestionnaire, Path outPath)
            throws JAXBException, IOException {
        JSONSerializer jsonSerializer = new JSONSerializer();
        String content = jsonSerializer.serialize(lunaticQuestionnaire);
        log.info("Writing Lunatic questionnaire at location: " + outPath);
        Files.writeString(outPath, content);
    }
}
