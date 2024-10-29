package fr.insee.eno.core.model.sequence;

import fr.insee.ddi.lifecycle33.datacollection.SequenceType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Special kind of sequence that describes a "roundabout".
 * A roundabout is like a loop with refinements.
 * In DDI, the roundabout is described as a DDI loop, with a DDI sequence that encapsulates it.
 * In Lunatic, roundabout components are resolved in a dedicated processing.
 */
@Getter
@Setter
@Context(format = Format.DDI, type = SequenceType.class)
public class RoundaboutSequence extends AbstractSequence {

    /** DDI reference of the loop.
     * Note: mapped as the id of the first control construct reference. */
    @DDI("getControlConstructReferenceArray(0).getIDArray(0).getStringValue()")
    private String loopReference;

    /** Boolean that describes if the completed occurrences should be locked or not.
     * In DDI, this is modeled by the presence or not of a ComputationItem among the control construct references. */
    @DDI("!getControlConstructReferenceList().?[#this.getTypeOfObject().toString() == 'ComputationItem'].isEmpty()")
    private Boolean locked;

    /**
     * As dynamic tables, roundabouts can have controls (roundabout-level controls and/or occurrence-level controls).
     * In DDI, the controls are mapped at the questionnaire level and are inserted here through a processing.
     * @see fr.insee.eno.core.processing.in.steps.ddi.DDIInsertControls
     * In Lunatic, the processing that transforms loops into roundabouts uses this list.
     * @see fr.insee.eno.core.processing.out.steps.lunatic.LunaticRoundaboutLoops
     */
    private List<Control> controls = new ArrayList<>();

}
