package fr.insee.eno.core.model.question;

import datacollection33.GridResponseDomainInMixedType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** A TableCell object is the content of a table.
 * A cell is neither part of the header nor of the left column. */
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Getter
@Setter
public abstract class TableCell extends EnoObject {

    @DDI(contextType = GridResponseDomainInMixedType.class,
            field = "getResponseDomain().getOutParameter().getIDArray(0).getStringValue()")
    String id;

    @DDI(contextType = GridResponseDomainInMixedType.class,
            field = "T(java.lang.Integer).parseInt(getGridAttachmentArray(0).getCellCoordinatesAsDefinedArray(0).getSelectDimensionList()" +
                    ".?[#this.getRank().intValue() == 1].get(0).getRangeMinimum())") // range maximum is the same in Insee DDI
    int rowNumber;

    @DDI(contextType = GridResponseDomainInMixedType.class,
            field = "T(java.lang.Integer).parseInt(getGridAttachmentArray(0).getCellCoordinatesAsDefinedArray(0).getSelectDimensionList()" +
                    ".?[#this.getRank().intValue() == 2].get(0).getRangeMinimum())") // range maximum is the same in Insee DDI
    int columnNumber;
}
