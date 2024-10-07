package fr.insee.eno.transform.xsl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

public class EnoErrorListener implements ErrorListener {

    private static final Marker fatal = MarkerFactory.getMarker("FATAL");
    private static final Logger logger = LoggerFactory.getLogger(EnoErrorListener.class);

    @Override
    public void warning(TransformerException e) throws TransformerException {
        logger.warn(e.getMessage());
    }

    @Override
    public void error(TransformerException e) throws TransformerException {
        logger.error(e.getMessage(), e);
    }

    @Override
    public void fatalError(TransformerException e) throws TransformerException {
        logger.error(fatal,e.getMessage(), e);
    }

}
