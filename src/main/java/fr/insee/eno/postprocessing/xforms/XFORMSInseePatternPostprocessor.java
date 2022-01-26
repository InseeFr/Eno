package fr.insee.eno.postprocessing.xforms;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;

public class XFORMSInseePatternPostprocessor extends XFORMSPostProcessorWithMetadata {

	private static final Logger logger = LoggerFactory.getLogger(XFORMSInseePatternPostprocessor.class);

	private static final String styleSheetPath = Constants.UTIL_XFORMS_INSEE_PATTERN_XSL;
	
	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, null, survey);
	}

    @Override
    public File process(File input, byte[] parameters, byte[] metadata, String survey) throws Exception {
		return this.process(input, parameters, survey,  styleSheetPath, Constants.FIX_ADHERENCE_XFORMS_EXTENSION,metadata);
	}
	
	public String toString() {
		return PostProcessing.XFORMS_INSEE_PATTERN.name();
	}

}
