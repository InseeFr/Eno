package fr.insee.eno.core.model;

import datacollection33.ComputationItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.ControlType;
import lombok.Getter;
import lombok.Setter;

/** Consistency check. */
@Getter
@Setter
public class Control extends EnoObject {

    @DDI(contextType = ComputationItemType.class, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = ControlType.class, field = "setId(#param)")
    private String id;

    private Instruction instruction;

}
