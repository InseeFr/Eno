package fr.insee.eno.core.output;

import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.lunatic.conversion.JSONSerializer;
import fr.insee.lunatic.conversion.XMLSerializer;
import fr.insee.lunatic.model.flat.Questionnaire;

import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;

public class LunaticSerializer {

    private LunaticSerializer() {}

    /**
     * Define interface here since Lunatic-Model does not provide an interface for serializers.
     */
    private interface ILunaticSerializer {
        String serialize(Questionnaire lunaticQuestionnaire) throws JAXBException, UnsupportedEncodingException;
    }

    private static String serialize(Questionnaire lunaticQuestionnaire, ILunaticSerializer serializer)
            throws LunaticSerializationException {
        try {
            return serializer.serialize(lunaticQuestionnaire);
        } catch (JAXBException e) {
            throw new LunaticSerializationException(
                    "Lunatic questionnaire given cannot be serialized.", e);
        } catch (UnsupportedEncodingException e) {
            throw new LunaticSerializationException(
                    "Encoding exception encountered while trying to serialize Lunatic questionnaire.", e);
        }
    }

    public static String serializeToJson(Questionnaire lunaticQuestionnaire) throws LunaticSerializationException {
        JSONSerializer jsonSerializer = new JSONSerializer();
        return serialize(lunaticQuestionnaire, jsonSerializer::serialize);
    }

    public static String serializeToXml(Questionnaire lunaticQuestionnaire) throws LunaticSerializationException {
        XMLSerializer xmlSerializer = new XMLSerializer();
        return serialize(lunaticQuestionnaire, xmlSerializer::serialize);
    }

}
