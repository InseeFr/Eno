package fr.insee.eno.core.model.navigation;

import datacollection33.ControlConstructSchemeType;
import datacollection33.LoopType;
import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.converter.DDIConverter;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.DDIIndex;
import group33.ResourcePackageType;
import logicalproduct33.VariableGroupType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reusable33.AbstractIdentifiableType;

import java.util.List;
import java.util.Optional;

/**
 * A linked loop is a loop based on another loop, or based on a dynamic table question.
 */
@Getter
@Setter
@Slf4j
@Context(format = Format.DDI, type = LoopType.class)
public class LinkedLoop extends Loop {

    /** Reference (id) of the loop or dynamic table question on which the linked loop is based on. */
    @DDI("T(fr.insee.eno.core.model.navigation.LinkedLoop).findLoopReference(#this, #index)")
    private String reference;

    // TODO: method bindings -> no fully qualified classes in spel in annotations

    /**
     * In current DDI modeling, a loop object that is based on another loop does not directly have a reference towards
     * the loop it is "based on".
     * The link is done in variable group objects.
     * This method contains the complex searching logic behind this.
     * Note: a simple reference in the DDI loop object would avoid all this...
     * TODO: As long as the DDI modeling is not simplified, dedicated business exception objects would be better. */
    public static String findLoopReference(LoopType ddiLoop, DDIIndex index) {
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
        // Using the variable group, we finally have the "based on" reference:
        // it is the first "BasedOnReference" defined in the variable group
        String referenceObjectId = referenceGroup.get().getBasedOnObject().getBasedOnReferenceArray(0)
                .getIDArray(0).getStringValue();
        if (loopId.equals(referenceObjectId)) {
            throw new MappingException("Linked loop '"+loopId+"' referenced itself. " +
                    "Note: Default implicit value is forbidden. " +
                    "A loop must have either a minimum/maximum or a 'Based on' reference.");
        }
        // The linked loop is returned
        AbstractIdentifiableType loopReference = index.get(referenceObjectId);
        referenceTypeCheck(loopId, loopReference);
        return referenceObjectId;
    }

    private static void referenceTypeCheck(String loopId, AbstractIdentifiableType loopReference) {
        if (! (
                loopReference instanceof LoopType ||
                (loopReference instanceof QuestionGridType questionGridType
                && DDIConverter.instantiateFrom(questionGridType) instanceof DynamicTableQuestion)
        )) {
            throw new MappingException(String.format(
                    "Loop '%s' is based on object '%s' (type '%s'), which is neither a loop nor a dynamic table.",
                    loopId, loopReference.getIDArray(0).getStringValue(), loopReference.getClass()));
        }
    }

}
