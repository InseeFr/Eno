package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.IVariableType;
import logicalproduct33.VariableType;

public class Variable {

    @DDI(contextType = VariableType.class,
            field = "getVariableNameArray(0).getStringArray(0).getStringValue()")
    @Lunatic(contextType = IVariableType.class, field = "setName(#param)")
    String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
