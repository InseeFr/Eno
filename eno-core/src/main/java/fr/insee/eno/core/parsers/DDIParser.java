package fr.insee.eno.core.parsers;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import instance33.DDIInstanceDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Slf4j
public class DDIParser {

    private DDIParser() {}

    public static DDIInstanceDocument parse(InputStream ddiInputStream) throws IOException, DDIParsingException {
        log.info("Parsing DDI document from input stream given");
        try {
            return DDIInstanceDocument.Factory.parse(ddiInputStream);
        } catch (XmlException e) {
            throw new DDIParsingException("Unable to parse DDI document from input stream given.", e);
        }
    }

    public static DDIInstanceDocument parse(URL ddiUrl) throws IOException, DDIParsingException {
        log.info("Parsing DDI document from URL " + ddiUrl);
        log.atDebug().log(()->"Test DEBUG logs with lambdas");
        try (InputStream is = ddiUrl.openStream()) {
            DDIInstanceDocument ddiInstanceDocument = DDIInstanceDocument.Factory.parse(is);
            log.info("Successfully parsed DDI from URL " + ddiUrl);
            return ddiInstanceDocument;
        } catch (XmlException e) {
            throw new DDIParsingException("Unable to parse DDI document from URL " + ddiUrl, e);
        }
    }

}