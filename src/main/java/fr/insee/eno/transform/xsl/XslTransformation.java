package fr.insee.eno.transform.xsl;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	public void xslTransform(Transformer transformer, String xmlInput, String xmlOutput) throws Exception {
		logger.debug("Starting xsl transformation -Input : " + xmlInput + " -Output : " + xmlOutput);
		transformer.transform(new StreamSource(new File(xmlInput)), new StreamResult(new File(xmlOutput)));
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
	public void transform(String input, String xslSheet, String output) throws Exception {
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		Transformer transformer = tFactory.newTransformer(new StreamSource(new File(xslSheet)));
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
	public void transformIncorporation(String input, String xslSheet, String output, String generatedFileParameter)
			throws Exception {
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(new File(xslSheet)));
		transformer.setParameter(XslParameters.INCORPORATION_GENERATED_FILE, generatedFileParameter);
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
	public void transformDereferencing(String input, String xslSheet, String output, String outputFolderParameter)
			throws Exception {
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(new File(xslSheet)));
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
	public void transformTitling(String input, String xslSheet, String output, String parametersFileParameter)
			throws Exception {
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(new File(xslSheet)));
		transformer.setParameter(XslParameters.TITLING_PARAMETERS_FILE, parametersFileParameter);
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
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	public void transformDdi2frBasicForm(String input, String xslSheet, String output, String campaignParameter,
			String modelParameter, String propertiesFileParameter) throws Exception {
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(new File(xslSheet)));
		transformer.setParameter(XslParameters.DDI2FR_CAMPAIGN, campaignParameter);
		transformer.setParameter(XslParameters.DDI2FR_MODEL, modelParameter);
		transformer.setParameter(XslParameters.DDI2FR_PROPERTIES_FILE, propertiesFileParameter);
		xslTransform(transformer, input, output);
	}

}
