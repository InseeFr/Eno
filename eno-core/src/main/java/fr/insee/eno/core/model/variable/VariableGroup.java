package fr.insee.eno.core.model.variable;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import logicalproduct33.VariableGroupType;
import lombok.Getter;
import lombok.Setter;
import reusable33.ReferenceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A variable group contains all variables that share a common "scope". For collected variables, this means variables
 * that are collected in a loop or a dynamic table, and loops that are linked. Example: loop "A" contains variables
 * "foo" and "bar", loop "B" is linked to loop "A" and contains the variable "gus", then these two loops corresponds
 * to a single variable group that contains variables "foo", "bar" and "gus". Calculated and external are defined at
 * a certain level (i.e. scope) in Pogues that corresponds to questionnaire-level, or a main loop, or a dynamic table.
 */
@Getter
@Setter
@Context(format = Format.DDI, type = VariableGroupType.class)
public class VariableGroup extends EnoObject {

    public static final String DDI_QUESTIONNAIRE_TYPE = "Questionnaire";

    @DDI("!getVariableGroupNameList().isEmpty ? getVariableGroupNameArray(0).getStringArray(0).getStringValue() : null")
    private String name;

    @DDI("getTypeOfVariableGroup().getStringValue()")
    private String type;

    @DDI("getVariableReferenceList().![#index.get(#this.getIDArray(0).getStringValue())]")
    private final List<Variable> variables = new ArrayList<>();

    /** Ordered list of iterable (i.e. loop or dynamic table) identifiers that correspond to this variable group.
     * The first reference is either a "main" loop or a dynamic table, the others are linked loops.
     * If the variable group corresponds to questionnaire-level variables, this list is empty. */
    @DDI("T(fr.insee.eno.core.model.variable.VariableGroup).getDDILoopReferences(#this)")
    private final List<String> loopReferences = new ArrayList<>();

    public Optional<Variable> getVariableByName(String variableName) {
        return variables.stream()
                .filter(variable -> variable.getName().equals(variableName))
                .findFirst();
    }

    public static List<String> getDDILoopReferences(VariableGroupType ddiVariableGroup) {
        if (DDI_QUESTIONNAIRE_TYPE.equals(ddiVariableGroup.getTypeOfVariableGroup().getStringValue()))
            return new ArrayList<>();
        // iterable means either loop or dynamic table
        List<String> iterableReferences = new ArrayList<>();
        for (ReferenceType reference : ddiVariableGroup.getBasedOnObject().getBasedOnReferenceList()) {
            iterableReferences.add(reference.getIDArray(0).getStringValue());
        }
        return iterableReferences;
    }

}
