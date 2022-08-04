package fr.insee.eno.core.model;

import datacollection33.SequenceType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
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
public abstract class AbstractSequence extends EnoObject {

    @DDI(contextType = SequenceType.class, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "setId(#param)")
    private String id;

    /** Sequence / subsequence label. */
    @DDI(contextType = SequenceType.class, field = "getLabelArray(0).getContentArray(0).getStringValue()")
    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "setLabel(#param)")
    private String label;

    /** Sequence / subsequence instructions.
     * In DDI, the SequenceType object contains the list of references to the instructions.
     * In Lunatic, instructions and declarations belongs to the same list. */
    @DDI(contextType = SequenceType.class,
            field = "getInterviewerInstructionReferenceList().![#index.get(#this.getIDArray(0).getStringValue())]")
    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "getDeclarations()")
    private final List<Instruction> instructions = new ArrayList<>();

    /** Sequence / subsequence declarations.
     * In DDI, the declarations are mapped in the questionnaire object, and are put here through a 'processing' class.
     * In Lunatic, instructions and declarations belongs to the same list. */
    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "getDeclarations()")
    private final List<Declaration> declarations = new ArrayList<>();

    /** Sequence / subsequence controls.
     * In DDI, the controls are mapped in the questionnaire object, and are put here through a 'processing' class.
     * In Lunatic, the sequence / subsequence object has a list of controls. */
    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "getControls()")
    private final List<Control> controls = new ArrayList<>();

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
