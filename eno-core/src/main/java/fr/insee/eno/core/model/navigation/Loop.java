package fr.insee.eno.core.model.navigation;

import datacollection33.LoopType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reusable33.ReferenceType;

import java.util.ArrayList;
import java.util.List;

/**
 * A loop is defined by its scope, which is a sequence or subsequence reference.
 * TODO: current DDI modeling only works with scope = single sequence/subsequence. */
@Getter
@Setter
@Slf4j
public abstract class Loop extends EnoIdentifiableObject {

    @Lunatic(contextType = fr.insee.lunatic.model.flat.Subsequence.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    private String componentType = "LOOP";

    /** Loop business name.
     * Unused in Lunatic. */
    @DDI(contextType = LoopType.class, field = "getConstructNameArray(0).getStringArray(0).getStringValue()")
    private String name;

    /** Sequence or sub-sequence to loop on.
     * In Lunatic, components within the referenced sequence will be moved in the loop components' list.
     * This is done in a Lunatic processing. */
    @DDI(contextType = LoopType.class,
            field = "T(fr.insee.eno.core.model.navigation.Loop).mapSequenceReference(#this)")
    private String sequenceReference; // TODO: to be replaced by a loopScope property analog to what's done for filters

    /** Same principle as sequence items list in sequence objects.
     * TODO: waiting for a fix in DDI modeling, this should be a List<StructureItemReference>
     *     with DDI mapping expression:
     *     #index.get(getControlConstructReferenceArray(0).getIDArray(0).getStringValue()).getControlConstructReferenceList()
     * */
    @DDI(contextType = LoopType.class, field = "T(java.util.List).of(#this.getControlConstructReference())")
    private final List<ItemReference> loopItems = new ArrayList<>();

    /** References of sequences or subsequences that are in the scope of the loop.
     * Note: in Pogues a loop can only be defined on sequence or subsequences.
     * (In other formats, nothing makes it formally impossible to have loops defined directly on questions.)
     * In DDI, this property is filled by a processing using the "loopItems" property. */
    private final List<StructureItemReference> loopScope = new ArrayList<>();

    public static String mapSequenceReference(LoopType ddiLoop) {
        ReferenceType controlConstruct = ddiLoop.getControlConstructReference();
        String typeOfObject = controlConstruct.getTypeOfObject().toString();
        if (! "Sequence".equals(typeOfObject)) {
            log.warn(String.format("DDI loop '%s' references an object of type '%s' (should be 'Sequence')",
                    ddiLoop.getIDArray(0).getStringValue(), typeOfObject));
        }
        return controlConstruct.getIDArray(0).getStringValue();
    }

    /** A loop can be in the scope of a filter.
     * In DDI, filters are mapped at questionnaire level and inserted through a processing step. */
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Loop.class, field = "setConditionFilter(#param)")
    private Filter filter;

}
