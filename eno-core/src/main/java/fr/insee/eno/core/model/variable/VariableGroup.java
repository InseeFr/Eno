package fr.insee.eno.core.model.variable;

import fr.insee.ddi.lifecycle33.logicalproduct.VariableGroupType;
import fr.insee.ddi.lifecycle33.logicalproduct.VariableSchemeType;
import fr.insee.ddi.lifecycle33.logicalproduct.VariableType;
import fr.insee.ddi.lifecycle33.reusable.AbstractIdentifiableType;
import fr.insee.ddi.lifecycle33.reusable.ReferenceType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.exceptions.business.IllegalDDIElementException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.DDIIndex;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static final String DDI_QUESTIONNAIRE_TYPE = "Questionnaire";
    private static final String DDI_LOOP_TYPE = "Loop";
    private static final String DDI_PAIRWISE_TYPE = "PairwiseLink";

    /** Type of variable group. */
    public enum Type {

        /** Questionnaire-level variable group. */
        QUESTIONNAIRE,

        /** Loop or dynamic table variable group. */
        LOOP,

        /** Pairwise links variable group. */
        PAIRWISE_LINKS
    }

    /** Business name of the variable group.
     * In DDI, this is equal to the questionnaire business name for questionnaire-level variable group, otherwise
     * is equal to the business name of the iteration object (e.g. main loop or dynamic table) that corresponds
     * to the variable group. */
    @DDI("!getVariableGroupNameList().isEmpty ? getVariableGroupNameArray(0).getStringArray(0).getStringValue() : null")
    private String name;

    /** Type of variable group.
     * In DDI, this property provides a distinction between the questionnaire-level variable group and others. */
    @DDI("T(fr.insee.eno.core.model.variable.VariableGroup).convertDDITypeOfVariableGroup(#this)")
    private Type type;

    /** List of variables that belong to the variable group. */
    @DDI("T(fr.insee.eno.core.model.variable.VariableGroup).getVariables(#this, #index)")
    private final List<Variable> variables = new ArrayList<>();

    /** Ordered list of iterable (i.e. loop or dynamic table) identifiers that correspond to this variable group.
     * The first reference is either a "main" loop or a dynamic table, the others are linked loops.
     * If the variable group corresponds to questionnaire-level variables, this list is empty. */
    @DDI("T(fr.insee.eno.core.model.variable.VariableGroup).getDDILoopReferences(#this, #index)")
    private final List<String> loopReferences = new ArrayList<>();

    public Optional<Variable> getVariableByName(String variableName) {
        return variables.stream()
                .filter(variable -> variable.getName().equals(variableName))
                .findFirst();
    }

    public static List<String> getDDILoopReferences(VariableGroupType ddiVariableGroup, DDIIndex ddiIndex) {
        if (DDI_QUESTIONNAIRE_TYPE.equals(ddiVariableGroup.getTypeOfVariableGroup().getStringValue()))
            return new ArrayList<>();

        // iterable means either loop or dynamic table
        List<String> iterableReferences = new ArrayList<>();
        if(DDI_PAIRWISE_TYPE.equals(ddiVariableGroup.getTypeOfVariableGroup().getStringValue())){
            String variableGroupId = ddiVariableGroup.getIDArray(0).getStringValue();
            VariableSchemeType variableSchemeType = (VariableSchemeType) ddiIndex.getParent(variableGroupId);
            Optional<VariableGroupType> optionalVariableGroupOrigin = variableSchemeType.getVariableGroupList().stream()
                    .filter(variableGroupType -> variableGroupType.getVariableGroupReferenceList().stream()
                            .anyMatch(ref->variableGroupId.equals(ref.getIDArray(0).getStringValue()))
            ).findFirst();
            if(optionalVariableGroupOrigin.isPresent()){
                VariableGroupType variableGroupOrigin = optionalVariableGroupOrigin.get();
                return getDDILoopReferences(variableGroupOrigin, ddiIndex);
            }
        }
        for (ReferenceType reference : ddiVariableGroup.getBasedOnObject().getBasedOnReferenceList()) {
            iterableReferences.add(reference.getIDArray(0).getStringValue());
        }
        return iterableReferences;
    }

    public static Type convertDDITypeOfVariableGroup(VariableGroupType ddiVariableGroup) {
        String ddiTypeOfGroup = ddiVariableGroup.getTypeOfVariableGroup().getStringValue();
        return switch (ddiTypeOfGroup) {
            case DDI_QUESTIONNAIRE_TYPE -> Type.QUESTIONNAIRE;
            case DDI_LOOP_TYPE -> Type.LOOP;
            case DDI_PAIRWISE_TYPE -> Type.PAIRWISE_LINKS;
            default -> throw new IllegalDDIElementException(String.format(
                    "Invalid type of variable group '%s' in DDI variable group '%s'.",
                    ddiTypeOfGroup, ddiVariableGroup.getIDArray(0).getStringValue()));
        };
    }

    public static List<VariableType> getVariables(VariableGroupType ddiVariableGroup, DDIIndex ddiIndex){
        List<VariableType> variables = new ArrayList<>(ddiVariableGroup.getVariableReferenceList().stream()
                .map(varRef -> ddiIndex.get(varRef.getIDArray(0).getStringValue()))
                .map(VariableType.class::cast)
                .toList());

        ddiVariableGroup.getVariableGroupReferenceList().forEach( vgRef -> {
            VariableGroupType variableGroupInside = (VariableGroupType) ddiIndex.get(vgRef.getIDArray(0).getStringValue());
            String ddiTypeOfGroupInside = variableGroupInside.getTypeOfVariableGroup().getStringValue();
            if(DDI_PAIRWISE_TYPE.equals(ddiTypeOfGroupInside)) variables.addAll(getVariables(variableGroupInside, ddiIndex));
        });
        return variables;

    }

}
