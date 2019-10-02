package fr.insee.eno.generation;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.transform.xsl.XslParameters;
import fr.insee.eno.transform.xsl.XslTransformation;

public class DDI2PoguesXMLGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(DDI2PoguesXMLGenerator.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public File generate(File finalInput, byte[] parameters, String surveyName) throws Exception {
		//TODO:implement this generator
		return null;
	}

	/**
	 * @param finalInput
	 * @return
	 */
	private String getFormNameFolder(File finalInput) {
		String formNameFolder;
		formNameFolder = FilenameUtils.getBaseName(finalInput.getAbsolutePath());
		formNameFolder = FilenameUtils.removeExtension(formNameFolder);
		formNameFolder = formNameFolder.replace(XslParameters.TITLED_EXTENSION, "");
		return formNameFolder;
	}

	public String in2out() {
		return "xml-pogues2ddi";
	}
}
