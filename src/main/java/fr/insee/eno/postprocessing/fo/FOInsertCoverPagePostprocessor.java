package fr.insee.eno.postprocessing.fo;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import fr.insee.eno.exception.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A PDF post processing
 */
public class FOInsertCoverPagePostprocessor extends FOPostProcessor {

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, survey, Constants.TRANSFORMATIONS_COVER_PAGE_FO_4PDF, Constants.COVER_PAGE_FO_EXTENSION);
	}

	public String toString() {
		return PostProcessing.FO_INSERT_COVER_PAGE.name();
	}

}
