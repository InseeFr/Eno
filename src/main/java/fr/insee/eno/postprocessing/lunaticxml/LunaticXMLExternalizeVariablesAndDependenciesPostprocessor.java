package fr.insee.eno.postprocessing.lunaticxml;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import fr.insee.eno.exception.Utils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * Customization of JS postprocessor.
 */
public class LunaticXMLExternalizeVariablesAndDependenciesPostprocessor extends LunaticXMLPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(LunaticXMLExternalizeVariablesAndDependenciesPostprocessor.class);

	private static final String styleSheetPath = Constants.TRANSFORMATIONS_EXTERNALIZE_VARIABLES_AND_DEPENDENCIES_LUNATIC_XML;

	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {
		return this.process(input, parameters, surveyName,  styleSheetPath, Constants.EXTERNALIZE_VARIABLES_LUNATIC_XML_EXTENSION);
		
	}

	public String toString() {
		return PostProcessing.LUNATIC_XML_EXTERNALIZE_VARIABLES.name();
	}

}
