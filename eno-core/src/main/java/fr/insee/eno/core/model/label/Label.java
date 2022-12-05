package fr.insee.eno.core.model.label;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import lombok.Getter;
import lombok.Setter;
import reusable33.LabelType;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** Label object that is used in components */
@Getter
@Setter
@Context(format = Format.DDI, type = LabelType.class)
public class Label extends EnoObject {

    @DDI(contextType = LabelType.class, field = "getContentArray(0).getStringValue()")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.LabelType.class, field = "setValue(#param)")
    String value;

    /** For now, Lunatic type in label objects does not come from metadata, but is hardcoded here in Eno.
     * See labels documentation. */
    @Lunatic(contextType = fr.insee.lunatic.model.flat.LabelType.class, field = "setType('VTL|MD')")
    String type;

}
