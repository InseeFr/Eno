package fr.insee.eno;

import com.google.inject.Inject;

import fr.insee.eno.generation.Generator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

/**
 * Orchestrates the whole generation process.
 * */
public class GenerationService {
	private final Preprocessor preprocessor;
	private final Generator generator;
	private final Postprocessor postprocessor;
	
	@Inject
	public GenerationService(Preprocessor preprocessor, Generator generator, Postprocessor postprocessor) {
		this.preprocessor = preprocessor;
		this.generator = generator;
		this.postprocessor = postprocessor;
	}
	
	
}
