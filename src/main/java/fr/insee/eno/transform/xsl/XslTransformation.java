package fr.insee.eno.transform.xsl;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface XslTransformation {
	
	 Logger LOGGER = LoggerFactory.getLogger(XslTransformation.class);

	default void transform(InputStream xmlInput, OutputStream xmlOutput, InputStream xslSheet) throws Exception {
		LOGGER.info("Simple transformation");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer=setParameters(transformer);
		transformer.setErrorListener(new EnoErrorListener());
		xslTransform(transformer, xmlInput, xmlOutput);
	}
	
	default Transformer setParameters(Transformer transformer) {
		return transformer;
	}

	default void xslTransform(Transformer transformer, InputStream xmlInput, OutputStream xmlOutput) throws Exception {
		LOGGER.debug("Starting xsl transformation -Input : " + xmlInput + " -Output : " + xmlOutput);
		transformer.transform(new StreamSource(xmlInput), new StreamResult(xmlOutput));
	}

}
