package fr.insee.eno.ws.service;

import fr.insee.eno.parameters.Context;
import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.Mode;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.service.MultiModelService;
import fr.insee.eno.service.ParameterizedGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class QuestionnaireGenerateService {

	// Eno core service
	private final ParameterizedGenerationService generationService = new ParameterizedGenerationService();
	private final MultiModelService multiModelService = new MultiModelService();

	// Eno-WS service
	private final ParameterService parameterService;

	public QuestionnaireGenerateService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	public ByteArrayOutputStream generateQuestionnaireFile(Context context, OutFormat outFormat, Mode mode,
														   MultipartFile in,
														   MultipartFile metadata,
														   MultipartFile specificTreatment) throws Exception {

		ENOParameters enoParameters = parameterService.getDefaultCustomParameters(context, outFormat, mode);
		ByteArrayOutputStream enoOutput;

		try(
				InputStream enoInput = in.getInputStream();
				InputStream metadataIS = metadata!=null ? metadata.getInputStream():null;
				InputStream specificTreatmentIS = specificTreatment!=null ? specificTreatment.getInputStream():null
		){

			enoOutput= generationService.generateQuestionnaire(
					enoInput, enoParameters, metadataIS, specificTreatmentIS, null);

			log.info("END of Eno 'in to out' processing");
		}
		return enoOutput;
	}

	public ByteArrayOutputStream generateMultiModelQuestionnaires(Context context, OutFormat outFormat, Mode mode,
																 MultipartFile in,
																 MultipartFile metadata,
																 MultipartFile specificTreatment) throws Exception {

		ENOParameters enoParameters = parameterService.getDefaultCustomParameters(context, outFormat, mode);
		ByteArrayOutputStream enoOutput;

		try(
				InputStream enoInput = in.getInputStream();
				InputStream metadataIS = metadata!=null ? metadata.getInputStream():null;
				InputStream specificTreatmentIS = specificTreatment!=null ? specificTreatment.getInputStream():null
		){

			enoOutput= multiModelService.generateQuestionnaire(
					enoInput, enoParameters, metadataIS, specificTreatmentIS, null);

			log.info("END of Eno multi-model questionnaires processing");
		}
		return enoOutput;
	}

}
