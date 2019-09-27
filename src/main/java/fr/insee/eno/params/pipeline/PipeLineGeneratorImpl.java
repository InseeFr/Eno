package fr.insee.eno.params.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2FRGenerator;
import fr.insee.eno.generation.DDI2JSGenerator;
import fr.insee.eno.generation.DDI2ODTGenerator;
import fr.insee.eno.generation.DDI2PDFGenerator;
import fr.insee.eno.generation.DDI2PoguesXMLGenerator;
import fr.insee.eno.generation.Generator;
import fr.insee.eno.generation.PoguesXML2DDIGenerator;
import fr.insee.eno.parameters.InFormat;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.Pipeline;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.ddi.DDIPostprocessor;
import fr.insee.eno.postprocessing.fr.FRBrowsingPostprocessor;
import fr.insee.eno.postprocessing.fr.FREditPatronPostprocessor;
import fr.insee.eno.postprocessing.fr.FRFixAdherencePostprocessor;
import fr.insee.eno.postprocessing.fr.FRIdentificationPostprocessor;
import fr.insee.eno.postprocessing.fr.FRInsertEndPostprocessor;
import fr.insee.eno.postprocessing.fr.FRInsertGenericQuestionsPostprocessor;
import fr.insee.eno.postprocessing.fr.FRInsertWelcomePostprocessor;
import fr.insee.eno.postprocessing.fr.FRModeleColtranePostprocessor;
import fr.insee.eno.postprocessing.fr.FRSpecificTreatmentPostprocessor;
import fr.insee.eno.postprocessing.js.JSExternalizeVariablesPostprocessor;
import fr.insee.eno.postprocessing.js.JSSortComponentsPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFEditStructurePagesPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFInsertAccompanyingMailsPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFInsertCoverPagePostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFInsertEndQuestionPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFMailingPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFSpecificTreatmentPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFTableColumnPostprocessorFake;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDIMappingPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.PoguesXMLPreprocessorGoToTreatment;
import fr.insee.eno.preprocessing.Preprocessor;

