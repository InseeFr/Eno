package fr.insee.eno.core.model.question;

import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.CheckboxBoolean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BooleanQuestion extends SingleResponseQuestion {

    @Lunatic(contextType = CheckboxBoolean.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "CHECKBOX_BOOLEAN";

}
