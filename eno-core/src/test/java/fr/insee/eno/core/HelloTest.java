package fr.insee.eno.core;

import instance33.DDIInstanceDocument;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HelloTest {

    @Test
    public void hello() throws XmlException, IOException {
        DDIInstanceDocument instanceDocument=DDIInstanceDocument.Factory.parse(HelloTest.class.getClassLoader().getResourceAsStream("l10xmg2l.xml") );
        assertNotNull(instanceDocument);
    }
}
