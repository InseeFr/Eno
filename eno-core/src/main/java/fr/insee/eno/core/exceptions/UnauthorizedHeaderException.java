package fr.insee.eno.core.exceptions;

import fr.insee.eno.core.model.question.TableQuestion;

/**
 * DDI and Lunatic formats allow to have nested code list in the header of a table question.
 * This is yet not permitted by Eno (and is also not implemented).
 * This exception is designed to be thrown if a nested code list is found in a table question.
 */
public class UnauthorizedHeaderException extends RuntimeException {

    public UnauthorizedHeaderException(TableQuestion tableQuestion) {
        super(String.format(
                "Table question with id='%s' and name ='%s' contains a hierarchical code list in its header, " +
                        "which is not allowed.",
                tableQuestion.getId(), tableQuestion.getName()));
    }

}
