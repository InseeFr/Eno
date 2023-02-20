package fr.insee.eno.ws.controller.exception;

import fr.insee.eno.legacy.exception.EnoGenerationException;
import fr.insee.eno.legacy.exception.EnoParametersException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class EnoExceptionController {

	@ExceptionHandler(value = EnoParametersException.class)
	public ResponseEntity<Object> exception(EnoParametersException exception) {
		return new ResponseEntity<>("EnoParameters error : "+exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = EnoGenerationException.class)
	public ResponseEntity<Object> exception(EnoGenerationException exception) {
		return new ResponseEntity<>("EnoGeneration error : "+exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Object> exception(Exception exception) {
		exception.printStackTrace();
		return new ResponseEntity<>("Unknown error during generation : "+exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}