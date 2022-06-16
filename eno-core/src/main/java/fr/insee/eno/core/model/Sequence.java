package fr.insee.eno.core.model;

import datacollection33.SequenceType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sequence {

    @DDI(contextType = SequenceType.class, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.SequenceType.class, field = "setId(#param)")
    private String id;

}
