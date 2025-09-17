package fr.insee.eno.params.pipeline;

import fr.insee.eno.generation.*;
import fr.insee.eno.parameters.*;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.fo.*;
import fr.insee.eno.postprocessing.xforms.*;
import fr.insee.eno.preprocessing.*;
import fr.insee.eno.service.GenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipeLineGeneratorImpl implements PipelineGenerator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PipeLineGeneratorImpl.class);
	
	// In2Out Generator
	private IdentityGenerator identityGenerator = new IdentityGenerator();
	
	private DDI2XFORMSGenerator ddi2xformsGenerator = new DDI2XFORMSGenerator();

	private DDI2FODTGenerator ddi2fodtGenerator = new DDI2FODTGenerator();

	private DDI2FOGenerator ddi2foGenerator = new DDI2FOGenerator();

	private PoguesXML2DDIGenerator poguesXml2ddiGenerator = new PoguesXML2DDIGenerator();
	
	// PreProcessing
	
	private NoopPreprocessor noopPreprocessor = new NoopPreprocessor();
	
	private DDIDereferencingPreprocessor ddiDereferencing = new DDIDereferencingPreprocessor();

	private DDICleaningPreprocessor ddiCleaning = new DDICleaningPreprocessor();

	private DDITitlingPreprocessor ddiTitling = new DDITitlingPreprocessor();

	private DDIMappingPreprocessor ddiMapping = new DDIMappingPreprocessor();
	
	private DDIMultimodalSelectionPreprocessor ddiMultimodal = new DDIMultimodalSelectionPreprocessor();
	
	private DDIMarkdown2XhtmlPreprocessor ddiMW2XHTML = new DDIMarkdown2XhtmlPreprocessor();

	private PoguesXMLPreprocessorGoToTreatment poguesXmlGoTo = new PoguesXMLPreprocessorGoToTreatment();
	
	private PoguesXmlInsertFilterLoopIntoQuestionTree poguesXmlFilterLoopIntoQuestionTree = new PoguesXmlInsertFilterLoopIntoQuestionTree();
	
	private DDI32ToDDI33Preprocessor ddi32ToDDI33Preprocessor = new DDI32ToDDI33Preprocessor();
	
	// PostProcessing
	private XFORMSBrowsingPostprocessor xformsBrowsing = new XFORMSBrowsingPostprocessor();

	private XFORMSInseePatternPostprocessor xformsInseePattern = new XFORMSInseePatternPostprocessor();

	private XFORMSFixAdherencePostprocessor xformsFixAdherence = new XFORMSFixAdherencePostprocessor();

	private XFORMSIdentificationPostprocessor xformsIdentification = new XFORMSIdentificationPostprocessor();

	private XFORMSInsertEndPostprocessor xformsInsertEnd = new XFORMSInsertEndPostprocessor();

	private XFORMSInsertGenericQuestionsPostprocessor xformsInsertGenericQuestions = new XFORMSInsertGenericQuestionsPostprocessor();

	private XFORMSInsertWelcomePostprocessor xformsInsertWelcome = new XFORMSInsertWelcomePostprocessor();

	private XFORMSInseeModelPostprocessor xformsInseeModel = new XFORMSInseeModelPostprocessor();

	private XFORMSSpecificTreatmentPostprocessor xformsSpecificTreatment = new XFORMSSpecificTreatmentPostprocessor();

	private FOEditStructurePagesPostprocessor foEditStructurePages = new FOEditStructurePagesPostprocessor();

	private FOInsertAccompanyingMailsPostprocessor foInsertAccompanyingMails = new FOInsertAccompanyingMailsPostprocessor();

	private FOInsertCoverPagePostprocessor foInsertCoverPage = new FOInsertCoverPagePostprocessor();

	private FOInsertEndQuestionPostprocessor foInsertEndQuestion = new FOInsertEndQuestionPostprocessor();
	
	private FOMailingPostprocessor foMailing = new FOMailingPostprocessor();
	
	private FOSpecificTreatmentPostprocessor foSpecificTreatment = new FOSpecificTreatmentPostprocessor();
	
	private FOTableColumnPostprocessorFake foTableColumn = new FOTableColumnPostprocessorFake();

	private NoopPostprocessor noop = new NoopPostprocessor();
	

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
		if(preProcessings.isEmpty()) {preprocessors.add(noopPreprocessor);}
		else {
		for(PreProcessing preProcessing : preProcessings) {
			preprocessors.add(getPreProcessor(preProcessing));
		} }
		return preprocessors.toArray(new Preprocessor[preprocessors.size()]);
	}
	
	@Override
	public Generator setGenerator(InFormat inFormat, OutFormat outFormat) {
		Generator generator=null;
		
		switch (inFormat) {
		case DDI:
			switch (outFormat) {
			case DDI:
				generator = identityGenerator;
				break;
			case XFORMS:
				generator = ddi2xformsGenerator;
				break;
			case FODT:
				generator = ddi2fodtGenerator;
				break;
			case FO:
				generator = ddi2foGenerator;
				break;
//			case POGUES_XML:
//				generator = ddi2poguesXmlGenerator;
//				break;
			}
			break;
		case POGUES_XML:
			switch (outFormat) {
			case DDI:
				generator = poguesXml2ddiGenerator;
				break;
			default:
				generator = identityGenerator;
				break;
			}
			break;
		case XFORMS:
			generator = identityGenerator;
			break;
		}
		return generator;
	}

	@Override
	public Postprocessor getPostPorcessor(PostProcessing postProcessing) {
		Postprocessor postprocessor = null;
		switch (postProcessing) {
		case XFORMS_BROWSING:
			postprocessor = xformsBrowsing;
			break;
		case XFORMS_INSEE_PATTERN:
			postprocessor = xformsInseePattern;
			break;
		case XFORMS_FIX_ADHERENCE:
			postprocessor = xformsFixAdherence;
			break;
		case XFORMS_IDENTIFICATION:
			postprocessor = xformsIdentification;
			break;
		case XFORMS_INSERT_END:
			postprocessor = xformsInsertEnd;
			break;
		case XFORMS_INSERT_GENERIC_QUESTIONS:
			postprocessor = xformsInsertGenericQuestions;
			break;
		case XFORMS_INSERT_WELCOME:
			postprocessor = xformsInsertWelcome;
			break;
		case XFORMS_INSEE_MODEL:
			postprocessor = xformsInseeModel;
			break;
		case XFORMS_SPECIFIC_TREATMENT:
			postprocessor = xformsSpecificTreatment;
			break;
		case FO_EDIT_STRUCTURE_PAGES:
			postprocessor = foEditStructurePages;
			break;
		case FO_INSERT_ACCOMPANYING_MAILS:
			postprocessor = foInsertAccompanyingMails;
			break;
		case FO_INSERT_COVER_PAGE:
			postprocessor = foInsertCoverPage;
			break;
		case FO_INSERT_END_QUESTION:
			postprocessor = foInsertEndQuestion;
			break;
		case FO_MAILING:
			postprocessor = foMailing;
			break;
		case FO_SPECIFIC_TREATMENT:
			postprocessor = foSpecificTreatment;
			break;
		case FO_TABLE_COLUMN:
			postprocessor = foTableColumn;
			break;
		case DDI_SPECIFIC_TREATMENT:
			postprocessor = noop;
			break;
		case FODT_SPECIFIC_TREATMENT:
			postprocessor = noop;
			break;
		}
		return postprocessor;
	}

	@Override
	public Preprocessor getPreProcessor(PreProcessing preProcessing) {
		Preprocessor preprocessor = null;
		
		if(preProcessing==null) {
			preprocessor = noopPreprocessor;
			}
		else {
		switch (preProcessing) {
		case DDI_32_TO_DDI_33:
			preprocessor = ddi32ToDDI33Preprocessor;
			break;
		case DDI_MARKDOWN_TO_XHTML:
			preprocessor = ddiMW2XHTML;
			break;
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
		case DDI_MULTIMODAL_SELECTION:
			preprocessor = ddiMultimodal;
			break;
		case POGUES_XML_INSERT_FILTER_LOOP_INTO_QUESTION_TREE:
			preprocessor = poguesXmlFilterLoopIntoQuestionTree;
			break;
		case POGUES_XML_GOTO_2_ITE:
			preprocessor = poguesXmlGoTo;
			break;
			case POGUES_XML_SUPPRESSION_GOTO:
			break;
		case POGUES_XML_TWEAK_TO_MERGE_EQUIVALENT_ITE:
			break;
		}}
		return preprocessor;
	}
	

}
