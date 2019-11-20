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
	 * @return
	 * @throws Exception
	 */
	public GenerationService setPipeLine(Pipeline pipeline) throws Exception;
	
	/**
	 * 
	 * @param postProcessings
	 * @return
	 */
	public Postprocessor[] setPostProcessors(List<PostProcessing> postProcessings);
	
	/**
	 * 
	 * @param preProcessings
	 * @return
	 */
	public Preprocessor[] setPreProcessors(List<PreProcessing> preProcessings);
	
	/**
	 * 
	 * @param inFormat
	 * @param outFormat
	 * @return
	 */
	public Generator setGenerator(InFormat inFormat, OutFormat outFormat);
	
	/**
	 * 
	 * @param postProcessing
	 * @return
	 */
	public Postprocessor getPostPorcessor(PostProcessing postProcessing);
	
	/**
	 * 
	 * @param preProcessing
	 * @return
	 */
	public Preprocessor getPrePorcessor(PreProcessing preProcessing);

}
