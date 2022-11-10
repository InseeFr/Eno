package fr.insee.eno.core.output;

import fr.insee.eno.core.exceptions.LunaticSerializationException;
import fr.insee.lunatic.conversion.JSONSerializer;
import fr.insee.lunatic.model.flat.Questionnaire;

import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;

public class LunaticSerializer {

    public static String serializeToJson(Questionnaire lunaticQuestionnaire) throws LunaticSerializationException {
        JSONSerializer jsonSerializer = new JSONSerializer();
        try {
            return jsonSerializer.serialize(lunaticQuestionnaire);
        } catch (JAXBException e) {
            throw new LunaticSerializationException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
