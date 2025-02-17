package fr.insee.eno.ws.service;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.serialize.LunaticSerializer;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.ws.dto.FileDto;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public abstract class LunaticGenerationService {

    private static final String LUNATIC_JSON_FILE_NAME = "lunatic-form.json";

    @Value("${version.eno}")
    String enoVersion;

    @Value("${version.lunatic.model}")
    String lunaticModelVersion;

    /**
     * Transform given input stream to a Lunatic questionnaire.
     * @param inputStream Input stream, e.g. of a DDI or Pogues questionnaire.
     * @param enoParameters Eno parameters object.
     * @param lunaticPostProcessing Specific treatments to be applied. Can be null.
     * @return String Lunatic questionnaire.
     */
    public FileDto transform(InputStream inputStream, EnoParameters enoParameters, LunaticPostProcessing lunaticPostProcessing) {
        try {
            Questionnaire lunaticQuestionnaire = mainTransformation(inputStream, enoParameters);
            lunaticQuestionnaire.setEnoCoreVersion(enoVersion);
            lunaticQuestionnaire.setLunaticModelVersion(lunaticModelVersion);
            if (lunaticPostProcessing != null)
                lunaticPostProcessing.apply(lunaticQuestionnaire);
            return FileDto.builder()
                    .name(LUNATIC_JSON_FILE_NAME)
                    .content(LunaticSerializer.serializeToJson(lunaticQuestionnaire).getBytes())
                    .build();
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }
    public FileDto transform(
            MultipartFile inputFile, EnoParameters enoParameters, LunaticPostProcessing lunaticPostProcessing) throws IOException {
        return transform(inputFile.getInputStream(), enoParameters, lunaticPostProcessing);
    }

    abstract Questionnaire mainTransformation(InputStream inputStream, EnoParameters enoParameters) throws Exception;

    abstract void handleException(Exception e);

}
