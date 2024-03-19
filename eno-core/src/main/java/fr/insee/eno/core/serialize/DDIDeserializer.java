package fr.insee.eno.core.serialize;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import instance33.DDIInstanceDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Wrapper class for the deserializer provided by the DDI lib.
 */
@Slf4j
public class DDIDeserializer {

    private DDIDeserializer() {}

    /**
     * Deserializes the DDI document from given input stream.
     * @param ddiInputStream Input stream of a DDI document.
     * @return A DDI instance document.
     * @throws DDIParsingException if an error occurs during deserialization.
     */
    public static DDIInstanceDocument deserialize(InputStream ddiInputStream) throws DDIParsingException {
        log.info("Parsing DDI document from input stream given");
        try {
            return DDIInstanceDocument.Factory.parse(ddiInputStream);
        } catch (XmlException e) {
            throw new DDIParsingException("Unable to parse DDI document from input stream given.", e);
        } catch (IOException e) {
            throw new DDIParsingException("IOException occurred when trying to parse given input stream.", e);
        }
    }

    /**
     * Deserializes the DDI document file from given URL.
     * @param ddiUrl URL of a DDI instance document.
     * @return A DDI instance document.
     * @throws DDIParsingException if an error occurs during deserialization.
     */
    public static DDIInstanceDocument deserialize(URL ddiUrl) throws DDIParsingException {
        log.info("Parsing DDI document from URL " + ddiUrl);
        log.atDebug().log(()->"Test DEBUG logs with lambdas");
        try (InputStream is = ddiUrl.openStream()) {
            DDIInstanceDocument ddiInstanceDocument = DDIInstanceDocument.Factory.parse(is);
            log.info("Successfully parsed DDI from URL " + ddiUrl);
            return ddiInstanceDocument;
        } catch (XmlException e) {
            throw new DDIParsingException("Unable to parse DDI document from URL " + ddiUrl, e);
        } catch (IOException e) {
            throw new DDIParsingException("IOException occurred when trying to open stream from URL " + ddiUrl, e);
        }
    }

}
