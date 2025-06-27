package fr.insee.eno.core.model.sequence;

import fr.insee.ddi.lifecycle33.datacollection.InstructionType;
import fr.insee.ddi.lifecycle33.datacollection.SequenceType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.DDIIndex;
import fr.insee.lunatic.model.flat.Sequence;
import fr.insee.lunatic.model.flat.Subsequence;
import fr.insee.pogues.model.ComponentType;
import fr.insee.pogues.model.RoundaboutType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Abstract object for sequence and subsequence.
 * In DDI, a sequence or subsequence is a SequenceType object.
 * In Lunatic, a sequence is a Sequence object, a subsequence is a Subsequence object. */
@Getter
@Setter
@Context(format = Format.POGUES, type = {fr.insee.pogues.model.SequenceType.class, RoundaboutType.class})
@Context(format = Format.DDI, type = SequenceType.class)
@Context(format = Format.LUNATIC, type = {Sequence.class, Subsequence.class})
public abstract class AbstractSequence extends EnoIdentifiableObject implements EnoComponent {

    /** Business identifier name of the sequence/subsequence. */
    @Pogues("getName()")
    @DDI("!getConstructNameList().isEmpty() ? getConstructNameArray(0).getStringArray(0).getStringValue() : null")
    private String name;

    /** Sequence / subsequence label. */
    @Pogues("!getLabel().isEmpty ? getLabel().getFirst() : null")
    @DDI("getLabelArray(0)")
    @Lunatic("setLabel(#param)")
    Label label;

    /** Sequence / subsequence instructions.
     * In DDI, the SequenceType object contains the list of references to the instructions.
     * In Lunatic, instructions and declarations belongs to the same list. */
    @Pogues("getDeclaration().?[#this.getPosition().value() == 'AFTER_QUESTION_TEXT']")
    @DDI("T(fr.insee.eno.core.model.sequence.AbstractSequence).mapDDIInstructions(#this, #index)")
    @Lunatic("getDeclarations()")
    private final List<Instruction> instructions = new ArrayList<>();

    /** Sequence / subsequence declarations.
     * In DDI, the declarations are mapped in the questionnaire object, and are put here through a 'processing' class.
     * In Lunatic, instructions and declarations belongs to the same list.
     * TODO: NOTE: seems like a sequence or subsequence cannot have declarations (Pogues). */
    @Pogues("getDeclaration().?[#this.getPosition().value() == 'BEFORE_QUESTION_TEXT']")
    @Lunatic("getDeclarations()")
    private final List<Declaration> declarations = new ArrayList<>();

    /** Sequence / subsequence filter.
     * In DDI, the filters are mapped in the questionnaire object.
     * If there is a declared filter for this sequence / subsequence, it is put here through a 'processing' class.
     * Otherwise, there is a default filter (with expression "true").
     * In Lunatic, a ComponentType object has a ConditionFilter object. */
    @Lunatic("setConditionFilter(#param)")
    private ComponentFilter componentFilter = new ComponentFilter();

    /** Ordered list of all items in the sequence / subsequence.
     * Important note: if a loop is defined over a sequence or subsequence, the sequence item will correspond to
     * the loop (and not the sequence or subsequence), same for the filters. */
    @DDI("getControlConstructReferenceList()")
    private final List<ItemReference> sequenceItems = new ArrayList<>();

    /** Ordered list of only subsequence and question items.
     * TODO: proper oop to make a difference between subsequences/question, loops/filters and controls/declarations
     * In DDI, this list is filled in a processing class using the 'sequenceItems' list.
     * In Pogues, for now roundabouts are filtered (we'll see how we want to manage these later). */
    @Pogues("T(fr.insee.eno.core.model.sequence.AbstractSequence).mapPoguesStructure(#this)")
    private final List<StructureItemReference> sequenceStructure = new ArrayList<>();

    public static List<ComponentType> mapPoguesStructure(fr.insee.pogues.model.SequenceType poguesSequence) {
        return poguesSequence.getChild().stream()
                .filter(child -> !(child instanceof RoundaboutType)).toList();
    }
    /** Pogues roundabout sequence objects don't have child components. */
    public static List<ComponentType> mapPoguesStructure(RoundaboutType poguesRoundabout) {
        return new ArrayList<>();
    }

    public static List<InstructionType> mapDDIInstructions(SequenceType ddiSequence, DDIIndex ddiIndex) {
        return ddiSequence.getInterviewerInstructionReferenceList().stream()
                .map(instructionReference -> ddiIndex.get(instructionReference.getIDArray(0).getStringValue()))
                .map(InstructionType.class::cast)
                .filter(ddiInstruction -> {
                    String instructionName = ddiInstruction.getInstructionNameArray(0).getStringArray(0).getStringValue();
                    return ! (RoundaboutSequence.DDI_INSTANCE_LABEL_TYPE.equals(instructionName)
                            || RoundaboutSequence.DDI_INSTANCE_DESCRIPTION_TYPE.equals(instructionName));
                })
                .toList();
    }

}
