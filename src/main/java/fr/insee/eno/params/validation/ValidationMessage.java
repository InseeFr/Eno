package fr.insee.eno.params.validation;

public class ValidationMessage {
	
	public ValidationMessage(String message, boolean valid) {
		super();
		this.message = message;
		this.valid = valid;
	}
	private String message;
	private boolean valid;
	
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
