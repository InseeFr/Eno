package fr.insee.eno.core.parsers;

import instance33.DDIInstanceDocument;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.io.InputStream;

public class DDIParser {

    public static DDIInstanceDocument parse(InputStream ddiInputStream) throws XmlException, IOException {
        return DDIInstanceDocument.Factory.parse(ddiInputStream);
    }
}
