package fr.insee.eno.core.model.variable;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoObject;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class VariableGroup extends EnoObject {

    @DDI("!getVariableGroupNameList().isEmpty ? getVariableGroupNameArray(0).getStringArray(0).getStringValue() : null")
    private String name;

    @DDI("getTypeOfVariableGroup().getStringValue()")
    private String type;

    @DDI("getVariableReferenceList().![#index.get(#this.getIDArray(0).getStringValue())]")
    private final List<Variable> variables = new ArrayList<>();

    public Optional<Variable> getVariableByName(String variableName) {
        return variables.stream()
                .filter(variable -> variable.getName().equals(variableName))
                .findFirst();
    }
}
