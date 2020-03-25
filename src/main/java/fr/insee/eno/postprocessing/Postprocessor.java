package fr.insee.eno.postprocessing;

import java.io.File;

/**
 * Transforms to the generated output depending on specified (parameterized)
 * options.
 */
public interface Postprocessor {

	File process(File input, byte[] parametersFile, String survey) throws Exception;

	default File process(File input, byte[] parametersFile, byte[] metadata, String survey) throws Exception{
		return this.process(input,parametersFile,survey);
	}
	
	default File process(File input, byte[] parametersFile, byte[] metadata, byte[] specificTreatmentXsl, String survey) throws Exception{
		return this.process(input,parametersFile,metadata,survey);
	}
	
	default File process(File input, byte[] parametersFile, byte[] metadata, byte[] specificTreatmentXsl, byte[] mapping, String survey) throws Exception{
		return this.process(input,parametersFile,metadata,specificTreatmentXsl,survey);
	}
	
	public String toString();
}
