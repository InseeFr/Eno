package fr.insee.eno.core.model.multimode;

import fr.insee.eno.core.annotations.Contexts;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.multimode.MultimodeRule;
import fr.insee.pogues.model.Rule;
import fr.insee.pogues.model.Rules;
import fr.insee.pogues.model.ValueTypeEnum;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Contexts.Context(format = Format.POGUES, type = Rule.class)
@Contexts.Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.multimode.MultimodeRule.class)
public class EnoRule {

    @Pogues("getName().value()")
    @Lunatic("setName(#param)")
    String name;

    @Pogues("getType().value()")
    @Lunatic("setType(fr.insee.lunatic.model.flat.LabelTypeEnum.valueOf(#param))")
    String type;

    @Pogues("getValue()")
    @Lunatic("setValue(#param)")
    String value;
}
