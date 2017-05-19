package fr.insee.eno.transform.xsl;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Use for controlling the resolution of includes
 * FIXME we need to urgently change the includes to match a simpler scheme
 * i.e. import statements href are equal to <code>/path/to/resources/directory</code>
 * */
public class ClasspathURIResolver implements URIResolver {
	
	final static Logger logger = LogManager.getLogger(ClasspathURIResolver.class);

	@Override
	public Source resolve(String href, String base) throws TransformerException {
		logger.debug("Resolving URI with href: " + href + " and base: " + base);
		String resolvedHref;
		if (href.startsWith("..")) {
			if (href.startsWith("../..")) {
				resolvedHref = href.replaceFirst("../..", "/xslt");
				logger.debug("Resolved URI is: " + resolvedHref);
			} else {
				resolvedHref = href.replaceFirst("..", "/xslt");
				logger.debug("Resolved XSLT URI is: " + resolvedHref);
			}			
		} else {
			resolvedHref = href;
			logger.debug("Resolved URI href is: " + resolvedHref);
		}
		return new StreamSource(ClasspathURIResolver.class.getResourceAsStream(resolvedHref));
	}

}
