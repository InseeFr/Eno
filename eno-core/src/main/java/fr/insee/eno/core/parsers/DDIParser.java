package fr.insee.eno.core.parsers;

import instance33.DDIInstanceDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Slf4j
public class DDIParser {

    public static DDIInstanceDocument parse(InputStream ddiInputStream) throws XmlException, IOException {
        log.info("Parsing DDI document from input stream given");
        return DDIInstanceDocument.Factory.parse(ddiInputStream);
    }

    public static DDIInstanceDocument parse(URL ddiUrl) throws IOException {
        log.info("Parsing DDI document from URL " + ddiUrl);
        try (InputStream is = ddiUrl.openStream()) {
            DDIInstanceDocument ddiInstanceDocument = DDIInstanceDocument.Factory.parse(is);
            log.info("Successfully parsed DDI from URL " + ddiUrl);
            return ddiInstanceDocument;
        } catch (XmlException e) {
            throw new RuntimeException("Unable to parse DDI document from URL " + ddiUrl, e);
        }

    }
}
