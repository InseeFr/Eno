package fr.insee.eno.core.writers;

import fr.insee.lunatic.conversion.JSONSerializer;
import fr.insee.lunatic.model.flat.Questionnaire;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LunaticWriter {

    /**
     * Write a Lunatic questionnaire as a json file
     * (temporary method to test the application).
     * @param lunaticQuestionnaire A Lunatic questionnaire instance.
     * @param outFile Path of the output file.
     * @throws JAXBException if the questionnaire given cannot be serialized.
     * @throws IOException if the out file cannot be written.
     */
    public static void writeJsonQuestionnaire(Questionnaire lunaticQuestionnaire, Path outFile)
            throws JAXBException, IOException {
        JSONSerializer jsonSerializer = new JSONSerializer();
        String content = jsonSerializer.serialize(lunaticQuestionnaire);
        Files.writeString(outFile, content);
    }
}
