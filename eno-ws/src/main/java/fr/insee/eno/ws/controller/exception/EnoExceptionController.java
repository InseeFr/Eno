package fr.insee.eno.ws.controller.exception;

import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.exception.EnoParametersException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EnoExceptionController {

	@ExceptionHandler(value = EnoParametersException.class)
	public ResponseEntity<Object> exception(EnoParametersException exception) {
		return new ResponseEntity<>("Eno parameters error: "+exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = EnoGenerationException.class)
	public ResponseEntity<Object> exception(EnoGenerationException exception) {
		return new ResponseEntity<>("Eno generation error: "+exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Object> exception(Exception exception) {
		return new ResponseEntity<>("Unknown error during generation ("+exception.getClass()+") : "+exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}