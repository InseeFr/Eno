package fr.insee.eno.core.model.question.table;

import datacollection33.GridResponseDomainInMixedType;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** A TableCell object is the content of a table.
 * A cell is neither part of the header nor of the left column. */
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Getter
@Setter
public class BooleanCell extends TableCell {}