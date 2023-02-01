package fr.insee.eno.core.exceptions.technical;

public class IndexingException extends RuntimeException {

    public IndexingException(String message, Exception exception) {
        super(message, exception);
    }

}
