package fr.insee.eno.ws.service;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoParametersException;
import fr.insee.eno.parameters.Context;
import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.Mode;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.params.ValorizatorParameters;
import fr.insee.eno.params.ValorizatorParametersImpl;
import fr.insee.eno.params.validation.ValidationMessage;
import fr.insee.eno.params.validation.Validator;
import fr.insee.eno.params.validation.ValidatorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@Slf4j
public class ParameterService {

	private ValorizatorParameters valorizatorParameters = new ValorizatorParametersImpl();
	private Validator validatorImp = new ValidatorImpl();
	
	public ENOParameters getDefaultCustomParameters(Context context, OutFormat outFormat, Mode mode) throws Exception  {
		InputStream mergedParamsInputStream = getDefaultCustomParametersFile(context, outFormat, mode);
		ENOParameters finalParams = valorizatorParameters.getParameters(mergedParamsInputStream);
		return finalParams;
	}
		
	public InputStream getDefaultCustomParametersFile(Context context, OutFormat outFormat, Mode mode) throws Exception {
		context = context != null ? context : Context.DEFAULT;
		String parametersPath = "";

		ValidationMessage validation = validatorImp.validateMode(outFormat, mode);

		if (validation.isValid()) {
			if (mode != null && outFormat == OutFormat.LUNATIC_XML) {
				parametersPath = String.format("/params/%s/%s/%s.xml", outFormat.value().toLowerCase(),
						mode.value().toLowerCase(), context.value().toLowerCase());
			} else {
				parametersPath = String.format("/params/%s/%s.xml", outFormat.value().toLowerCase(),
						context.value().toLowerCase());
			}
			try(InputStream fileParam = Constants.getInputStreamFromPath(parametersPath)){
				ByteArrayOutputStream mergedParams = valorizatorParameters.mergeParameters(fileParam);
				InputStream params =  new ByteArrayInputStream(mergedParams.toByteArray());
				mergedParams.close();
				return params;
			}
		} else	{
			log.error(validation.getMessage());
			throw new EnoParametersException(validation.getMessage());
		}
	}
	public InputStream getDefaultParametersIS()  {
		InputStream xmlParameters = Constants.getInputStreamFromPath(Constants.PARAMETERS_DEFAULT_XML);
		return xmlParameters;
	}

	public String getFileNameFromCampagneName(ENOParameters enoParameters) {
		if (enoParameters == null || enoParameters.getParameters() == null || enoParameters.getParameters().getCampagne() == null ||
				enoParameters.getParameters().getCampagne().isEmpty()) {
				throw new EnoParametersException("The 'campagne' tag is null or empty.");
			}
		return enoParameters.getParameters().getCampagne() + ".zip";
	}

	public String getFileNameFromParameters(ENOParameters enoParameters, boolean multiModel){
		return getFileNameFromParameters(enoParameters.getPipeline().getOutFormat(), multiModel);
	}

	public String getFileNameFromParameters (OutFormat outFormat,boolean multiModel){
		if (multiModel) return "questionnaires.zip";
		return switch (outFormat) {
				case FO -> "questionnaire.fo";
				case FODT -> "questionnaire.fodt";
				case DDI -> "ddi-questionnaire.xml";
				case LUNATIC_XML -> "lunatic-questionnaire.xml";
				case XFORMS -> "questionnaire.xhtml";
			};
		}
	}




