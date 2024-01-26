package fr.insee.eno.core.model.label;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.LabelType;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import lombok.Getter;
import lombok.Setter;
import reusable33.InternationalStringType;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** Label object used at the questionnaire level.
 * This class is designed to map DDI "InternationalString" content.
 * @see Label */
@Getter
@Setter
@Context(format = Format.DDI, type = InternationalStringType.class)
@Context(format = Format.LUNATIC, type = LabelType.class)
public class QuestionnaireLabel extends EnoObject implements EnoLabel {

    /** Label content.
     * @see Label for details.
     */
    @DDI("getStringArray(0).getStringValue()")
    @Lunatic("setValue(#param)")
    String value;

    /** Property that is specific to Lunatic.
     * For now, Lunatic type does not come from metadata, but is hardcoded here in Eno.
     * See labels documentation. */
    @Lunatic("setType(T(fr.insee.lunatic.model.flat.LabelTypeEnum).fromValue(#param))")
    String type = LabelTypeEnum.VTL_MD.value();

}
