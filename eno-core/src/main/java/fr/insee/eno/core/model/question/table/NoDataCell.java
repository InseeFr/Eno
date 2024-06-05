package fr.insee.eno.core.model.question.table;

import datacollection33.GridAttachmentType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

/**
 * Cell of a table that is not designed to collect data.
 * Contains a label that is displayed for the respondent.
 */
@Getter
@Setter
@Context(format = Format.DDI, type = GridAttachmentType.class)
public class NoDataCell extends EnoObject {

    // TODO: creating a ResponseCell that extends TableCell and make this class also extend it would avoid code duplication.

    /** Row position in the table. Starts at 1. */
    @DDI("T(fr.insee.eno.core.model.question.table.TableCell).convertDDIDimension(#this, 1)")
    Integer rowNumber;

    /** Column position in the table. Starts at 1.
     * In DDI, the 'GridResponseDomainInMixed' element may not have a rank '2' dimension
     * (e.g. in a question grid that correspond to a complex multiple choice question). */
    @DDI("T(fr.insee.eno.core.model.question.table.TableCell).convertDDIDimension(#this, 2)")
    Integer columnNumber;

    /** Label to be displayed in the cell.
     * In DDI, this label is described higher and is inserted here through a processing. */
    @Lunatic("setLabel(#param)")
    CellLabel cellLabel;

}
