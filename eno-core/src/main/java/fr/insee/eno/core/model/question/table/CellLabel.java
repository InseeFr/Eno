package fr.insee.eno.core.model.question.table;

import fr.insee.ddi.lifecycle33.datacollection.CellLabelType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.label.EnoLabel;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.LabelType;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.DDI, type = CellLabelType.class)
@Context(format = Format.LUNATIC, type = LabelType.class)
public class CellLabel extends EnoObject implements EnoLabel {

    /** Label associated with a DDI NoDataByDefinition (non-collected cell).
     * Note: other value exist:
     * <ul>
     *     <li>"filteredcell": Filter condition of a cell.</li>
     *     <li>"readonlyresponsecell": Readonly condition of a cell.</li>
     * </ul>
     * But these have no use in DDI to Lunatic mapping.
     */
    public static final String DDI_FIXED_CELL = "fixedcell";

    /** Label to be displayed. */
    @DDI("getContentArray(0).getStringValue()")
    @Lunatic("setValue(#param)")
    String value;

    @DDI("getTypeOfLabel().getStringValue()")
    String ddiCellType;

    /** Property that is specific to Lunatic.
     * For now, Lunatic type in label objects does not come from metadata, but is hardcoded here in Eno.
     * See labels documentation. */
    @Lunatic("setType(T(fr.insee.lunatic.model.flat.LabelTypeEnum).fromValue(#param))")
    String type = LabelTypeEnum.VTL_MD.value();

    /** In DDI, the cell label is not placed within the cell description.
     * A DDI processing inserts cell labels within the corresponding cell using row/column coordinates. */
    @DDI("T(fr.insee.eno.core.model.question.table.TableCell).convertDDIDimension(#this.getGridAttachmentArray(0), 1)")
    Integer rowNumber;

    /** In DDI, the cell label is not placed within the cell description.
     * A DDI processing inserts cell labels within the corresponding cell using row/column coordinates. */
    @DDI("T(fr.insee.eno.core.model.question.table.TableCell).convertDDIDimension(#this.getGridAttachmentArray(0), 2)")
    Integer columnNumber;

}
