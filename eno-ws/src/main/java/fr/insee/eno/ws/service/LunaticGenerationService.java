package fr.insee.eno.ws.service;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.serialize.LunaticSerializer;
import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;

public abstract class LunaticGenerationService {

    @Value("${version.eno}")
    String enoVersion;

    @Value("${version.lunatic.model}")
    String lunaticModelVersion;

    /**
     * Transform given input stream to a Lunatic questionnaire.
     * @param inputStream Input stream, e.g. of a DDI or Pogues questionnaire.
     * @param enoParameters Eno parameters object.
     * @return String Lunatic questionnaire.
     */
    public final String transform(InputStream inputStream, EnoParameters enoParameters) {
        try {
            Questionnaire lunaticQuestionnaire = mainTransformation(inputStream, enoParameters);
            lunaticQuestionnaire.setEnoCoreVersion(enoVersion);
            lunaticQuestionnaire.setLunaticModelVersion(lunaticModelVersion);
            return LunaticSerializer.serializeToJson(lunaticQuestionnaire);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    /**
     * Transform given input stream to a Lunatic questionnaire.
     * @param inputStream Input stream, e.g. of a DDI or Pogues questionnaire.
     * @param enoParameters Eno parameters object.
     * @param lunaticPostProcessing Specific treatments to be applied.
     * @return String Lunatic questionnaire.
     */
    public String transform(InputStream inputStream, EnoParameters enoParameters, LunaticPostProcessing lunaticPostProcessing) {
        try {
            Questionnaire lunaticQuestionnaire = mainTransformation(inputStream, enoParameters);
            lunaticQuestionnaire.setEnoCoreVersion(enoVersion);
            lunaticQuestionnaire.setLunaticModelVersion(lunaticModelVersion);
            if (lunaticPostProcessing != null)
                lunaticPostProcessing.apply(lunaticQuestionnaire);
            return LunaticSerializer.serializeToJson(lunaticQuestionnaire);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    abstract Questionnaire mainTransformation(InputStream inputStream, EnoParameters enoParameters) throws Exception;

    abstract void handleException(Exception e);

}
