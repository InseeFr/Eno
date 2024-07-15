package fr.insee.eno.core.model.question.table;

import fr.insee.ddi.lifecycle33.datacollection.GridAttachmentType;
import fr.insee.ddi.lifecycle33.datacollection.GridResponseDomainInMixedType;
import fr.insee.ddi.lifecycle33.datacollection.SelectDimensionType;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.BodyCell;

import java.math.BigInteger;
import java.util.Optional;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** A TableCell object is the content of a table.
 * A cell is neither part of the header nor of the left column.
 * @see fr.insee.eno.core.model.question.TableQuestion */
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Context(format = Format.LUNATIC, type = BodyCell.class)
public interface TableCell {

    Integer getRowNumber();
    void setRowNumber(Integer rowNumber);
    Integer getColumnNumber();
    void setColumnNumber(Integer columnNumber);

    /** From the GridAttachmentType object given, return the range value of the dimension that has the
     * given 'rank'. Return null if there is no dimension that has the given rank.
     * Note: DDI has range 'minimum' and 'maximum' properties. This method returns the 'minimum' one,
     * both are equal in current Insee modeling. */
    static Integer convertDDIDimension(GridAttachmentType gridAttachmentType, long dimensionRank) {
        Optional<SelectDimensionType> dimensions = gridAttachmentType
                .getCellCoordinatesAsDefinedArray(0).getSelectDimensionList()
                .stream()
                .filter(selectDimensionType -> selectDimensionType.getRank().equals(BigInteger.valueOf(dimensionRank)))
                .findAny();
        if (dimensions.isEmpty())
            return null;
        return Integer.parseInt(dimensions.get().getRangeMinimum());
    }

}
