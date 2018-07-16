package fr.insee.eno.postprocessing;

import java.io.File;

/**
 * Transforms to the generated output depending on specified (parameterized)
 * options.
 */
public interface Postprocessor {
	
	File process(File input, File parametersFile)  throws Exception;

}
