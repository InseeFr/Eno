package fr.insee.eno.core.model.label;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import reusable33.LabelType;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** Label object that is used in components */
@Context(format = Format.DDI, type = LabelType.class)
public class Label extends EnoObject {

    @DDI(contextType = LabelType.class, field = "getContentArray(0).getStringValue()")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.LabelType.class, field = "setValue(#param)")
    String value;

    @Lunatic(contextType = fr.insee.lunatic.model.flat.LabelType.class, field = "setType('TODO')") //TODO: mapping or processing for this
    String type;

}
