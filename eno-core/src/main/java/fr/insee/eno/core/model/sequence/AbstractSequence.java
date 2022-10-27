package fr.insee.eno.core.model.sequence;

import datacollection33.SequenceType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.*;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.lunatic.model.flat.Subsequence;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Abstract object for sequence and subsequence.
 * In DDI, a sequence or subsequence is a SequenceType object.
 * In Lunatic, a sequence is a SequenceType, a subsequence is a Subsequence object. */
@Getter
@Setter
public abstract class AbstractSequence extends EnoIdentifiableObject implements EnoComponent {

    /** Sequence / subsequence label. */
    @DDI(contextType = SequenceType.class, field = "getLabelArray(0)")
    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "setLabel(#param)")
    Label label;

    /** Sequence / subsequence instructions.
     * In DDI, the SequenceType object contains the list of references to the instructions.
     * In Lunatic, instructions and declarations belongs to the same list. */
    @DDI(contextType = SequenceType.class,
            field = "getInterviewerInstructionReferenceList().![#index.get(#this.getIDArray(0).getStringValue())]")
    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "getDeclarations()")
    private final List<Instruction> instructions = new ArrayList<>();

    /** Sequence / subsequence declarations.
     * In DDI, the declarations are mapped in the questionnaire object, and are put here through a 'processing' class.
     * In Lunatic, instructions and declarations belongs to the same list.
     * TODO: NOTE: seems like a sequence or subsequence cannot have declarations (Pogues). */
    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "getDeclarations()")
    private final List<Declaration> declarations = new ArrayList<>();

    /** Sequence / subsequence controls.
     * In DDI, the controls are mapped in the questionnaire object, and are put here through a 'processing' class.
     * In Lunatic, the sequence / subsequence object has a list of controls. */
    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "getControls()")
    private final List<Control> controls = new ArrayList<>();

    /** Sequence / subsequence filter.
     * In DDI, the filters are mapped in the questionnaire object.
     * If there is a declared filter for this sequence / subsequence, it is put here through a 'processing' class.
     * Otherwise, there is a default filter (with expression "true").
     * In Lunatic, a ComponentType object has a ConditionFilter object. */
    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class},
            field = "setConditionFilter(#param)")
    private Filter filter = new Filter();

    /** Ordered list of references to all objects in the sequence / subsequence
     * that corresponds to a subsequence (if any) or a question. */
    @DDI(contextType = SequenceType.class,
            field = "getControlConstructReferenceList()" +
                    ".?[#this.getTypeOfObject().toString() == 'QuestionConstruct'" +
                    "or #this.getTypeOfObject().toString() == 'Sequence']" +
                    ".![#index.get(#this.getIDArray(0).getStringValue())]" +
                    ".![#this instanceof T(datacollection33.QuestionConstructType) ? " +
                    "#this.getQuestionReference().getIDArray(0).getStringValue() : " +
                    "#this instanceof T(datacollection33.SequenceType) ? " +
                    "#this.getIDArray(0).getStringValue() : " +
                    "null]")
    private final List<String> componentReferences = new ArrayList<>();

    /** Ordered list of all items in the sequence / subsequence.
     * Note: the 'componentReference' attribute usages could be replaced with this one. */
    @DDI(contextType = SequenceType.class, field = "getControlConstructReferenceList()")
    private final List<SequenceItem> sequenceItems = new ArrayList<>();

}
