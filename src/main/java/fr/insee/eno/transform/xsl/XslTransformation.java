package fr.insee.eno.transform.xsl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;


import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;

/**
 * Main Saxon Service used to perform XSLT transformations
 * 
 * @author gerose
 *
 */
public class XslTransformation {

	final static Logger LOGGER = LoggerFactory.getLogger(XslTransformation.class);

	/**
	 * Basic Transformer initialization without parameters
	 * 
	 * @param input    : the input xml file
	 * @param xslSheet : the xsl stylesheet that will be used
	 * @param output   : the xml output that will be created
	 * @throws Exception : if the factory couldn't be found or if the paths are
	 *                   incorrect
	 */
	public void transform(InputStream input, InputStream xslSheet, OutputStream output) throws Exception {
		LOGGER.debug("Using the basic transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		// transformer.setURIResolver(new ClasspathURIResolver());
		xslTransform(transformer, input, output);
	}

	/**
	 * Basic Transformer initialization with config in2out properties file
	 * 
	 * @param input    : the input xml file
	 * @param xslSheet : the xsl stylesheet that will be used
	 * @param output   : the xml output that will be created
	 * @param in2out   : the in2out information for config file
	 * @throws Exception : if the factory couldn't be found or if the paths are
	 *                   incorrect
	 */
	public void transformCleaning(InputStream input, InputStream xslSheet, OutputStream output, String in2out)
			throws Exception {
		String default_properties_file = null;
		if (in2out == "ddi2xforms") {
			default_properties_file = Constants.CONFIG_DDI2XFORMS;
		}
		if (in2out == "ddi2fodt") {
			default_properties_file = Constants.CONFIG_DDI2FODT;
		}
		if (in2out == "ddi2fo") {
			default_properties_file = Constants.CONFIG_DDI2FO;
		}
		if (in2out == "ddi2lunatic-xml") {
			default_properties_file = Constants.CONFIG_DDI2LUNATIC_XML;
		}
		LOGGER.debug("Using the basic transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.PROPERTIES_FILE, default_properties_file);
		// transformer.setURIResolver(new ClasspathURIResolver());
		xslTransform(transformer, input, output);
	}

	/**
	 * Incorporation Transformer initialization with its parameters
	 * 
	 * @param input                  : the input xml file
	 * @param xslSheet               : the xsl stylesheet that will be used
	 * @param output                 : the xml output that will be created
	 * @param generatedFileParameter : Incorporation xsl parameter
	 * @throws Exception : if the factory couldn't be found or if the paths are
	 *                   incorrect
	 */
	public void transformIncorporation(InputStream input, InputStream xslSheet, OutputStream output,
			File generatedFileParameter) throws Exception {
		LOGGER.debug("Using the incorporation transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.INCORPORATION_GENERATED_FILE, generatedFileParameter.toURI());
		xslTransform(transformer, input, output);
	}

	
	/**
	 * 
	 * @param input                 : the input xml file
	 * @param xslSheet              : the xsl stylesheet that will be used
	 * @param output                : the xml output that will be created
	 * @param outputFolderParameter : Markdown to XHTML xsl parameter
	 * @throws Exception : if the factory couldn't be found or if the paths are
	 *                   incorrect
	 */
	public void transformOutFolder(InputStream input, InputStream xslSheet, OutputStream output,
			File outputFolderParameter) throws Exception {
		LOGGER.debug("Using the transformOutFolder to XHTML transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.OUTPUT_FOLDER, outputFolderParameter);
		xslTransform(transformer, input, output);
	}
	


	public void transformWithParameters(InputStream input, InputStream xslSheet, OutputStream output, byte[] parameters)
			throws Exception {
		InputStream parametersIS = null;
		LOGGER.debug("Using transformer with parameters");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_FILE, Constants.PARAMETERS_DEFAULT);
		if (parameters != null) {
			parametersIS = new ByteArrayInputStream(parameters);
			Source source = new StreamSource(parametersIS);
			transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_NODE, source);
		}
		xslTransform(transformer, input, output);
		if (parameters != null) {
			parametersIS.close();
		}
	}

	
	/**
	 * Multimodal Selection Transformer initialization with its parameters
	 * 
	 * @param input          : the input xml file
	 * @param xslSheet       : the xsl stylesheet that will be used
	 * @param output         : the xml output that will be created
	 * @param in2out         : main transformation
	 * @throws Exception : if the factory couldn't be found or if the paths are
	 *                   incorrect
	 */
	public void transformModalSelection(InputStream input, InputStream xslSheet, OutputStream output, String in2out)
			throws Exception {
		String outputFormat = "";
		if (in2out.equals("ddi2xforms")) {
			outputFormat = "xforms";
		}
		if (in2out.equals("ddi2fodt")) {
			outputFormat = "fodt";
		}
		if (in2out.equals("ddi2fo")) {
			outputFormat = "fo";
		}
		if (in2out.equals("ddi2lunatic-xml")) {
			outputFormat = "lunatic-xml";
		}

		LOGGER.debug("Using the multimodal selection transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.MULTIMODAL_SELECTION_OUTPUT_FORMAT, outputFormat);
		xslTransform(transformer, input, output);

	}

	public void transformIn2Out(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			byte[] parameters, String propertiesFile) throws Exception {
		InputStream parametersIS = null;
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, propertiesFile);
		transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_FILE, Constants.PARAMETERS_DEFAULT);
		if (parameters != null) {
			LOGGER.info("Using specifics parameters");
			parametersIS = new ByteArrayInputStream(parameters);
			Source source = new StreamSource(parametersIS);
			transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_NODE, source);
		}
		transformer.setParameter(XslParameters.IN2OUT_LABELS_FOLDER, Constants.LABELS_FOLDER);
		LOGGER.debug(String.format("Transformer parameters are: %s, %s",
				transformer.getParameter(XslParameters.IN2OUT_PROPERTIES_FILE),
				transformer.getParameter(XslParameters.IN2OUT_PARAMETERS_FILE),
				transformer.getParameter(XslParameters.IN2OUT_LABELS_FOLDER)));
		xslTransform(transformer, inputFile, outputFile);
		if (parameters != null) {
			parametersIS.close();
		}
	}


