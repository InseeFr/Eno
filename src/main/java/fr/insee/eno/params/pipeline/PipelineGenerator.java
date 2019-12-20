package fr.insee.eno.params.pipeline;

import java.util.List;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.Generator;
import fr.insee.eno.parameters.InFormat;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.Pipeline;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

public interface PipelineGenerator {
	
	/**
	 * 
	 * @param pipeline
	 * @return a GenerationService with the Eno transformation (PreProcessor, Generator and PostProcessor) according to the param.
	 * @throws Exception
	 */
	public GenerationService setPipeLine(Pipeline pipeline) throws Exception;
	
	/**
	 * 
	 * @param postProcessings
	 * @return a list of Postprocessor (Java class) according to a List of PostProcessing (Enum)
	 */
	public Postprocessor[] setPostProcessors(List<PostProcessing> postProcessings);
	
	/**
	 * 
	 * @param a list of Preprocessor (Java class) according to a List of PreProcessing (Enum)
	 * @return
	 */
	public Preprocessor[] setPreProcessors(List<PreProcessing> preProcessings);
	
	/**
	 * 
	 * @param inFormat
	 * @param outFormat
	 * @return the Generator according to the inFormat(Enum) and outFormat(Enum)
	 */
	public Generator setGenerator(InFormat inFormat, OutFormat outFormat);
	
	/**
	 * Linking function : Postprocessor with PostProcessing
	 * @param postProcessing
	 * @return the Postprocessor java class according to PostProcessing(Enum)
	 */
	public Postprocessor getPostPorcessor(PostProcessing postProcessing);
	
	/**
	 * Linking function : Preprocessor with PreProcessing
	 * @param postProcessing
	 * @return the Preprocessor java class according to PreProcessing(Enum)
	 */
	public Preprocessor getPrePorcessor(PreProcessing preProcessing);

}
