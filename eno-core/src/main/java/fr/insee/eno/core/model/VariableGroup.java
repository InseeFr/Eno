package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import logicalproduct33.VariableGroupType;

import java.util.ArrayList;
import java.util.List;

public class VariableGroup {

    @DDI(contextType = VariableGroupType.class,
            field = "getVariableGroupNameArray(0).getStringArray(0).getStringValue()")
    private String name;

    @DDI(contextType = VariableGroupType.class,
            field = "getVariableGroupReferenceList().![#index.get(#this.getIDArray(0).getStringValue())]")
    private final List<Variable> groupVariables = new ArrayList<>();

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<Variable> getGroupVariables() {
        return groupVariables;
    }
}
