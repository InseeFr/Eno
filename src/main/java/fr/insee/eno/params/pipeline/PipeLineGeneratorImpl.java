package fr.insee.eno.params.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import fr.insee.eno.postprocessing.ddi.DDIMarkdown2XhtmlPostprocessor;
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

@Service
public class PipeLineGeneratorImpl implements PipelineGenerator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PipeLineGeneratorImpl.class);
	
	// In2Out Generator
	@Autowired
	DDI2FRGenerator ddi2frGenerator;
	@Autowired
	DDI2JSGenerator ddi2jsGenerator;
	@Autowired
	DDI2ODTGenerator ddi2odtGenerator;
	@Autowired
	DDI2PDFGenerator ddi2pdfGenerator;
	@Autowired
	DDI2PoguesXMLGenerator ddi2poguesXmlGenerator;
	@Autowired
	PoguesXML2DDIGenerator poguesXml2ddiGenerator;
	
	// PreProcessing
	@Autowired
	DDIDereferencingPreprocessor ddiDereferencing;
	@Autowired
	DDICleaningPreprocessor ddiCleaning;
	@Autowired
	DDITitlingPreprocessor ddiTitling;
	@Autowired
	DDIMappingPreprocessor ddiMapping;
	@Autowired
	PoguesXMLPreprocessorGoToTreatment poguesXmlGoTo;
	
	// PostProcessing
	@Autowired
	DDIMarkdown2XhtmlPostprocessor ddiMW2XHTML;
	@Autowired
	FRBrowsingPostprocessor frBrowsing;
	@Autowired
	FREditPatronPostprocessor frEditPatron;
	@Autowired
	FRFixAdherencePostprocessor frFixAdherence;
	@Autowired
	FRIdentificationPostprocessor frIdentification;
	@Autowired
	FRInsertEndPostprocessor frInsertEnd;
	@Autowired
	FRInsertGenericQuestionsPostprocessor frInsertGenericQuestions;
	@Autowired
	FRInsertWelcomePostprocessor frInsertWelcome;
	@Autowired
	FRModeleColtranePostprocessor frModeleColtrane;
	@Autowired
	FRSpecificTreatmentPostprocessor frSpecificTreatment;
	@Autowired
	PDFEditStructurePagesPostprocessor pdfEditStructurePages;
	@Autowired
	PDFInsertAccompanyingMailsPostprocessor pdfInsertAccompanyingMails;
	@Autowired
	PDFInsertCoverPagePostprocessor pdfInsertCoverPage;
	@Autowired
	PDFInsertEndQuestionPostprocessor pdfInsertEndQuestion;
	@Autowired
	PDFMailingPostprocessor pdfMailing;
	@Autowired
	PDFSpecificTreatmentPostprocessor pdfSpecificTreatment;
	@Autowired
	PDFTableColumnPostprocessorFake pdfTableColumn;
	@Autowired
	JSExternalizeVariablesPostprocessor jsExternalizeVariables;
	@Autowired
	JSSortComponentsPostprocessor jsSortComponents;
	@Autowired
	NoopPostprocessor noop;
	

	@Override
	public GenerationService setPipeLine(Pipeline pipeline) throws Exception {
		LOGGER.info("Creating new pipeline...");
		Preprocessor[] preprocessors = setPreProcessors(pipeline.getPreProcessing());
		Generator generator = setGenerator(pipeline.getInFormat(), pipeline.getOutFormat());
		Postprocessor[] postprocessors = setPostProcessors(pipeline.getPostProcessing());
		
		LOGGER.info("PreProccesings : "+Arrays.toString(preprocessors));
		LOGGER.info("Core generation : "+generator.in2out());
		LOGGER.info("PostProccesings : "+Arrays.toString(postprocessors));
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
				generator = ddi2frGenerator;
				break;
			case JS:
				generator = ddi2jsGenerator;
				break;
			case ODT:
				generator = ddi2odtGenerator;
				break;
			case PDF:
				generator = ddi2pdfGenerator;
				break;
			case POGUES_XML:
				generator = ddi2poguesXmlGenerator;
				break;
			}
			break;
		case POGUES_XML:
			switch (outFormat) {
			case DDI:
				generator = poguesXml2ddiGenerator;
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
			postprocessor = ddiMW2XHTML;
			break;
		case FR_BROWSING:
			postprocessor = frBrowsing;
			break;
		case FR_EDIT_PATRON:
			postprocessor = frEditPatron;
			break;
		case FR_FIX_ADHERENCE:
			postprocessor = frFixAdherence;
			break;
		case FR_IDENTIFICATION:
			postprocessor = frIdentification;
			break;
		case FR_INSERT_END:
			postprocessor = frInsertEnd;
			break;
		case FR_INSERT_GENERIC_QUESTIONS:
			postprocessor = frInsertGenericQuestions;
			break;
		case FR_INSERT_WELCOME:
			postprocessor = frInsertWelcome;
			break;
		case FR_MODELE_COLTRANE:
			postprocessor = frModeleColtrane;
			break;
		case FR_SPECIFIC_TREATMENT:
			postprocessor = frSpecificTreatment;
			break;
		case PDF_EDIT_STRUCTURE_PAGES:
			postprocessor = pdfEditStructurePages;
			break;
		case PDF_INSERT_ACCOMPANYING_MAILS:
			postprocessor = pdfInsertAccompanyingMails;
			break;
		case PDF_INSERT_COVER_PAGE:
			postprocessor = pdfInsertCoverPage;
			break;
		case PDF_INSERT_END_QUESTION:
			postprocessor = pdfInsertEndQuestion;
			break;
		case PDF_MAILING:
			postprocessor = pdfMailing;
			break;
		case PDF_SPECIFIC_TREATMENT:
			postprocessor = pdfSpecificTreatment;
			break;
		case PDF_TABLE_COLUMN:
			postprocessor = pdfTableColumn;
			break;
		case JS_EXTERNALIZE_VARIABLES:
			postprocessor = jsExternalizeVariables;
			break;
		case JS_SORT_COMPONENTS:
			postprocessor = jsSortComponents;
			break;
		case JS_SPECIFIC_TREATMENT:
			postprocessor = noop;
			break;
		case DDI_SPECIFIC_TREATMENT:
			postprocessor = noop;
			break;
		case ODT_SPECIFIC_TREATMENT:
			postprocessor = noop;
			break;
		}
		return postprocessor;
	}

	@Override
	public Preprocessor getPrePorcessor(PreProcessing preProcessing) {
		Preprocessor preprocessor = null;
		switch (preProcessing) {
		case DDI_DEREFERENCING:
			preprocessor = ddiDereferencing;
			break;
		case DDI_CLEANING:
			preprocessor = ddiCleaning;
			break;
		case DDI_TITLING:
			preprocessor = ddiTitling;
			break;
		case DDI_MAPPING:
			preprocessor = ddiMapping;
			break;
		case POGUES_XML_GOTO_2_ITE:
			preprocessor = poguesXmlGoTo;
			break;
		case POGUES_XML_SUPPRESSION_GOTO:
			break;
		case POGUES_XML_TWEAK_TO_MERGE_EQUIVALENT_ITE:
			break;
		}
		return preprocessor;
	}
	

}
