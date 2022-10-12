package fr.insee.eno.core.model.label;

import datacollection33.DynamicTextType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.lunatic.model.flat.LabelType;
import lombok.Getter;
import lombok.Setter;

import static fr.insee.eno.core.annotations.Contexts.Context;

@Getter
@Setter
@Context(format = Format.DDI, type = DynamicTextType.class)
@Context(format = Format.LUNATIC, type = DynamicTextType.class)
public class DynamicLabel extends EnoObject {

    @DDI(contextType = DynamicTextType.class, field = "getTextContentArray(0).getText().getStringValue()")
    @Lunatic(contextType = LabelType.class, field = "setValue(#param)")
    String value;

    @Lunatic(contextType = LabelType.class, field = "setType('TODO')") //TODO: mapping or processing for this
    String type;

}
