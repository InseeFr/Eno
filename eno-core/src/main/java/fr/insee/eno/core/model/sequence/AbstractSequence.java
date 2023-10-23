package fr.insee.eno.core.model.sequence;

import datacollection33.SequenceType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Sequence;
import fr.insee.lunatic.model.flat.Subsequence;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Abstract object for sequence and subsequence.
 * In DDI, a sequence or subsequence is a SequenceType object.
 * In Lunatic, a sequence is a Sequence object, a subsequence is a Subsequence object. */
@Getter
@Setter
@Context(format = Format.DDI, type = SequenceType.class)
@Context(format = Format.LUNATIC, type = {Sequence.class, Subsequence.class})
public abstract class AbstractSequence extends EnoIdentifiableObject implements EnoComponent {

    /** Sequence / subsequence label. */
    @DDI("getLabelArray(0)")
    @Lunatic("setLabel(#param)")
    Label label;

    /** Sequence / subsequence instructions.
     * In DDI, the SequenceType object contains the list of references to the instructions.
     * In Lunatic, instructions and declarations belongs to the same list. */
    @DDI("getInterviewerInstructionReferenceList().![#index.get(#this.getIDArray(0).getStringValue())]")
    @Lunatic("getDeclarations()")
    private final List<Instruction> instructions = new ArrayList<>();

    /** Sequence / subsequence declarations.
     * In DDI, the declarations are mapped in the questionnaire object, and are put here through a 'processing' class.
     * In Lunatic, instructions and declarations belongs to the same list.
     * TODO: NOTE: seems like a sequence or subsequence cannot have declarations (Pogues). */
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
     * In DDI, this list is filled in a processing class using the 'sequenceItems' list. */
    private final List<StructureItemReference> sequenceStructure = new ArrayList<>();

}
