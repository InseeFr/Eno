package fr.insee.eno.core.model.multimode;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.multimode.MultimodeRule;
import fr.insee.pogues.model.Rule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.POGUES, type = Rule.class)
@Context(format = Format.LUNATIC, type = MultimodeRule.class)
public class EnoMultimodeRule extends EnoObject {

    /** In Lunatic, this is a key of the map.
     * @see fr.insee.eno.core.processing.out.steps.lunatic.LunaticMultimodeRules */
    @Pogues("getName().value()")
    private String name;

    @Pogues("getType().value()")
    @Lunatic("setType(T(fr.insee.lunatic.model.flat.LabelTypeEnum).valueOf(#param))")
    private String type;

    @Pogues("T(fr.insee.eno.core.model.calculated.CalculatedExpression).removeSurroundingDollarSigns(getValue())")
    @Lunatic("setValue(#param)")
    private String value;

}
