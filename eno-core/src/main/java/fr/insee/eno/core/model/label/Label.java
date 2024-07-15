package fr.insee.eno.core.model.label;

import fr.insee.ddi.lifecycle33.reusable.LabelType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import lombok.Getter;
import lombok.Setter;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** Label object that is used e.g. in sequences or code lists.
 * This class is designed to map DDI "Label" objects.
 * There are several classes for different kinds of label in DDI, but use cases are the same for each of them.
 * In Lunatic, a label is always interpreted as a VTL expression (with eventually Markdown syntax being also
 * interpreted afterward).
 * Note that each kind of label correspond to a VTL expression in Lunatic. Thus, the value of a label is expected to
 * be a valid VTL expression. This especially implies that a static label should start and end with a quote character.
 */
@Getter
@Setter
@Context(format = Format.DDI, type = LabelType.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.LabelType.class)
public class Label extends EnoObject implements EnoLabel {

    /** Text content of the label, which can be either static or dynamic.
     * In DDI, if the label contains variables, their names are replaced by references.
     * There is a processing class to resolve the references and put back variable names instead. */
    @DDI("getContentArray(0).getStringValue()")
    @Lunatic("setValue(#param)")
    String value;

    /** Property that is specific to Lunatic.
     * For now, Lunatic type in label objects does not come from metadata, but is hardcoded here in Eno.
     * See labels documentation. */
    @Lunatic("setType(T(fr.insee.lunatic.model.flat.LabelTypeEnum).fromValue(#param))")
    String type = LabelTypeEnum.VTL_MD.value();

}
