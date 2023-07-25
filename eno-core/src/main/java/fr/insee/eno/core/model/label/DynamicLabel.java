package fr.insee.eno.core.model.label;

import datacollection33.DynamicTextType;
import fr.insee.eno.core.Constant;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.LabelType;
import lombok.Getter;
import lombok.Setter;

import static fr.insee.eno.core.annotations.Contexts.Context;

@Getter
@Setter
@Context(format = Format.DDI, type = DynamicTextType.class)
@Context(format = Format.LUNATIC, type = LabelType.class)
public class DynamicLabel extends EnoObject implements EnoLabel {

    @DDI("getTextContentArray(0).getText().getStringValue()")
    @Lunatic("setValue(#param)")
    String value;

    /** For now, Lunatic type in label objects does not come from metadata, but is hardcoded here in Eno.
     * See labels documentation. */
    @Lunatic("setType(#param)")
    String type = Constant.LUNATIC_LABEL_VTL_MD;

}
