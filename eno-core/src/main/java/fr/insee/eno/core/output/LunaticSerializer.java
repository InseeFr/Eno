package fr.insee.eno.core.output;

import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.lunatic.conversion.JsonSerializer;
import fr.insee.lunatic.exception.SerializationException;
import fr.insee.lunatic.model.flat.Questionnaire;

public class LunaticSerializer {

    private LunaticSerializer() {}

    /**
     * Define interface here since Lunatic-Model does not provide an interface for serializers.
     */
    private interface ILunaticSerializer {
        String serialize(Questionnaire lunaticQuestionnaire) throws SerializationException;
    }

    private static String serialize(Questionnaire lunaticQuestionnaire, ILunaticSerializer serializer)
            throws LunaticSerializationException {
        try {
            return serializer.serialize(lunaticQuestionnaire);
        } catch (SerializationException e) {
            throw new LunaticSerializationException(
                    "Lunatic questionnaire given cannot be serialized.", e);
        }
    }

    public static String serializeToJson(Questionnaire lunaticQuestionnaire) throws LunaticSerializationException {
        JsonSerializer jsonSerializer = new JsonSerializer();
        try {
            return jsonSerializer.serialize(lunaticQuestionnaire);
        } catch (SerializationException e) {
            throw new LunaticSerializationException("Error when calling Lunatic-Model serializer.", e);
        }
    }
}
