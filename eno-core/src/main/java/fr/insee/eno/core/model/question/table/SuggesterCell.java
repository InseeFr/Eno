package fr.insee.eno.core.model.question.table;

import datacollection33.GridResponseDomainInMixedType;
import fr.insee.eno.core.annotations.Contexts;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.BodyCell;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Contexts.Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Contexts.Context(format = Format.LUNATIC, type = BodyCell.class)
public class SuggesterCell extends ResponseCell {

    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "SUGGESTER";

    @DDI("getResponseDomain().getCodeListReference().getIDArray(0).getStringValue()")
    @Lunatic("setStoreName(#param)")
    public String codeListReference;

}
