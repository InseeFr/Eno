package fr.insee.eno.params.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.InFormat;
import fr.insee.eno.parameters.Mode;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.Pipeline;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.parameters.PreProcessing;

public class ValidatorImpl implements Validator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorImpl.class);
	
	public static final PostProcessing[] POST_PROCESSINGS_FULL = PostProcessing.class.getEnumConstants();
	public static final PreProcessing[] PRE_PROCESSINGS_FULL = PreProcessing.class.getEnumConstants();
	
	@Override
	public ValidationMessage validate(ENOParameters parametersType) {
		Pipeline pipeline = parametersType.getPipeline();
		ValidationMessage validationIn2Out = validateIn2Out(pipeline.getInFormat(), pipeline.getOutFormat());
		ValidationMessage validationPreProcessings = validatePreProcessings(pipeline);
		ValidationMessage validationPostProcessings = validatePostProcessings(pipeline);
		ValidationMessage validationMode = validateMode(pipeline.getOutFormat(), parametersType.getMode());
		
		boolean isValid = validationIn2Out.isValid()
				&& validationPreProcessings.isValid()
				&& validationPostProcessings.isValid()
				&& validationMode.isValid();
		String message = validationIn2Out.getMessage() +", "+
				validationPreProcessings.getMessage() +", "+
				validationMode.getMessage() +", "+
				validationPostProcessings.getMessage();

		return new ValidationMessage(message,isValid);
	}

	@Override
	public ValidationMessage validateIn2Out(InFormat inFormat, OutFormat outFormat) {
		boolean isValid = inFormat!=null && outFormat!=null;
		String message = "";
		if(isValid) {
			switch (inFormat) {
			case DDI:
				isValid = true;
				break;
			case POGUES_XML:
				isValid = outFormat.equals(OutFormat.DDI);
				break;
			case XFORMS:
				isValid = outFormat.equals(OutFormat.XFORMS);
				break;
			default:
				isValid=false;
				break;
			}
			message += isValid ? "" : "The combination (in:'"+inFormat.value()+"'/out:'"+ outFormat.value() +"')"+" format is not valid"; 
		}
		else {
			message = "One of In/Out format doesn't exist in Eno. ";
		}

		message += isValid ? "The combination In/Out format is valid" : "";
		LOGGER.info(message);
		return new ValidationMessage(message, isValid);
	}

	@Override
	public ValidationMessage validatePreProcessings(Pipeline pipeline) {
		boolean isValid=true;
		String message="";
		InFormat inFormat = pipeline.getInFormat();
		boolean preProcessingNeeded = pipeline.getPostProcessing().contains(PostProcessing.XFORMS_INSEE_MODEL);	
		List<PreProcessing> preProcessings = pipeline.getPreProcessing();
		List<PreProcessing> preProcessingsCopy = new ArrayList<>(preProcessings);

		if(inFormat!=null) {
			if(isValid) {
				Collections.sort(preProcessingsCopy);
				List<PreProcessing> preProcessingInFormat = Arrays.asList(Arrays.stream(PRE_PROCESSINGS_FULL).filter(p->p.value().contains(inFormat.value()+"-")).toArray(PreProcessing[]::new));
				
				boolean order = preProcessingsCopy.equals(preProcessings);
				boolean consistency = preProcessingInFormat.containsAll(preProcessingsCopy);
				boolean mapping = preProcessingNeeded ? preProcessings.contains(PreProcessing.DDI_MAPPING) || inFormat.equals(InFormat.XFORMS):true;
				
				isValid = order && consistency && mapping;
				
				message += order ? "" : "PreProcessings are not in the right order. The right order should be : "+preProcessingsCopy;
				message += consistency ? "" : "PreProcessings are not valid according to the InFormat ('"+inFormat.value()+"') ";
				message += mapping ? "" : "The PostProcessing '"+PostProcessing.XFORMS_INSEE_MODEL.value()+"', need the PreProcessing '"+PreProcessing.DDI_MAPPING.value()+"' ";
								
			}
			else {
				message+="One of PreProcessings doesn't exist in Eno.";
			}
		}
		else {
			isValid = false;
			message+="The inFormat doesn't exist in Eno, validator can't check PreProcessings.";
		}
		message += isValid ? "PreProcessing are valid" : "";
		LOGGER.info(message);
		return new ValidationMessage(message, isValid);
	}

	@Override
	public ValidationMessage validatePostProcessings(Pipeline pipeline) {
		boolean isValid = true;
		String message="";
		OutFormat outFormat = pipeline.getOutFormat();	
		List<PostProcessing> postProcessings = pipeline.getPostProcessing();
		List<PostProcessing> postProcessingsCopy = new ArrayList<>(postProcessings);
		
		if(outFormat!=null) {
			if(isValid) {
				
				Collections.sort(postProcessingsCopy);			
				List<PostProcessing> postProcessingOutFormat = Arrays.asList(Arrays.stream(POST_PROCESSINGS_FULL).filter(p->p.value().contains(outFormat.value()+"-")).toArray(PostProcessing[]::new));
				
				boolean order = postProcessingsCopy.equals(postProcessings);
				boolean consistency = postProcessingOutFormat.containsAll(postProcessingsCopy);
				
				isValid = order && consistency;
				
				message += order ? "" : "PostProcessings are not in the right order. The order should be : "+postProcessingsCopy;
				message += consistency ? "" : "PostProcessings are not valid according to the OutFormat ('"+outFormat.value()+"') ";
			}
			else {
				message+="One of PostProcessings doesn't exist in Eno.";
			}
		}
		else {
			isValid = false;
			message+="The outFormat doesn't exist in Eno, validator can't check PostProcessings.";
		}

		message += isValid ? "PostProcessing are valid" : "";
		LOGGER.info(message);
		return new ValidationMessage(message, isValid);
	}
	
