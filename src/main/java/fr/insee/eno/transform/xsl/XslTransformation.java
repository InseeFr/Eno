package fr.insee.eno.transform.xsl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

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
	 * Main Saxon transformation method
	 * 
	 * @param transformer : The defined transformer with his embedded parameters
	 *                    (defined in the other methods of this class)
	 * @param xmlInput    : The input xml file where the XSLT will be applied
	 * @param xmlOutput   : The output xml file after the transformation
	 * @throws Exception : Mainly if the input/output files path are incorrect
	 */
	public void xslTransform(Transformer transformer, InputStream xmlInput, OutputStream xmlOutput) throws Exception {
		LOGGER.debug("Starting xsl transformation -Input : " + xmlInput + " -Output : " + xmlOutput);
		transformer.transform(new StreamSource(xmlInput), new StreamResult(xmlOutput));
	}

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
		if (in2out == "ddi2fr") {
			default_properties_file = Constants.CONFIG_DDI2FR;
		}
		if (in2out == "ddi2odt") {
			default_properties_file = Constants.CONFIG_DDI2ODT;
		}
		if (in2out == "ddi2pdf") {
			default_properties_file = Constants.CONFIG_DDI2PDF;
		}
		if (in2out == "ddi2js") {
			default_properties_file = Constants.CONFIG_DDI2JS;
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
	 * Markdown to XHTML with its parameters
	 * 
	 * @param input                 : the input xml file
	 * @param xslSheet              : the xsl stylesheet that will be used
	 * @param output                : the xml output that will be created
	 * @param outputFolderParameter : Markdown to XHTML xsl parameter
	 * @throws Exception : if the factory couldn't be found or if the paths are
	 *                   incorrect
	 */
	public void transformMw2XHTML(InputStream input, InputStream xslSheet, OutputStream output,
			File outputFolderParameter) throws Exception {
		LOGGER.debug("Using the Markdown to XHTML transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.MW2XHTML_OUTPUT_FOLDER, outputFolderParameter);
		xslTransform(transformer, input, output);
	}

	/**
	 * TweakXhtmlForDdi with its parameters
	 * 
	 * @param input                 : the input xml file
	 * @param xslSheet              : the xsl stylesheet that will be used
	 * @param output                : the xml output that will be created
	 * @param outputFolderParameter : TweakXhtmlForDdi xsl parameter
	 * @throws Exception : if the factory couldn't be found or if the paths are
	 *                   incorrect
	 */
	public void transformTweakXhtmlForDdi(InputStream input, InputStream xslSheet, OutputStream output,
			File outputFolderParameter) throws Exception {
		LOGGER.debug("Using the TweakXhtmlForDdi transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.TWEAK_XHTML_FOR_DDI_OUTPUT_FOLDER, outputFolderParameter);
		xslTransform(transformer, input, output);
	}

	/**
	 * Dereferencing Transformer initialization with its parameters
	 * 
	 * @param input                 : the input xml file
	 * @param xslSheet              : the xsl stylesheet that will be used
	 * @param output                : the xml output that will be created
	 * @param outputFolderParameter : Dereferencing xsl parameter
	 * @throws Exception : if the factory couldn't be found or if the paths are
	 *                   incorrect
	 */
	public void transformDereferencing(InputStream input, InputStream xslSheet, OutputStream output,
			File outputFolderParameter) throws Exception {
		LOGGER.debug("Using the dereferencing transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.DEREFERENCING_OUTPUT_FOLDER, outputFolderParameter);
		xslTransform(transformer, input, output);
	}

	public void transformMapping(InputStream input, InputStream xslSheet, OutputStream output, byte[] parameters)
			throws Exception {
		InputStream parametersIS = null;
		LOGGER.debug("Using the Mapping transformer");
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
	 * Titling Transformer initialization with its parameters
	 * 
	 * @param input          : the input xml file
	 * @param xslSheet       : the xsl stylesheet that will be used
	 * @param output         : the xml output that will be created
	 * @param parametersFile : Titling xsl parameter
	 * @throws Exception : if the factory couldn't be found or if the paths are
	 *                   incorrect
	 */
	public void transformTitling(InputStream input, InputStream xslSheet, OutputStream output, byte[] parameters)
			throws Exception {
		InputStream parametersIS = null;
		LOGGER.debug("Using the titling transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
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

	private void transformIn2Out(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
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

	public void transformDDI2FR(InputStream inputFile, OutputStream outputFile, InputStream xslSheet, byte[] parameters)
			throws Exception {
		LOGGER.info("Producing a basic XForms from the DDI spec");
		transformIn2Out(inputFile, outputFile, xslSheet, parameters, Constants.CONFIG_DDI2FR);

	}

	public void transformDDI2ODT(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			byte[] parameters) throws Exception {
		LOGGER.info("Producing a basic ODT from the DDI spec");
		transformIn2Out(inputFile, outputFile, xslSheet, parameters, Constants.CONFIG_DDI2ODT);

	}

	public void transformDDI2PDF(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			byte[] parameters) throws Exception {
		LOGGER.info("Producing a basic PDF (Fo) from the DDI spec");

		transformIn2Out(inputFile, outputFile, xslSheet, parameters, Constants.CONFIG_DDI2PDF);

	}

	public void transformDDI2JS(InputStream inputFile, OutputStream outputFile, InputStream xslSheet, byte[] parameters)
			throws Exception {
		LOGGER.info("Producing a JS (xml file) from the DDI spec");

		transformIn2Out(inputFile, outputFile, xslSheet, parameters, Constants.CONFIG_DDI2JS);

	}

	public void transformPoguesXML2DDI(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			byte[] parameters) throws Exception {
		LOGGER.info("Producing a basic DDI from the PoguesXML spec");
		transformIn2Out(inputFile, outputFile, xslSheet, parameters, Constants.CONFIG_POGUES_XML2DDI);

	}

	private void transformBrowsingin2Out(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			File labelFolder) throws Exception {
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.IN2OUT_LABELS_FOLDER, Constants.LABELS_FOLDER);
		LOGGER.debug(String.format("Transformer parameter is: %s",
				transformer.getParameter(XslParameters.IN2OUT_LABELS_FOLDER)));
		xslTransform(transformer, inputFile, outputFile);
	}

	public void transformBrowsingDDI2FR(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			File labelFolder) throws Exception {
		LOGGER.info("Include the navigation elements into the XForms questionnaire");
		transformBrowsingin2Out(inputFile, outputFile, xslSheet, labelFolder);
	}

	public void transformBrowsingDDI2ODT(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			File labelFolder) throws Exception {
		LOGGER.info("Include the navigation elements into the ODT questionnaire");
		transformBrowsingin2Out(inputFile, outputFile, xslSheet, labelFolder);
	}

	public void transformBrowsingDDI2JS(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			File labelFolder) throws Exception {
		LOGGER.info("Include the navigation elements into the JS questionnaire");
		transformBrowsingin2Out(inputFile, outputFile, xslSheet, labelFolder);
	}

	public void transformFOToStep1FO(InputStream inputFile, OutputStream outputFile, InputStream xslSheet)
			throws Exception {
		LOGGER.info("Producing a custom FO (PDF) from the FO with conditioning variables");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, Constants.CONFIG_DDI2PDF);
		LOGGER.debug(String.format("FO Transformer parameters file is: %s",
				transformer.getParameter(Constants.CONFIG_DDI2PDF)));
		xslTransform(transformer, inputFile, outputFile);
	}

	public void transformFOToStep2FO(InputStream inputFile, OutputStream outputFile, InputStream xslSheet)
			throws Exception {
		LOGGER.info("Producing a specific treatment FO from survey's parameters");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());

		xslTransform(transformer, inputFile, outputFile);
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
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, Constants.CONFIG_DDI2PDF);
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

	public void transformBrowsingFr(InputStream inputFile, OutputStream outputFile, InputStream xslSheet)
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

	public void transformModelColtraneFr(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
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
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, Constants.CONFIG_DDI2FR);
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

	public void transformWithFRSpecificTreatment(InputStream inputFile, OutputStream outputFile,
			InputStream specificTreatmentXsl, byte[] parameters) throws Exception {
		InputStream parametersIS = null;
		LOGGER.info("Specific treatment as post-processing for FR transformation with parameter file");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(specificTreatmentXsl));
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, Constants.CONFIG_DDI2FR);
		transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_FILE, Constants.PARAMETERS_DEFAULT);

		if (parameters != null) {
			parametersIS = new ByteArrayInputStream(parameters);
			Source source = new StreamSource(parametersIS);
			transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_NODE, source);
		}
		transformer.setErrorListener(new EnoErrorListener());
		xslTransform(transformer, inputFile, outputFile);
	}
	
	public void transformWithPDFSpecificTreatment(InputStream inputFile, OutputStream outputFile,
			InputStream specificTreatmentXsl, byte[] parameters) throws Exception {
		InputStream parametersIS = null;
		LOGGER.info("Specific treatment as post-processing for PDF transformation with parameter file");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(specificTreatmentXsl));
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, Constants.CONFIG_DDI2PDF);
		transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_FILE, Constants.PARAMETERS_DEFAULT);

		if (parameters != null) {
			parametersIS = new ByteArrayInputStream(parameters);
			Source source = new StreamSource(parametersIS);
			transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_NODE, source);
		}
		transformer.setErrorListener(new EnoErrorListener());
		xslTransform(transformer, inputFile, outputFile);
	}

	public void transformFRToFRSimplePost(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			byte[] parameters) throws Exception {
		InputStream parametersIS = null;
		LOGGER.info("Post-processing for FR transformation with parameter file");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, Constants.CONFIG_DDI2FR);
		transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_FILE, Constants.PARAMETERS_DEFAULT);
		if (parameters != null) {
			parametersIS = new ByteArrayInputStream(parameters);
			Source source = new StreamSource(parametersIS);
			transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_NODE, source);
		}
		transformer.setErrorListener(new EnoErrorListener());
		xslTransform(transformer, inputFile, outputFile);
	}
	
	public void transformJSToJSSimplePost(InputStream inputFile, OutputStream outputFile, InputStream xslSheet,
			byte[] parameters) throws Exception {
		InputStream parametersIS = null;
		LOGGER.info("Post-processing for JS transformation with parameter file");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, Constants.CONFIG_DDI2JS);
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
		LOGGER.info("Simple post-processing for FR transformation");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		xslTransform(transformer, inputFile, outputFile);
	}

	// JS

	public void transformJSToJSPost(InputStream inputFile, OutputStream outputFile, InputStream xslSheet)
			throws Exception {
		LOGGER.info("Post-processing for JS transformation");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		xslTransform(transformer, inputFile, outputFile);
	}
	
	public void mergeEnoParameters(InputStream newParameters, OutputStream finalParameters, InputStream xslSheet) throws Exception {
		LOGGER.info("Merging new eno paramters with default parameters.");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		tFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setErrorListener(new EnoErrorListener());
		xslTransform(transformer, newParameters, finalParameters);

	}

}
