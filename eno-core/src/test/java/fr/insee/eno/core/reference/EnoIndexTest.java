package fr.insee.eno.core.reference;

import fr.insee.eno.core.exceptions.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parsers.DDIParser;
import instance33.DDIInstanceDocument;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EnoIndexTest {

    @Test
    public void indexingDoneByMapper() throws IOException, DDIParsingException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                this.getClass().getClassLoader().getResourceAsStream("in/ddi/l10xmg2l.xml"));
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
