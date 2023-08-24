package fr.insee.eno.core.model.question.table;

import datacollection33.GridResponseDomainInMixedType;
import datacollection33.SelectDimensionType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.BodyCell;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** A TableCell object is the content of a table.
 * A cell is neither part of the header nor of the left column. */
@Getter
@Setter
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Context(format = Format.LUNATIC, type = BodyCell.class)
public abstract class TableCell extends EnoIdentifiableObject {

    /** Source parameter id from DDI **/
    @DDI("getResponseDomain().getOutParameter().getIDArray(0).getStringValue()")
    String id;

    @DDI("T(fr.insee.eno.core.model.question.table.TableCell).convertDimension(#this, 1)")
    Integer rowNumber;

    @DDI("T(fr.insee.eno.core.model.question.table.TableCell).convertDimension(#this, 2)")
    Integer columnNumber;

    public static Integer convertDimension(GridResponseDomainInMixedType gridType, long dimensionNumber) {
        List<SelectDimensionType> dimensions = gridType.getGridAttachmentArray(0).getCellCoordinatesAsDefinedArray(0).getSelectDimensionList()
                .stream().filter(selectDimensionType -> selectDimensionType.getRank().equals(BigInteger.valueOf(dimensionNumber)))
                .toList();

        if(dimensions.isEmpty()) {
            return null;
        }
        return Integer.parseInt(dimensions.get(0).getRangeMinimum());
    }

}
