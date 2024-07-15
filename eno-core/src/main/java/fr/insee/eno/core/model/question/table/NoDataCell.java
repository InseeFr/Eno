package fr.insee.eno.core.model.question.table;

import fr.insee.ddi.lifecycle33.datacollection.GridAttachmentType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.BodyCell;
import lombok.Getter;
import lombok.Setter;

/**
 * Cell of a table that is not designed to collect data.
 * Contains a label that is displayed for the respondent.
 */
@Getter
@Setter
@Context(format = Format.DDI, type = GridAttachmentType.class)
@Context(format = Format.LUNATIC, type = BodyCell.class)
public class NoDataCell extends EnoObject implements TableCell {

    /** Identifier of the no data cell. **/
    @Lunatic("setId(#param)")
    String id;

    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "TEXT";

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
