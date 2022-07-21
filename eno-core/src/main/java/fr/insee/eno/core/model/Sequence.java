package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.Lunatic;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sequence extends AbstractSequence {

    @Lunatic(contextType = fr.insee.lunatic.model.flat.SequenceType.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    private String componentType = "SEQUENCE";

}
