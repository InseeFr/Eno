package fr.insee.eno.core.reference;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.serialize.DDIDeserializer;
import instance33.DDIInstanceDocument;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnoIndexTest {

    @Test
    void indexingDoneByMapper() throws IOException, DDIParsingException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream("functional/ddi/ddi-l20g2ba7.xml"));
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(ddiInstanceDocument, enoQuestionnaire);
        //
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        assertNotNull(enoIndex);
        //TODO: proper unit test
    }
}
