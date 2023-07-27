package fr.insee.eno.core.model.variable;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoObject;
import logicalproduct33.VariableGroupType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VariableGroup extends EnoObject {

    @DDI("!getVariableGroupNameList().isEmpty ? getVariableGroupNameArray(0).getStringArray(0).getStringValue() : null")
    private String name;

    @DDI("getVariableReferenceList().![#index.get(#this.getIDArray(0).getStringValue())]")
    private final List<Variable> variables = new ArrayList<>();

}
