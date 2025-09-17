package fr.insee.eno.postprocessing.fo;

import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * PDF postprocessor.
 */
public class FOTableColumnPostprocessorFake implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(FOTableColumnPostprocessorFake.class);

	@Override
	public ByteArrayOutputStream process(InputStream inputStream, byte[] parameters, String survey) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(inputStream.readAllBytes());
		inputStream.close();
		logger.debug("End of TableColumn post-processing (Fake)");
		return byteArrayOutputStream;

	}

	public String toString() {
		return PostProcessing.FO_TABLE_COLUMN.name();
	}
}