	public void transformFOToStep4FO(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			String surveyName, String formName, byte[] parameters) throws Exception {

		InputStream parametersIS = null;
		LOGGER.info("Inserting generic pages in the FO from survey's parameters");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.IN2OUT_SURVEY_NAME, surveyName);
		transformer.setParameter(XslParameters.IN2OUT_FORM_NAME, formName);
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, Constants.CONFIG_DDI2FO);
		transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_FILE, Constants.PARAMETERS_DEFAULT);
		if (parameters != null) {
			parametersIS = new ByteArrayInputStream(parameters);
			Source source = new StreamSource(parametersIS);
			transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_NODE, source);
		}
		xslTransform(transformer, inputFile, outputFile);
		if (parameters != null) {
			parametersIS.close();
		}
	}

	/* POST transformations */
	// FR

	public void transformBrowsingXforms(InputStream inputFile, OutputStream outputFile, InputStream xslSheet)
			throws Exception {
		LOGGER.info("Post-processing browsing for FR transformation.");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.IN2OUT_LABELS_FOLDER, Constants.LABELS_FOLDER);
		LOGGER.debug(String.format("Transformer parameter is: %s",
				transformer.getParameter(XslParameters.IN2OUT_LABELS_FOLDER)));
		xslTransform(transformer, inputFile, outputFile);
	}

	public void transformInseeModelXforms(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			InputStream mappingFile) throws Exception {
		LOGGER.info("Post-processing for FR transformation with mapping.xml file.");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.IN2OUT_MAPPING_FILE, Constants.MAPPING_DEFAULT);
		if (mappingFile != null) {
			Source source = new StreamSource(mappingFile);
			transformer.setParameter(XslParameters.IN2OUT_MAPPING_FILE_NODE, source);
		}
		LOGGER.debug(String.format("Transformer parameter is: %s",
				transformer.getParameter(XslParameters.IN2OUT_MAPPING_FILE_NODE)));
		xslTransform(transformer, inputFile, outputFile);

	}

	public void transformWithMetadata(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			byte[] parameters, byte[] metadata) throws Exception {
		InputStream parametersIS = null;
		InputStream metadataIS = null;
		LOGGER.info("Post-processing for FR transformation with parameter file and metadata file");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, Constants.CONFIG_DDI2XFORMS);
		transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_FILE, Constants.PARAMETERS_DEFAULT);
		transformer.setParameter(XslParameters.IN2OUT_METADATA_FILE, Constants.METADATA_DEFAULT);
		if (metadata != null) {
			metadataIS = new ByteArrayInputStream(metadata);
			Source source = new StreamSource(metadataIS);
			transformer.setParameter(XslParameters.IN2OUT_METADATA_NODE, source);
		}
		if (parameters != null) {
			parametersIS = new ByteArrayInputStream(parameters);
			Source source = new StreamSource(parametersIS);
			transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_NODE, source);
		}
		transformer.setErrorListener(new EnoErrorListener());
		xslTransform(transformer, inputFile, outputFile);
	}


	public void transformSimplePost(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			byte[] parameters, String in2out) throws Exception {
		String config = null;
		if (in2out.equals("ddi2xforms")) {
			config = Constants.CONFIG_DDI2XFORMS;
		}
		if (in2out.equals("ddi2fodt")) {
			config = Constants.CONFIG_DDI2FODT;
		}
		if (in2out.equals("ddi2lunaticXML")) {
			config = Constants.CONFIG_DDI2LUNATIC_XML;
		}
		if (in2out.equals("ddi2fo")) {
			config = Constants.CONFIG_DDI2FO;
		}
		InputStream parametersIS = null;
		LOGGER.info("transformSimplePost transformation with parameter file");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, config);
		transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_FILE, Constants.PARAMETERS_DEFAULT);
		if (parameters != null) {
			parametersIS = new ByteArrayInputStream(parameters);
			Source source = new StreamSource(parametersIS);
			transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_NODE, source);
		}
		transformer.setErrorListener(new EnoErrorListener());
		xslTransform(transformer, inputFile, outputFile);
	}


	public void transformSimple(InputStream inputFile, OutputStream outputFile, InputStream xslSheet) throws Exception {
		LOGGER.info("Simple transformation");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		xslTransform(transformer, inputFile, outputFile);
	}


}
