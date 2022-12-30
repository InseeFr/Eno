package fr.insee.eno.core.output;

import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.lunatic.conversion.JSONSerializer;
import fr.insee.lunatic.model.flat.Questionnaire;

import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;

public class LunaticSerializer {

    private LunaticSerializer() {}

    public static String serializeToJson(Questionnaire lunaticQuestionnaire) throws LunaticSerializationException {
        JSONSerializer jsonSerializer = new JSONSerializer();
        try {
            return jsonSerializer.serialize(lunaticQuestionnaire);
        } catch (JAXBException e) {
            throw new LunaticSerializationException(
                    "Lunatic questionnaire given cannot be serialized.", e);
        } catch (UnsupportedEncodingException e) {
            throw new LunaticSerializationException(
                    "Encoding exception encountered while trying to serialize Lunatic questionnaire.", e);
        }
    }

}
