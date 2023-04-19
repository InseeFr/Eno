package fr.insee.eno.core.model.navigation;

import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Loop extends EnoObject {

    @Lunatic(contextType = fr.insee.lunatic.model.flat.Subsequence.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    private String componentType = "LOOP";

}
