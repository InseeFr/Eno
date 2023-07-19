package fr.insee.eno.core.model.question.table;

import datacollection33.GridResponseDomainInMixedType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.question.TableCell;
import fr.insee.lunatic.model.flat.BodyCell;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class TextCell extends TableCell {
    @DDI(contextType = GridResponseDomainInMixedType.class, field = "getResponseDomain().getMaxLength().intValue()")
    @Lunatic(contextType = BodyCell.class, field = "setMaxLength(#param)")
    BigInteger maxLength;
}