//	@Override
//	public ValidationMessage validateModeLunatic(Mode mode) {
//		boolean isValid = mode!=null;
//		String message = "";
//		if(isValid) {
//			switch (mode) {
//			case PAPI:
//				isValid = false;
//				break;
//			case NONE:
//				isValid = false;
//				break;
//			default:
//				isValid=true;
//				break;
//			}
//			message += isValid ? "" : "The mode:'"+mode.value()+"' is not valid for lunatic"; 
//		}
//		else {
//			message = "The mode is mandatory for lunatic or the mode doesn't exist in Eno. ";
//		}
//
//		message += isValid ? "The mode is valid" : "";
//		LOGGER.info(message);
//		return new ValidationMessage(message, isValid);
//	}
	
	@Override
	public ValidationMessage validateMode(OutFormat outFormat, Mode mode) {
		boolean isValid = mode==null && outFormat !=OutFormat.LUNATIC_XML;
		String message = "";
		if(mode!=null) {
			switch (outFormat) {
			case DDI:
				isValid = mode.equals(Mode.NONE);
				break;
			case FODT:
				isValid = mode.equals(Mode.NONE);
				break;
			case FO:
				isValid = mode.equals(Mode.PAPI);
				break;
			case XFORMS:
				isValid = mode.equals(Mode.CAWI);
				break;
			case LUNATIC_XML:
				isValid = mode.equals(Mode.CAPI_CATI)
				||mode.equals(Mode.CAWI)
				||mode.equals(Mode.PROCESS);
				break;
			default:
				isValid=false;
				break;
			}
			message += isValid ? "" : "The combination (out:'"+outFormat.value()+"'/mode:'"+ mode.value() +"')"+" format is not valid"; 

		}
		else {
			message = "The mode null is not authorized only for Lunatic. ";
		}

		message += isValid ? "The combination (out:'"+outFormat.value()+"'/mode:'"+ (mode!=null? mode.value() : "null") +"')"+" format is valid":"";
		LOGGER.info(message);
		return new ValidationMessage(message, isValid);
	}
}
