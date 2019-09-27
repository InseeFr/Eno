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
	
	public GenerationService setPipeLine(Pipeline pipeline) throws Exception;
	
	public Postprocessor[] setPostProcessors(List<PostProcessing> postProcessings);
	
	public Preprocessor[] setPreProcessors(List<PreProcessing> preProcessings);
	
	public Generator setGenerator(InFormat inFormat, OutFormat outFormat);
	
	public Postprocessor getPostPorcessor(PostProcessing postProcessing);
	public Preprocessor getPrePorcessor(PreProcessing preProcessing);

}
