package fr.insee.eno.params.validation;

import java.util.Arrays;
import java.util.List;

import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.InFormat;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.Pipeline;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.parameters.PreProcessing;
public class ValidatorImpl implements Validator {
	
	public static final List<PreProcessing> PRE_PROCESSING_DDI = Arrays.asList(
			PreProcessing.DDI_DEREFERENCING,
			PreProcessing.DDI_CLEANING,
			PreProcessing.DDI_TITLING);
	
	public static final List<PreProcessing> PRE_PROCESSING_POGUES_XML_WITH_GOTO = Arrays.asList(
			PreProcessing.POGUES_XML_GOTO_2_ITE);
	
	public static final List<PreProcessing> PRE_PROCESSING_POGUES_XML_WITHOUT_GOTO = Arrays.asList(
			PreProcessing.POGUES_XML_SUPPRESSION_GOTO,
			PreProcessing.POGUES_XML_TWEAK_TO_MERGE_EQUIVALENT_ITE);
	
	public static final List<PostProcessing> POST_PROCESSING_DDI = Arrays.asList(
			PostProcessing.DDI_MARKDOWN_TO_XHTML);
	
	public static final List<PostProcessing> POST_PROCESSING_JS = Arrays.asList(
			PostProcessing.JS_SORT_COMPONENTS,
			PostProcessing.JS_EXTERNALIZE_VARIABLES);
	
	public static final List<PostProcessing> POST_PROCESSING_PDF = Arrays.asList(
			PostProcessing.PDF_MAILING,
			PostProcessing.PDF_TABLE_COLUMN,
			PostProcessing.PDF_INSERT_END_QUESTION,
			PostProcessing.PDF_EDIT_STRUCTURE_PAGES,
			PostProcessing.PDF_INSERT_COVER_PAGE,
			PostProcessing.PDF_INSERT_ACCOMPANYING_MAILS);
	
	public static final List<PostProcessing> POST_PROCESSING_FR = Arrays.asList(
			PostProcessing.FR_INSERT_GENERIC_QUESTIONS,
			PostProcessing.FR_BROWSING,
			PostProcessing.FR_MODELE_COLTRANE,
			PostProcessing.FR_EDIT_PATRON,
			PostProcessing.FR_IDENTIFICATION,
			PostProcessing.FR_INSERT_WELCOME,
			PostProcessing.FR_INSERT_END,
			PostProcessing.FR_FIX_ADHERENCE);

	@Override
	public boolean validate(byte[] parameters) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validate(ENOParameters parametersType) {
		Pipeline pipeline = parametersType.getPipeline();
		boolean isValid = 
				validateIn2Out(pipeline.getInFormat(), pipeline.getOutFormat())
				&& validatePostProcessings(pipeline);
		return false;
	}
	
	public boolean validateIn2Out(InFormat inFormat, OutFormat outFormat) {
		boolean isValid;
		
		switch (inFormat) {
		case DDI:
			isValid = !outFormat.equals(OutFormat.DDI);
			break;
		case POGUES_XML:
			isValid = !outFormat.equals(OutFormat.POGUES_XML);
			break;
		default:
			isValid=false;
			break;
		}		
		return isValid;
	}
	
	public boolean validatePreProcessings(InFormat inFormat, List<PreProcessing> preProcessings) {
		boolean isValid;
		switch (inFormat) {
		case DDI:
			isValid = 
			preProcessings.containsAll(PRE_PROCESSING_DDI)
			&& preProcessings.size()==PRE_PROCESSING_DDI.size();
			break;
		case POGUES_XML:
			isValid = 
			(preProcessings.containsAll(PRE_PROCESSING_POGUES_XML_WITH_GOTO) && preProcessings.size()==PRE_PROCESSING_POGUES_XML_WITH_GOTO.size())
			||
			(preProcessings.containsAll(PRE_PROCESSING_POGUES_XML_WITHOUT_GOTO) && preProcessings.size()==PRE_PROCESSING_POGUES_XML_WITHOUT_GOTO.size());
			break;
		default:
			isValid=false;
			break;
		}
		return isValid;
	}
	
	public boolean validatePostProcessings(Pipeline pipeline) {
		boolean isValid;
		
		OutFormat outFormat = pipeline.getOutFormat();
		List<PostProcessing> postProcessings = pipeline.getPostProcessing();
		boolean specificTreatment = false;
		switch (outFormat) {
		case DDI:
			isValid = postProcessings.containsAll(POST_PROCESSING_DDI) && postProcessings.size()==POST_PROCESSING_DDI.size();
			break;
		case FR:
			isValid = postProcessings.containsAll(POST_PROCESSING_FR) && postProcessings.size()==POST_PROCESSING_FR.size();
			break;
		case PDF:
			isValid = postProcessings.containsAll(POST_PROCESSING_PDF) && postProcessings.size()==POST_PROCESSING_PDF.size();			
			break;
		case JS:
			isValid = postProcessings.containsAll(POST_PROCESSING_JS) && postProcessings.size()==POST_PROCESSING_JS.size();
			break;
		case ODT:
			isValid = postProcessings.isEmpty();
			break;
		case POGUES_XML:
			isValid = postProcessings.isEmpty();
			break;
		}
		return false;
	}
	
	public boolean validateParams() {
		return false;
	}

}
