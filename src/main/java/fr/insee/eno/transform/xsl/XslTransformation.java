package fr.insee.eno.transform.xsl;

import fr.insee.eno.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * Main Saxon Service used to perform XSLT transformations
 * 
 * @author gerose
 *
 */
public class XslTransformation {

	final static Logger logger = LogManager.getLogger(XslTransformation.class);

	/**
	 * Main Saxon transformation method
	 * 
	 * @param transformer
	 *            : The defined transformer with his embedded parameters
	 *            (defined in the other methods of this class)
	 * @param xmlInput
	 *            : The input xml file where the XSLT will be applied
	 * @param xmlOutput
	 *            : The output xml file after the transformation
	 * @throws Exception
	 *             : Mainly if the input/output files path are incorrect
	 */
	public void xslTransform(Transformer transformer, InputStream xmlInput, OutputStream xmlOutput) throws Exception {
		logger.debug("Starting xsl transformation -Input : " + xmlInput + " -Output : " + xmlOutput);
		transformer.transform(new StreamSource(xmlInput), new StreamResult(xmlOutput));
	}

	/**
	 * Basic Transformer initialization without parameters
	 * 
	 * @param input
	 *            : the input xml file
	 * @param xslSheet
	 *            : the xsl stylesheet that will be used
	 * @param output
	 *            : the xml output that will be created
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	public void transform(InputStream input, InputStream xslSheet, OutputStream output) throws Exception {
		logger.debug("Using the basic transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		//transformer.setURIResolver(new ClasspathURIResolver());
		xslTransform(transformer, input, output);
	}

	/**
	 * Incorporation Transformer initialization with its parameters
	 * 
	 * @param input
	 *            : the input xml file
	 * @param xslSheet
	 *            : the xsl stylesheet that will be used
	 * @param output
	 *            : the xml output that will be created
	 * @param generatedFileParameter
	 *            : Incorporation xsl parameter
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	public void transformIncorporation(InputStream input, InputStream xslSheet, OutputStream output, File generatedFileParameter)
			throws Exception {
		logger.debug("Using the incorporation transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.INCORPORATION_GENERATED_FILE, generatedFileParameter.toURI());
		xslTransform(transformer, input, output);
	}
	
	/**
	 * Markdown to XHTML with its parameters
	 * 
	 * @param input
	 *            : the input xml file
	 * @param xslSheet
	 *            : the xsl stylesheet that will be used
	 * @param output
	 *            : the xml output that will be created
	 * @param outputFolderParameter
	 *            : Markdown to XHTML xsl parameter
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	public void transformMw2XHTML(InputStream input, InputStream xslSheet, OutputStream output, File outputFolderParameter)
			throws Exception {
		logger.debug("Using the Markdown to XHTML transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.MW2XHTML_OUTPUT_FOLDER, outputFolderParameter);
		xslTransform(transformer, input, output);
	}
	
	/**
	 * TweakXhtmlForDdi with its parameters
	 * 
	 * @param input
	 *            : the input xml file
	 * @param xslSheet
	 *            : the xsl stylesheet that will be used
	 * @param output
	 *            : the xml output that will be created
	 * @param outputFolderParameter
	 *            : TweakXhtmlForDdi xsl parameter
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	public void transformTweakXhtmlForDdi(InputStream input, InputStream xslSheet, OutputStream output, File outputFolderParameter)
			throws Exception {
		logger.debug("Using the TweakXhtmlForDdi transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.TWEAK_XHTML_FOR_DDI_OUTPUT_FOLDER, outputFolderParameter);
		xslTransform(transformer, input, output);
	}

	/**
	 * Dereferencing Transformer initialization with its parameters
	 * 
	 * @param input
	 *            : the input xml file
	 * @param xslSheet
	 *            : the xsl stylesheet that will be used
	 * @param output
	 *            : the xml output that will be created
	 * @param outputFolderParameter
	 *            : Dereferencing xsl parameter
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	public void transformDereferencing(InputStream input, InputStream xslSheet, OutputStream output, File outputFolderParameter)
			throws Exception {
		logger.debug("Using the dereferencing transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.DEREFERENCING_OUTPUT_FOLDER, outputFolderParameter);
		xslTransform(transformer, input, output);
	}

	/**
	 * Titling Transformer initialization with its parameters
	 * 
	 * @param input
	 *            : the input xml file
	 * @param xslSheet
	 *            : the xsl stylesheet that will be used
	 * @param output
	 *            : the xml output that will be created
	 * @param parametersFileParameter
	 *            : Titling xsl parameter
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	public void transformTitling(InputStream input, InputStream xslSheet, OutputStream output, InputStream parametersFileParameter)
			throws Exception {
		logger.debug("Using the titling transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(
				XslParameters.TITLING_PARAMETERS_FILE, 
				new URI("classpath:" + Constants.PARAMETERS_XML));
		xslTransform(transformer, input, output);
	}

	/**
	 * Ddi2fr Transformer initialization with its parameters
	 * 
	 * @param input
	 *            : the input xml file
	 * @param xslSheet
	 *            : the xsl stylesheet that will be used
	 * @param output
	 *            : the xml output that will be created
	 * @param campaignParameter
	 *            : Ddi2fr xsl parameter
	 * @param modelParameter
	 *            : Ddi2fr xsl parameter
	 * @param propertiesFileParameter
	 *            : Ddi2fr xsl parameter
	 * @param labelFolder
	 * 			  : the folder where the i18n labels reside
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	@Deprecated
	public void transformDdi2frBasicForm(
			InputStream input,
			InputStream xslSheet,
			OutputStream output,
			String campaignParameter,
			InputStream modelParameter,
			InputStream propertiesFileParameter,
			File labelFolder) 
					throws Exception {
		logger.debug("Using the DDI to XForms transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.DDI2FR_CAMPAIGN, campaignParameter);
		transformer.setParameter(XslParameters.DDI2FR_MODEL, modelParameter);
		transformer.setParameter(XslParameters.DDI2FR_PROPERTIES_FILE, propertiesFileParameter);
		transformer.setParameter(XslParameters.DDI2FR_LABELS_FOLDER, labelFolder);
		xslTransform(transformer, input, output);
	}
	
	/**
	 * Ddi2odt Transformer initialization with its parameters
	 * 
	 * @param input
	 *            : the input xml file
	 * @param xslSheet
	 *            : the xsl stylesheet that will be used
	 * @param output
	 *            : the xml output that will be created
	 * @param campaignParameter
	 *            : Ddi2odt xsl parameter
	 * @param modelParameter
	 *            : Ddi2odt xsl parameter
	 * @param propertiesFileParameter
	 *            : Ddi2odt xsl parameter
	 * @param labelFolder
	 * 			  : the folder where the i18n labels reside
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	@Deprecated
	public void transformDdi2odtBasicForm(
			InputStream input,
			InputStream xslSheet,
			OutputStream output,
			String campaignParameter,
			InputStream modelParameter,
			InputStream propertiesFileParameter,
			File labelFolder) 
					throws Exception {
		logger.debug("Using the DDI to ODT transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.DDI2ODT_CAMPAIGN, campaignParameter);
		transformer.setParameter(XslParameters.DDI2ODT_MODEL, modelParameter);
		transformer.setParameter(XslParameters.DDI2ODT_PROPERTIES_FILE, propertiesFileParameter);
		transformer.setParameter(XslParameters.DDI2ODT_LABELS_FOLDER, labelFolder);
		xslTransform(transformer, input, output);
	}
		
	
	public void transformDDI2FR(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			InputStream propertiesFile, InputStream parametersFile) throws Exception {
		logger.info("Producing a basic XForms from the DDI spec");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.DDI2FR_PROPERTIES_FILE, Constants.CONFIG_DDI2FR);
		transformer.setParameter(XslParameters.DDI2FR_PARAMETERS_FILE, Constants.PARAMETERS);
		transformer.setParameter(XslParameters.DDI2FR_LABELS_FOLDER, Constants.LABELS_FOLDER);
		logger.debug(
				String.format(
						"Transformer parameters are: %s, %s",
						transformer.getParameter(XslParameters.DDI2FR_PROPERTIES_FILE),
						transformer.getParameter(XslParameters.DDI2FR_PARAMETERS_FILE),
						transformer.getParameter(XslParameters.DDI2FR_LABELS_FOLDER)));
		xslTransform(transformer, inputFile, outputFile);
		
	}
	
	public void transformDDI2ODT(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			InputStream propertiesFile, InputStream parametersFile) throws Exception {
		logger.info("Producing a basic ODT from the DDI spec");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.DDI2ODT_PARAMETERS_FILE, Constants.PARAMETERS);
		transformer.setParameter(XslParameters.DDI2ODT_LABELS_FOLDER, Constants.LABELS_FOLDER);
		logger.debug(
				String.format(
						"Transformer parameters are: %s, %s",
						transformer.getParameter(XslParameters.DDI2ODT_PROPERTIES_FILE),
						transformer.getParameter(XslParameters.DDI2ODT_PARAMETERS_FILE),
						transformer.getParameter(XslParameters.DDI2ODT_LABELS_FOLDER)));
		xslTransform(transformer, inputFile, outputFile);
		
	}
	
	
	public void transformPoguesXML2DDI(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			InputStream propertiesFile, InputStream parametersFile) throws Exception {
		logger.info("Producing a basic DDI from the PoguesXML spec");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.POGUES_XML2DDI_PARAMETERS_FILE, Constants.PARAMETERS);
		transformer.setParameter(XslParameters.POGUES_XML2DDI_LABELS_FOLDER, Constants.LABELS_FOLDER);
		logger.debug(
				String.format(
						"Transformer parameters are: %s, %s",
						transformer.getParameter(XslParameters.POGUES_XML2DDI_PROPERTIES_FILE),
						transformer.getParameter(XslParameters.POGUES_XML2DDI_PARAMETERS_FILE),
						transformer.getParameter(XslParameters.POGUES_XML2DDI_LABELS_FOLDER)));
		xslTransform(transformer, inputFile, outputFile);
		
	}
	
	public void transformBrowsingDDI2FR(InputStream inputFile, OutputStream outputFile, InputStream xslSheet, File labelFolder) throws Exception {
		logger.info("Include the navigation elements into the XForms questionnaire");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.DDI2FR_LABELS_FOLDER, Constants.LABELS_FOLDER);
		logger.debug(
				String.format(
						"Transformer parameter is: %s",
						transformer.getParameter(XslParameters.DDI2FR_LABELS_FOLDER)));
		xslTransform(transformer, inputFile, outputFile);
	}
	
	public void transformBrowsingDDI2ODT(InputStream inputFile, OutputStream outputFile, InputStream xslSheet, File labelFolder) throws Exception {
		logger.info("Include the navigation elements into the ODT questionnaire");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.DDI2ODT_LABELS_FOLDER, Constants.LABELS_FOLDER);
		logger.debug(
				String.format(
						"Transformer parameter is: %s",
						transformer.getParameter(XslParameters.DDI2ODT_LABELS_FOLDER)));
		xslTransform(transformer, inputFile, outputFile);
	}
	

}
