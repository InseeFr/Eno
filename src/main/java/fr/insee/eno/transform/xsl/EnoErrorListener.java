package fr.insee.eno.transform.xsl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

public class EnoErrorListener implements ErrorListener {

    private final static Logger logger = LogManager.getLogger(EnoErrorListener.class);

    @Override
    public void warning(TransformerException e) throws TransformerException {
        logger.warn(e.getMessage(), e);
    }

    @Override
    public void error(TransformerException e) throws TransformerException {
        logger.error(e.getMessage(), e);
    }

    @Override
    public void fatalError(TransformerException e) throws TransformerException {
        logger.fatal(e.getMessage(), e);
    }

}
