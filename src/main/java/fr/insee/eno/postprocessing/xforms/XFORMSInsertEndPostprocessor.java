package fr.insee.eno.postprocessing.xforms;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;

public class XFORMSInsertEndPostprocessor extends XFORMSPostProcessorWithMetadata {

	private static final Logger logger = LoggerFactory.getLogger(XFORMSInsertEndPostprocessor.class);

	private static final String styleSheetPath = Constants.UTIL_XFORMS_INSERT_END_XSL;

	
	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, null, survey);
	}

    @Override
    public File process(File input, byte[] parameters, byte[] metadata, String survey) throws Exception {
		return this.process(input, parameters, survey,  styleSheetPath, Constants.INSERT_END_XFORMS_EXTENSION,metadata);
	}

}
