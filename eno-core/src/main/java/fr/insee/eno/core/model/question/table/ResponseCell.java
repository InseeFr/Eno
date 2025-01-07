package fr.insee.eno.core.model.question.table;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoObjectWithId;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.parameter.Format;
import fr.insee.pogues.model.ResponseType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.POGUES, type = ResponseType.class)
public abstract class ResponseCell extends EnoObject implements TableCell, EnoObjectWithId {

    /** Source parameter id from DDI **/
    @Pogues("getId()")
    @DDI("getResponseDomain().getOutParameter().getIDArray(0).getStringValue()")
    @Lunatic("setId(#param)")
    String id;

    /** Row position in the table. Starts at 1. */
    @DDI("T(fr.insee.eno.core.model.question.table.TableCell).convertDDIDimension(#this.getGridAttachmentArray(0), 1)")
    Integer rowNumber;

    /** Column position in the table. Starts at 1.
     * In DDI, the 'GridResponseDomainInMixed' element may not have a rank '2' dimension
     * (e.g. in a question grid that correspond to a complex multiple choice question). */
    @DDI("T(fr.insee.eno.core.model.question.table.TableCell).convertDDIDimension(#this.getGridAttachmentArray(0), 2)")
    Integer columnNumber;

    /** Response object for Lunatic.
     * In DDI, response names are mapped in the table question object and inserted here through a processing. */
    @Pogues("#this")
    @Lunatic("setResponse(#param)")
    Response response;

}
