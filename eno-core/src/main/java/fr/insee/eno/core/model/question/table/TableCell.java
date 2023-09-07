package fr.insee.eno.core.model.question.table;

import datacollection33.GridResponseDomainInMixedType;
import datacollection33.SelectDimensionType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.BodyCell;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** A TableCell object is the content of a table.
 * A cell is neither part of the header nor of the left column.
 * @see fr.insee.eno.core.model.question.TableQuestion */
@Getter
@Setter
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Context(format = Format.LUNATIC, type = BodyCell.class)
public abstract class TableCell extends EnoIdentifiableObject {

    /** Source parameter id from DDI **/
    @DDI("getResponseDomain().getOutParameter().getIDArray(0).getStringValue()")
    @Lunatic("setId(#param)")
    String id;

    /** Row position in the table. Starts at 1. */
    @DDI("T(fr.insee.eno.core.model.question.table.TableCell).convertDDIDimension(#this, 1)")
    Integer rowNumber;

    /** Column position in the table. Starts at 1. */
    @DDI("T(fr.insee.eno.core.model.question.table.TableCell).convertDDIDimension(#this, 2)")
    Integer columnNumber;

    /** Response object for Lunatic.
     * In DDI, response names are mapped in the table question object and inserted here through a processing. */
    @Lunatic("setResponse(#param)")
    Response response;

    public static Integer convertDDIDimension(GridResponseDomainInMixedType gridType, long dimensionNumber) {
        List<SelectDimensionType> dimensions = gridType
                .getGridAttachmentArray(0).getCellCoordinatesAsDefinedArray(0).getSelectDimensionList()
                .stream()
                .filter(selectDimensionType -> selectDimensionType.getRank().equals(BigInteger.valueOf(dimensionNumber)))
                .toList();
        if (dimensions.isEmpty())
            return null;
        return Integer.parseInt(dimensions.get(0).getRangeMinimum());
    }

}
