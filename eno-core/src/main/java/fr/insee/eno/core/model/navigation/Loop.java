package fr.insee.eno.core.model.navigation;

import datacollection33.ControlConstructSchemeType;
import datacollection33.LoopType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.reference.DDIIndex;
import group33.ResourcePackageType;
import logicalproduct33.VariableGroupType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reusable33.AbstractIdentifiableType;
import reusable33.CommandType;
import reusable33.ReferenceType;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Slf4j
public class Loop extends EnoIdentifiableObject {

    /** Loop business name.
     * Unused in Lunatic. */
    @DDI(contextType = LoopType.class, field = "getConstructNameArray(0).getStringArray(0).getStringValue()")
    private String name;

    /** Minimum number of iterations allowed.
     * In Pogues, this field is not mandatory if the "Based on" field is specified.
     * The value is a VTL expression. */
    @DDI(contextType = LoopType.class,
            field = "T(fr.insee.eno.core.model.navigation.Loop).mapMinIteration(#this, #index)")
    private CalculatedExpression minIteration;

    /** Minimum number of iterations allowed.
     * See 'minIteration' for details. */
    @DDI(contextType = LoopType.class,
            field = "T(fr.insee.eno.core.model.navigation.Loop).mapMaxIteration(#this, #index)")
    private CalculatedExpression maxIteration;

    /** Sequence or sub-sequence to loop on.
     * In Lunatic, components within the referenced sequence will be moved in the loop components' list.
     * This is done in a Lunatic processing. */
    @DDI(contextType = LoopType.class,
            field = "T(fr.insee.eno.core.model.navigation.Loop).mapSequenceReference(#this)")
    private String sequenceReference;

    // TODO: method bindings -> no fully qualified classes in spel in annotations

    public static CommandType mapMinIteration(LoopType ddiLoop, DDIIndex index) {
        if (ddiLoop.getInitialValue() != null) {
            return ddiLoop.getInitialValue().getCommandArray(0);
        } else {
            LoopType loopReference = findBasedOnLoop(ddiLoop, index);
            return loopReference.getInitialValue().getCommandArray(0);
        }
    }

    public static CommandType mapMaxIteration(LoopType ddiLoop, DDIIndex index) {
        if (ddiLoop.getLoopWhile() != null) {
            return ddiLoop.getLoopWhile().getCommandArray(0);
        } else {
            LoopType loopReference = findBasedOnLoop(ddiLoop, index);
            return loopReference.getLoopWhile().getCommandArray(0);
        }
    }

    /**
     * In current DDI modeling, a loop object that is based on another loop does not directly have a reference towards
     * the loop it is "based on".
     * The link is done in variable group objects.
     * This method contains the complex searching logic behind this.
     * Note: a simple reference in the DDI loop object would avoid all this...
     * TODO: As long as the DDI modeling is not simplified, dedicated business exception objects would be better. */
    private static LoopType findBasedOnLoop(LoopType ddiLoop, DDIIndex index) {
        String loopId = ddiLoop.getIDArray(0).getStringValue();
        // Get the variable groups list
        ControlConstructSchemeType controlConstructScheme = (ControlConstructSchemeType) index
                .getParent(ddiLoop.getIDArray(0).getStringValue());
        ResourcePackageType resourcePackage  = (ResourcePackageType) index
                .getParent(controlConstructScheme.getIDArray(0).getStringValue());
        List<VariableGroupType> variableGroupList = resourcePackage
                .getVariableSchemeArray(0).getVariableGroupList();
        // Find the variable group that contains a "BasedOnReference" object that matches the loop id
        Optional<VariableGroupType> referenceGroup = variableGroupList.stream()
                .filter(variableGroupType -> variableGroupType.getBasedOnObject().getBasedOnReferenceList()
                        .stream()
                        .map(referenceType -> referenceType.getIDArray(0).getStringValue())
                        .anyMatch(loopId::equals))
                .findAny();
        if (referenceGroup.isEmpty()) {
            throw new MappingException("Group associated with linked loop not found.");
        }
        // Using the variable group, we finally have the linked ("based on") loop reference
        String linkedLoopId = referenceGroup.get().getBasedOnObject().getBasedOnReferenceArray(0)
                .getIDArray(0).getStringValue();
        if (loopId.equals(linkedLoopId)) {
            throw new MappingException("Default implicit value is forbidden. " +
                    "A loop must have either a minimum/maximum or a 'Based on' loop");
        }
        // The linked loop is returned
        AbstractIdentifiableType loopReference = index.get(linkedLoopId);
        if (! (loopReference instanceof LoopType)) {
            throw new MappingException(String.format(
                    "Loop '%s' is based on object '%s' of type '%s' (which is not a loop!!).",
                    loopId, linkedLoopId, loopReference.getClass()));
        }
        return (LoopType) loopReference;
    }

    public static String mapSequenceReference(LoopType ddiLoop) {
        ReferenceType controlConstruct = ddiLoop.getControlConstructReference();
        String typeOfObject = controlConstruct.getTypeOfObject().toString();
        if (! "Sequence".equals(typeOfObject)) {
            log.warn(String.format("DDI loop '%s' references an object of type '%s' (should be 'Sequence')",
                    ddiLoop.getIDArray(0).getStringValue(), typeOfObject));
        }
        return controlConstruct.getIDArray(0).getStringValue();
    }

}
