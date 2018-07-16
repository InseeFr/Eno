package fr.insee.eno.postprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.CalculatorService;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * PDF postprocessor.
 */
public class PDFPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(PDFPostprocessor.class);

	// FIXME Inject !
	private static CalculatorService serviceTableColumnSize = new CalculatorService();


	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();
	
	@Override
	public File process(File input, File parametersFile, String survey) throws Exception {

		String outputForFO = FilenameUtils.removeExtension(input.getPath()) + Constants.FINAL_PDF_EXTENSION;

		String confFilePath = null;
		
		if(Constants.PDF_PLUGIN_XML_CONF_FILE !=null){
			logger.debug("Get conf file : "+Constants.PDF_PLUGIN_XML_CONF_FILE.getAbsolutePath());
			confFilePath =Constants.PDF_PLUGIN_XML_CONF_FILE.getAbsolutePath();
		}else{
			InputStream isConfFile = getClass().getClassLoader().getResourceAsStream("/config/plugins-conf.xml");
			confFilePath = FilenameUtils.removeExtension(input.getPath())+"-conf.xml";
			File confFile = new File(confFilePath);
			java.nio.file.Files.copy(
					isConfFile, 
					confFile.toPath(), 
				      StandardCopyOption.REPLACE_EXISTING);
			isConfFile.close();
			logger.debug("Get conf file : "+confFile.getAbsolutePath());
		}
				

		
		
		serviceTableColumnSize.tableColumnSizeProcessor(input.getAbsolutePath(), outputForFO,
				confFilePath);
		File outputForFOFile = new File(outputForFO);

		File outputCustomFOFile = new File (FilenameUtils.removeExtension(input.getPath()) + Constants.CUSTOM_FO_EXTENSION);

		InputStream PUBLIPOSTAGE_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_CUSTOMIZATION_FO_4PDF);
		saxonService.transform(
				FileUtils.openInputStream(outputForFOFile),
				PUBLIPOSTAGE_XSL, 
				FileUtils.openOutputStream(outputCustomFOFile));
		
		logger.info("end of Customization of fo file : " + input.getAbsolutePath());

		return outputCustomFOFile;

	}
}
