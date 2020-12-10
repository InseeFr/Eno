package fr.insee.eno.params.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.generation.DDI2FODTGenerator;
import fr.insee.eno.generation.DDI2FOGenerator;
import fr.insee.eno.generation.DDI2LunaticXMLGenerator;
import fr.insee.eno.generation.DDI2XFORMSGenerator;
import fr.insee.eno.generation.Generator;
import fr.insee.eno.generation.IdentityGenerator;
import fr.insee.eno.generation.PoguesXML2DDIGenerator;
import fr.insee.eno.parameters.InFormat;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.Pipeline;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.ddi.DDIMarkdown2XhtmlPostprocessor;
import fr.insee.eno.postprocessing.fo.FOEditStructurePagesPostprocessor;
import fr.insee.eno.postprocessing.fo.FOInsertAccompanyingMailsPostprocessor;
import fr.insee.eno.postprocessing.fo.FOInsertCoverPagePostprocessor;
import fr.insee.eno.postprocessing.fo.FOInsertEndQuestionPostprocessor;
import fr.insee.eno.postprocessing.fo.FOMailingPostprocessor;
import fr.insee.eno.postprocessing.fo.FOSpecificTreatmentPostprocessor;
import fr.insee.eno.postprocessing.fo.FOTableColumnPostprocessorFake;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLExternalizeVariablesPostprocessor;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLInsertGenericQuestionsPostprocessor;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLSortComponentsPostprocessor;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLVTLParserPostprocessor;
import fr.insee.eno.postprocessing.xforms.XFORMSBrowsingPostprocessor;
import fr.insee.eno.postprocessing.xforms.XFORMSFixAdherencePostprocessor;
import fr.insee.eno.postprocessing.xforms.XFORMSIdentificationPostprocessor;
import fr.insee.eno.postprocessing.xforms.XFORMSInseeModelPostprocessor;
import fr.insee.eno.postprocessing.xforms.XFORMSInseePatternPostprocessor;
import fr.insee.eno.postprocessing.xforms.XFORMSInsertEndPostprocessor;
import fr.insee.eno.postprocessing.xforms.XFORMSInsertGenericQuestionsPostprocessor;
import fr.insee.eno.postprocessing.xforms.XFORMSInsertWelcomePostprocessor;
import fr.insee.eno.postprocessing.xforms.XFORMSSpecificTreatmentPostprocessor;
import fr.insee.eno.preprocessing.DDI32ToDDI33Preprocessor;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDIMappingPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.PoguesXmlInsertFilterLoopIntoQuestionTree;
import fr.insee.eno.preprocessing.PoguesXMLPreprocessorGoToTreatment;
import fr.insee.eno.preprocessing.Preprocessor;
import fr.insee.eno.service.GenerationService;

public class PipeLineGeneratorImpl implements PipelineGenerator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PipeLineGeneratorImpl.class);
	
	// In2Out Generator
	private IdentityGenerator identityGenerator = new IdentityGenerator();
	
	private DDI2XFORMSGenerator ddi2xformsGenerator = new DDI2XFORMSGenerator();

	private DDI2LunaticXMLGenerator ddi2lunaticXmlGenerator = new DDI2LunaticXMLGenerator();

	private DDI2FODTGenerator ddi2fodtGenerator = new DDI2FODTGenerator();

	private DDI2FOGenerator ddi2foGenerator = new DDI2FOGenerator();

	private PoguesXML2DDIGenerator poguesXml2ddiGenerator = new PoguesXML2DDIGenerator();
	
	// PreProcessing
	private DDIDereferencingPreprocessor ddiDereferencing = new DDIDereferencingPreprocessor();

	private DDICleaningPreprocessor ddiCleaning = new DDICleaningPreprocessor();

	private DDITitlingPreprocessor ddiTitling = new DDITitlingPreprocessor();

	private DDIMappingPreprocessor ddiMapping = new DDIMappingPreprocessor();

	private PoguesXMLPreprocessorGoToTreatment poguesXmlGoTo = new PoguesXMLPreprocessorGoToTreatment();
	
	private PoguesXmlInsertFilterLoopIntoQuestionTree poguesXmlFilterLoopIntoQuestionTree = new PoguesXmlInsertFilterLoopIntoQuestionTree();
	
	private DDI32ToDDI33Preprocessor ddi32ToDDI33Preprocessor = new DDI32ToDDI33Preprocessor();
	
	// PostProcessing
	private DDIMarkdown2XhtmlPostprocessor ddiMW2XHTML = new DDIMarkdown2XhtmlPostprocessor();

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
	
	private LunaticXMLInsertGenericQuestionsPostprocessor lunaticXmlInsertGenericQuestions = new LunaticXMLInsertGenericQuestionsPostprocessor();
	
	private LunaticXMLExternalizeVariablesPostprocessor lunaticXmlExternalizeVariables = new LunaticXMLExternalizeVariablesPostprocessor();
	
	private LunaticXMLSortComponentsPostprocessor lunaticXmlSortComponents = new LunaticXMLSortComponentsPostprocessor();
	
	private LunaticXMLVTLParserPostprocessor lunaticXmlvtlParser = new LunaticXMLVTLParserPostprocessor();
	
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
		for(PreProcessing preProcessing : preProcessings) {
			preprocessors.add(getPreProcessor(preProcessing));
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
				generator = identityGenerator;
				break;
			case XFORMS:
				generator = ddi2xformsGenerator;
				break;
			case LUNATIC_XML:
				generator = ddi2lunaticXmlGenerator;
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
		case DDI_MARKDOWN_TO_XHTML:
			postprocessor = ddiMW2XHTML;
			break;
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
		case LUNATIC_XML_EXTERNALIZE_VARIABLES:
			postprocessor = lunaticXmlExternalizeVariables;
			break;
		case LUNATIC_XML_INSERT_GENERIC_QUESTIONS:
			postprocessor= lunaticXmlInsertGenericQuestions;
			break;
		case LUNATIC_XML_SORT_COMPONENTS:
			postprocessor = lunaticXmlSortComponents;
			break;
		case LUNATIC_XML_VTL_PARSER:
			postprocessor = lunaticXmlvtlParser;
			break;
		case LUNATIC_XML_SPECIFIC_TREATMENT:
			postprocessor = noop;
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
		switch (preProcessing) {
		case DDI_32_TO_DDI_33:
			preprocessor = ddi32ToDDI33Preprocessor;
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
		}
		return preprocessor;
	}
	

}
