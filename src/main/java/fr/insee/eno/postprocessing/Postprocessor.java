package fr.insee.eno.postprocessing;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Transforms to the generated output depending on specified (parameterized)
 * options.
 */
public interface Postprocessor {

	ByteArrayOutputStream process(InputStream input, byte[] parametersFile, String survey) throws Exception;

	default ByteArrayOutputStream process(InputStream input, byte[] parametersFile, byte[] metadata, String survey) throws Exception{
		return this.process(input,parametersFile,survey);
	}
	
	default ByteArrayOutputStream process(InputStream input, byte[] parametersFile, byte[] metadata, byte[] specificTreatmentXsl, String survey) throws Exception{
		return this.process(input,parametersFile,metadata,survey);
	}
	
	default ByteArrayOutputStream process(InputStream input, byte[] parametersFile, byte[] metadata, byte[] specificTreatmentXsl, byte[] mapping, String survey) throws Exception{
		return this.process(input, parametersFile,metadata,specificTreatmentXsl,survey);
	}
	
	public String toString();
}
