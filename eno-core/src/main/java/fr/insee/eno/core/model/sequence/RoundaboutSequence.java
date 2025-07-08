package fr.insee.eno.core.model.sequence;

import fr.insee.ddi.lifecycle33.datacollection.*;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.DDIIndex;
import fr.insee.pogues.model.RoundaboutType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Special kind of sequence that describes a "roundabout".
 * A roundabout is like a loop with refinements.
 * In DDI, the roundabout is described as a DDI loop, with a DDI sequence that encapsulates it.
 * In Lunatic, roundabout components are resolved in a dedicated processing.
 */
@Getter
@Setter
@Context(format = Format.POGUES, type = RoundaboutType.class)
@Context(format = Format.DDI, type = SequenceType.class)
public class RoundaboutSequence extends AbstractSequence {

    public static final String DDI_INSTANCE_LABEL_TYPE = "loop.instanceLabel";
    public static final String DDI_INSTANCE_DESCRIPTION_TYPE = "loop.instanceDescription";
    private static final String DDI_LOCKED_CONTROL_TYPE = "roundabout-locked";

    /** DDI reference of the loop.
     * Note: mapped as the id of the first control construct reference. */
    @Pogues("getId() + '-ROUNDABOUT_LOOP'")
    @DDI("getControlConstructReferenceArray(0).getIDArray(0).getStringValue()")
    private String loopReference;

    @Pogues("getLoop()")
    private LinkedLoop innerLoop;

    /** Boolean that describes if the completed occurrences should be locked or not.
     * In DDI, this is modeled by the presence or not of a ComputationItem among the control construct references. */
    @Pogues("isLocked()")
    @DDI("T(fr.insee.eno.core.model.sequence.RoundaboutSequence).ddiLockedProperty(#this, #index)")
    private Boolean locked;

    @DDI("T(fr.insee.eno.core.model.sequence.RoundaboutSequence).mapDDISpecialInstruction(#this, #index, " +
            "T(fr.insee.eno.core.model.sequence.RoundaboutSequence).DDI_INSTANCE_LABEL_TYPE)")
    private DynamicLabel occurrenceLabel;

    @DDI("T(fr.insee.eno.core.model.sequence.RoundaboutSequence).mapDDISpecialInstruction(#this, #index, " +
            "T(fr.insee.eno.core.model.sequence.RoundaboutSequence).DDI_INSTANCE_DESCRIPTION_TYPE)")
    private DynamicLabel occurrenceDescription;

    /**
     * As dynamic tables, roundabouts can have controls (roundabout-level controls and/or occurrence-level controls).
     * In DDI, the controls are mapped at the questionnaire level and are inserted here through a processing.
     * @see fr.insee.eno.core.processing.in.steps.ddi.DDIInsertControls
     * In Lunatic, the processing that transforms loops into roundabouts uses this list.
     * @see fr.insee.eno.core.processing.out.steps.lunatic.LunaticRoundaboutLoops
     */
    private List<Control> controls = new ArrayList<>();

    public static boolean ddiLockedProperty(SequenceType ddiRoundaboutSequence, DDIIndex ddiIndex) {
        return ddiRoundaboutSequence.getControlConstructReferenceList().stream()
                .filter(reference -> "ComputationItem".equals(reference.getTypeOfObject().toString()))
                .map(reference -> ddiIndex.get(reference.getIDArray(0).getStringValue()))
                .filter(Objects::nonNull)
                .filter(ComputationItemType.class::isInstance)
                .map(ComputationItemType.class::cast)
                .anyMatch(computationItem ->
                        DDI_LOCKED_CONTROL_TYPE.equals(computationItem.getTypeOfComputationItem().getStringValue()));
    }

    public static DynamicTextType mapDDISpecialInstruction(SequenceType ddiSequence, DDIIndex ddiIndex, String instructionName) {
        return ddiSequence.getInterviewerInstructionReferenceList().stream()
                .map(instructionReference -> ddiIndex.get(instructionReference.getIDArray(0).getStringValue()))
                .map(InstructionType.class::cast)
                .filter(ddiInstruction -> instructionName.equals(ddiInstruction.getInstructionNameArray(0).getStringArray(0).getStringValue()))
                .map(ddiInstruction -> ddiInstruction.getInstructionTextArray(0))
                .findAny().orElse(null);
    }

}
