package fr.insee.eno.core.parsers;

import fr.insee.eno.core.HelloTest;
import instance33.DDIInstanceDocument;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class DDIParserTest {

    @Test
    public void parserDDITest() throws IOException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse( // FIXME: https://stackoverflow.com/questions/45718145/intellij-errorjava-java-lang-exceptionininitializererror
                this.getClass().getClassLoader().getResource("l10xmg2l.xml"));

        //
        assertNotNull(ddiInstanceDocument);
        //
        assertFalse( ddiInstanceDocument.getDDIInstance().getIDList().isEmpty());
        assertEquals("INSEE-l10xmg2l", ddiInstanceDocument.getDDIInstance().getIDList().get(0).getStringValue());
    }

}
