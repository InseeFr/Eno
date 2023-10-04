package fr.insee.eno.core.exceptions.business;

import fr.insee.eno.core.model.question.EnoTable;

/**
 * DDI and Lunatic formats allow to have nested code list in the header of a table question.
 * This is yet not permitted by Eno (and is also not implemented).
 * This exception is designed to be thrown if a nested code list is found in a table question.
 */
public class UnauthorizedHeaderException extends RuntimeException {

    public UnauthorizedHeaderException(EnoTable enoTable) {
        super(String.format(
                "Table question with id='%s' and name ='%s' contains a hierarchical code list in its header, " +
                        "which is not allowed.",
                enoTable.getId(), enoTable.getName()));
    }

}
