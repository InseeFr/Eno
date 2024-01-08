package fr.insee.eno.ws.controller.exception;

import fr.insee.eno.core.exceptions.business.EnoParametersException;
import fr.insee.eno.ws.exception.ContextException;
import fr.insee.eno.ws.exception.MetadataFileException;
import fr.insee.eno.ws.exception.ModeParameterException;
import fr.insee.eno.legacy.exception.EnoGenerationException;
import fr.insee.eno.legacy.exception.EnoLegacyParametersException;
import fr.insee.eno.treatments.exceptions.SpecificTreatmentsDeserializationException;
import fr.insee.eno.treatments.exceptions.SpecificTreatmentsValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class EnoExceptionController {

	@ExceptionHandler(value = EnoLegacyParametersException.class)
	public ResponseEntity<Object> exception(EnoLegacyParametersException exception) {
		return new ResponseEntity<>("Parameters file is invalid: "+exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = EnoParametersException.class)
	public ResponseEntity<Object> exception(EnoParametersException exception) {
		return new ResponseEntity<>("Parameters file is invalid: "+exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = ModeParameterException.class)
	public ResponseEntity<Object> exception(ModeParameterException exception) {
		return new ResponseEntity<>("Collection mode error: "+exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = ContextException.class)
	public ResponseEntity<Object> exception(ContextException exception) {
		return new ResponseEntity<>("Metadata file error: "+exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = MetadataFileException.class)
	public ResponseEntity<Object> exception(MetadataFileException exception) {
		return new ResponseEntity<>("Metadata file error: "+exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = EnoGenerationException.class)
	public ResponseEntity<Object> exception(EnoGenerationException exception) {
		return new ResponseEntity<>("EnoGeneration error : "+exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = SpecificTreatmentsDeserializationException.class)
	public ResponseEntity<Object> exception(SpecificTreatmentsDeserializationException exception) {
		return new ResponseEntity<>("Erreur durant la vérification du json de traitement spécifique : "+exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = SpecificTreatmentsValidationException.class)
	public ResponseEntity<Object> exception(SpecificTreatmentsValidationException exception) {
		return new ResponseEntity<>("Erreur durant la vérification du json de traitement spécifique :  "+exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Object> exception(Exception exception) {
		return new ResponseEntity<>("Unknown error during generation: "+exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}