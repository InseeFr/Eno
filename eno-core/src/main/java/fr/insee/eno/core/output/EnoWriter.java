package fr.insee.eno.core.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Writer class created to visualise the content of an EnoQuestionnaire in a file.
 */
public class EnoWriter {

    private EnoWriter() {}

    /**
     * Write the given Eno questionnaire ina json file.
     * @param enoQuestionnaire A Eno questionnaire.
     * @param outPath Path to write to ou file.
     * @throws IOException if the file cannot be written at given path.
     */
    public static void writeJson(EnoQuestionnaire enoQuestionnaire, Path outPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outPath.toFile(), enoQuestionnaire);
    }

}
