package fr.insee.eno.core.model.question.table;

import fr.insee.ddi.lifecycle33.datacollection.GridResponseDomainInMixedType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.pogues.model.ResponseType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Context(format = Format.POGUES, type = ResponseType.class)
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Context(format = Format.LUNATIC, type = BodyCell.class)
public class TextCell extends ResponseCell {

    @DDI("getResponseDomain().getMaxLength().intValue()")
    @Lunatic("setMaxLength(#param)")
    BigInteger maxLength;

    @DDI("T(fr.insee.eno.core.model.question.TextQuestion).qualifyLength(" +
            "#this.getResponseDomain().getMaxLength().intValue())")
    @Lunatic("setComponentType(" +
            "T(fr.insee.eno.core.model.question.table.TextCell).lengthTypeToLunatic(#param))")
    TextQuestion.LengthType lengthType;

    public static ComponentTypeEnum lengthTypeToLunatic(TextQuestion.LengthType lengthType) {
        return switch (lengthType) {
            case SHORT -> ComponentTypeEnum.INPUT;
            case LONG -> ComponentTypeEnum.TEXTAREA;
        };
    }

}
