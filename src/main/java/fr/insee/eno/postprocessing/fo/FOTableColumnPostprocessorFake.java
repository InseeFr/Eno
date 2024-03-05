package fr.insee.eno.postprocessing.fo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;

/**
 * PDF postprocessor.
 */
public class FOTableColumnPostprocessorFake implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(FOTableColumnPostprocessorFake.class);

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream byteArrayInputStream, byte[] parameters, String survey) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(byteArrayInputStream.readAllBytes());
		byteArrayInputStream.close();
		logger.debug("End of TableColumn post-processing (Fake)");
		return byteArrayOutputStream;

	}

	public String toString() {
		return PostProcessing.FO_TABLE_COLUMN.name();
	}
}
