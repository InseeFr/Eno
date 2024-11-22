package fr.insee.eno.ws.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class EnoRedirectionException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;

    public EnoRedirectionException(String message) {
        super(message);
        httpStatusCode = null;
    }

    public EnoRedirectionException(String message, HttpStatusCode httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

}
