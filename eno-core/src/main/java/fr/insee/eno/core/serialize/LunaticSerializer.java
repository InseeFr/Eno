package fr.insee.eno.core.serialize;

import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.lunatic.conversion.JsonSerializer;
import fr.insee.lunatic.exception.SerializationException;
import fr.insee.lunatic.model.flat.Questionnaire;

/**
 * Wrapper class for Lunatic-Model serializer.
 */
@Slf4j
public class LunaticSerializer {
    private LunaticSerializer() {
        throw new IllegalArgumentException("Utility class");
    }

    /**
     * Serialize the given questionnaire in the json format.
     * @param lunaticQuestionnaire Lunatic questionnaire object.
     * @return Questionnaire serialized as json string.
     * @throws LunaticSerializationException if serialization fails.
     */
    public static String serializeToJson(Questionnaire lunaticQuestionnaire) throws LunaticSerializationException {
        JsonSerializer jsonSerializer = new JsonSerializer();
        try {
            return jsonSerializer.serialize(lunaticQuestionnaire);
        } catch (SerializationException e) {
            throw new LunaticSerializationException("Lunatic questionnaire given cannot be serialized.", e);
        }
    }

}
