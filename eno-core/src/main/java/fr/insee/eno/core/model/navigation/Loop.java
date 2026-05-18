package fr.insee.eno.core.model.navigation;

import fr.insee.ddi.lifecycle33.datacollection.LoopType;
import fr.insee.ddi.lifecycle33.datacollection.SequenceType;
import fr.insee.ddi.lifecycle33.reusable.ReferenceType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.DDIIndex;
import fr.insee.pogues.model.IterationType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * A loop is defined by its scope, which is a list of sequence or subsequence references.
 * */
@Getter
@Setter
@Slf4j
@Context(format = Format.DDI, type = LoopType.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.Loop.class)
@Context(format = Format.POGUES, type = IterationType.class)
public abstract class Loop extends EnoIdentifiableObject {

    /** Loop business name.
     * Unused in Lunatic. */
    @Pogues("getName()")
    @DDI("getConstructNameArray(0).getStringArray(0).getStringValue()")
    private String name;

    /** Same principle as sequence items list in sequence objects. */
    @DDI("T(fr.insee.eno.core.model.navigation.Loop).mapLoopItemReferences(#this, #index)")
    private final List<ItemReference> loopItems = new ArrayList<>();

    // Note: in the current Pogues model, the "member reference" property for loops is a list that should contain
    // exactly two elements. It is what it is...
    /** Identifier of the component (sequence or subsequence) on which the loop starts.
     * Mapped from Pogues and used to compute the loop's scope. */
    @Pogues("getMemberReference().getFirst()")
    private String poguesStartReference;
    /** Identifier of the component (sequence or subsequence) on which the loop starts.
     * Mapped from Pogues and used to compute the loop's scope. */
    @Pogues("getMemberReference().getLast()")
    private String poguesEndReference;

    @Pogues("isShouldSplitIterations()")
    @Lunatic("setIsPaginatedByIterations(#param)")
    private Boolean occurrencePagination;

    /** The occurrences of a loop can be filtered.
     * This attribute holds the identifier of the occurrence filter object.
     * null if there is no occurrence filter in the loop. */
    @DDI("getControlConstructReference().getTypeOfObject().toString() == 'IfThenElse' ? " +
            "getControlConstructReference().getIDArray(0).getStringValue() : null")
    private String occurrenceFilterId;

    /** References of sequences or subsequences that are in the scope of the loop.
     * Note: in Pogues a loop can only be defined on sequence or subsequences.
     * (In other formats, nothing makes it formally impossible to have loops defined directly on questions.)
     * In DDI, this property is filled by a processing using the "loopItems" property.
     * @see fr.insee.eno.core.processing.in.steps.ddi.DDIResolveLoopsScope
     * In Poues, it is filled using the "start id" and "end id" properties.
     * @see fr.insee.eno.core.processing.in.steps.pogues.PoguesResolveLoopScope
     */
    private final List<StructureItemReference> loopScope = new ArrayList<>();
    
    public static List<ReferenceType> mapLoopItemReferences(LoopType ddiLoop, DDIIndex ddiIndex) {
        ReferenceType controlConstructReference = ddiLoop.getControlConstructReference();
        String referencedControlConstructType = controlConstructReference.getTypeOfObject().toString();
        if ("Sequence".equals(referencedControlConstructType)) {
            SequenceType ddiSequence = (SequenceType) ddiIndex.get(controlConstructReference.getIDArray(0).getStringValue());
            return ddiSequence.getControlConstructReferenceList();
        }
        if ("IfThenElse".equals(referencedControlConstructType)) {
            return List.of(ddiLoop.getControlConstructReference());
        }
        throw new MappingException(String.format(
                "DDI loop '%s' references an object of unexpected type: '%s'.",
                ddiLoop.getIDArray(0).getStringValue(), referencedControlConstructType));
    }

}
