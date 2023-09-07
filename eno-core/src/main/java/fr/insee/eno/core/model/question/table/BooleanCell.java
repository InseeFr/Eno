package fr.insee.eno.core.model.question.table;

import datacollection33.GridResponseDomainInMixedType;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.BodyCell;
import lombok.Getter;
import lombok.Setter;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** A TableCell object is the content of a table.
 * A cell is neither part of the header nor of the left column. */
@Getter
@Setter
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Context(format = Format.LUNATIC, type = BodyCell.class)
public class BooleanCell extends TableCell {

    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "CHECKBOX_BOOLEAN";

}