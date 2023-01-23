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
    @DDI(contextType = LoopType.class, field = "#this?.getLoopWhile()?.getCommandArray(0)")
    private CalculatedExpression maxIteration;

    /** Sequence or sub-sequence to loop on.
     * In Lunatic, components within the referenced sequence will be moved in the loop components' list.
     * This is done in a Lunatic processing. */
    @DDI(contextType = LoopType.class,
            field = "T(fr.insee.eno.core.model.navigation.Loop).mapSequenceReference(#this)") // TODO: method bindings (-> no fully qualified classes in spel)
    private String sequenceReference;

    public static CommandType mapMinIteration(LoopType ddiLoop, DDIIndex index) {
        if (ddiLoop.getInitialValue() != null) {
            return ddiLoop.getInitialValue().getCommandArray(0);
        } else {
            String loopId = ddiLoop.getIDArray(0).getStringValue();
            ControlConstructSchemeType controlConstructScheme = (ControlConstructSchemeType) index.getParent(ddiLoop.getIDArray(0).getStringValue());
            ResourcePackageType resourcePackage  = (ResourcePackageType) index.getParent(controlConstructScheme.getIDArray(0).getStringValue());
            Optional<VariableGroupType> referenceGroup = resourcePackage.getVariableSchemeArray(0).getVariableGroupList()
                    .stream()
                    .filter(variableGroupType -> variableGroupType.getBasedOnObject().getBasedOnReferenceList()
                            .stream()
                            .map(referenceType -> referenceType.getIDArray(0).getStringValue())
                            .anyMatch(loopId::equals))
                    .findAny();
            if (referenceGroup.isEmpty()) {
                throw new MappingException("Group associated with linked loop not found.");
                //TODO: dedicated business exception
            }
            String linkedLoopId = referenceGroup.get().getBasedOnObject().getBasedOnReferenceArray(0)
                    .getIDArray(0).getStringValue();
            if (loopId.equals(linkedLoopId)) {
                throw new MappingException("Default implicit value is forbidden. " +
                        "A loop must have either a minimum/maximum or a 'Based on' loop");
                //TODO: dedicated business exception
            }
            AbstractIdentifiableType loopReference = index.get(linkedLoopId);
            if (! (loopReference instanceof LoopType)) {
                log.error(String.format("Loop '%s' is based on object '%s' of type '%s'.",
                        loopId, linkedLoopId, loopReference.getClass()));
                //throw new MappingException("Linked loop is only supported for loop based on other loop");
                return null;
                //TODO: dedicated business exception
            }
            return ((LoopType) loopReference).getInitialValue().getCommandArray(0);
        }
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