public class PipeLineGeneratorImpl implements PipelineGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(PipeLineGeneratorImpl.class);

	@Override
	public GenerationService setPipeLine(Pipeline pipeline) throws Exception {
		
		Preprocessor[] preprocessors = setPreProcessors(pipeline.getPreProcessing());
		Generator generator = setGenerator(pipeline.getInFormat(), pipeline.getOutFormat());
		Postprocessor[] postprocessors = setPostProcessors(pipeline.getPostProcessing());
		
		logger.info("PreProccesings : "+preprocessors.length);
		logger.info("Core generation : "+generator.in2out());
		logger.info("PostProccesings : "+postprocessors.length);
		return new GenerationService(preprocessors, generator, postprocessors);
	}

	@Override
	public Postprocessor[] setPostProcessors(List<PostProcessing> postProcessings) {
		List<Postprocessor> postprocessors = new ArrayList<Postprocessor>();
		if(!postProcessings.isEmpty()) {
			for(PostProcessing postProcessing : postProcessings) {
				postprocessors.add(getPostPorcessor(postProcessing));
			}
		}
		else {
			postprocessors.add(new NoopPostprocessor());
		}
		
		return postprocessors.toArray(new Postprocessor[postprocessors.size()]);
	}

	@Override
	public Preprocessor[] setPreProcessors(List<PreProcessing> preProcessings) {
		List<Preprocessor> preprocessors = new ArrayList<Preprocessor>();
		for(PreProcessing preProcessing : preProcessings) {
			preprocessors.add(getPrePorcessor(preProcessing));
		}
		return preprocessors.toArray(new Preprocessor[preprocessors.size()]);
	}

	
	
	
	
	@Override
	public Generator setGenerator(InFormat inFormat, OutFormat outFormat) {
		Generator generator=null;
		
		switch (inFormat) {
		case DDI:
			switch (outFormat) {
			case DDI:
				// DDI32ToDDI33
				generator=null; //TODO : add new IdentityGenerator()
				break;
			case FR:
				generator = new DDI2FRGenerator();
				break;
			case JS:
				generator = new DDI2JSGenerator();
				break;
			case ODT:
				generator = new DDI2ODTGenerator();
				break;
			case PDF:
				generator = new DDI2PDFGenerator();
				break;
			case POGUES_XML:
				generator = new DDI2PoguesXMLGenerator();
				break;
			}
			break;
		case POGUES_XML:
			switch (outFormat) {
			case DDI:
				generator = new PoguesXML2DDIGenerator();
				break;
			default:
				generator=null; //TODO : add new IdentityGenerator()
				break;
			}
		case FR:
			generator=null; //TODO : add new IdentityGenerator()
			break;
		}
		return generator;
	}

	@Override
	public Postprocessor getPostPorcessor(PostProcessing postProcessing) {
		Postprocessor postprocessor = null;
		switch (postProcessing) {
		case DDI_MARKDOWN_TO_XHTML:
			postprocessor = new DDIPostprocessor();
			break;
		case FR_BROWSING:
			postprocessor = new FRBrowsingPostprocessor();
			break;
		case FR_EDIT_PATRON:
			postprocessor = new FREditPatronPostprocessor();
			break;
		case FR_FIX_ADHERENCE:
			postprocessor = new FRFixAdherencePostprocessor();
			break;
		case FR_IDENTIFICATION:
			postprocessor = new FRIdentificationPostprocessor();
			break;
		case FR_INSERT_END:
			postprocessor = new FRInsertEndPostprocessor();
			break;
		case FR_INSERT_GENERIC_QUESTIONS:
			postprocessor = new FRInsertGenericQuestionsPostprocessor();
			break;
		case FR_INSERT_WELCOME:
			postprocessor = new FRInsertWelcomePostprocessor();
			break;
		case FR_MODELE_COLTRANE:
			postprocessor = new FRModeleColtranePostprocessor();
			break;
		case FR_SPECIFIC_TREATMENT:
			postprocessor = new FRSpecificTreatmentPostprocessor();
			break;
		case PDF_EDIT_STRUCTURE_PAGES:
			postprocessor = new PDFEditStructurePagesPostprocessor();
			break;
		case PDF_INSERT_ACCOMPANYING_MAILS:
			postprocessor = new PDFInsertAccompanyingMailsPostprocessor();
			break;
		case PDF_INSERT_COVER_PAGE:
			postprocessor = new PDFInsertCoverPagePostprocessor();
			break;
		case PDF_INSERT_END_QUESTION:
			postprocessor = new PDFInsertEndQuestionPostprocessor();
			break;
		case PDF_MAILING:
			postprocessor = new PDFMailingPostprocessor();
			break;
		case PDF_SPECIFIC_TREATMENT:
			postprocessor = new PDFSpecificTreatmentPostprocessor();
			break;
		case PDF_TABLE_COLUMN:
			postprocessor = new PDFTableColumnPostprocessorFake();
			break;
		case JS_EXTERNALIZE_VARIABLES:
			postprocessor = new JSExternalizeVariablesPostprocessor();
			break;
		case JS_SORT_COMPONENTS:
			postprocessor = new JSSortComponentsPostprocessor();
			break;
		case JS_SPECIFIC_TREATMENT:
			postprocessor = new NoopPostprocessor();
			break;
		case DDI_SPECIFIC_TREATMENT:
			postprocessor = new NoopPostprocessor();
			break;
		case ODT_SPECIFIC_TREATMENT:
			postprocessor = new NoopPostprocessor();
			break;
		case NONE:
			postprocessor = new NoopPostprocessor();
			break;
		}
		return postprocessor;
	}

	@Override
	public Preprocessor getPrePorcessor(PreProcessing preProcessing) {
		Preprocessor preprocessor = null;
		switch (preProcessing) {
		case DDI_DEREFERENCING:
			preprocessor = new DDIDereferencingPreprocessor();
			break;
		case DDI_CLEANING:
			preprocessor = new DDICleaningPreprocessor();
			break;
		case DDI_TITLING:
			preprocessor = new DDITitlingPreprocessor();
			break;
		case DDI_MAPPING:
			preprocessor = new DDIMappingPreprocessor();
			break;
		case POGUES_XML_GOTO_2_ITE:
			preprocessor = new PoguesXMLPreprocessorGoToTreatment();
			break;
		case POGUES_XML_SUPPRESSION_GOTO:
			break;
		case POGUES_XML_TWEAK_TO_MERGE_EQUIVALENT_ITE:
			break;
		}
		return preprocessor;
	}
	

}
