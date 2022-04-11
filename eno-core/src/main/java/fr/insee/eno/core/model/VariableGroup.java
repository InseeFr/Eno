package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import logicalproduct33.VariableGroupType;

public class VariableGroup {

    @DDI(contextType = VariableGroupType.class,
            field = "getVariableGroupNameArray(0).getStringArray(0).getStringValue()")
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
