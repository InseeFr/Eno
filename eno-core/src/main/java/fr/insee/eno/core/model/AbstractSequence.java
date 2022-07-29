package fr.insee.eno.core.model;

import datacollection33.IfThenElseTextType;
import datacollection33.IfThenElseType;
import datacollection33.SequenceType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class AbstractSequence extends EnoObject {

    @DDI(contextType = SequenceType.class, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "setId(#param)")
    private String id;

    @DDI(contextType = SequenceType.class, field = "getLabelArray(0).getContentArray(0).getStringValue()")
    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "setLabel(#param)")
    private String label;

    @DDI(contextType = SequenceType.class,
            field = "getInterviewerInstructionReferenceList().![#index.get(#this.getIDArray(0).getStringValue())]")
    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "getDeclarations()")
    private final List<Instruction> instructions = new ArrayList<>();

    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "getDeclarations()")
    private final List<Declaration> declarations = new ArrayList<>();

    @Lunatic(contextType = {fr.insee.lunatic.model.flat.SequenceType.class, Subsequence.class}, field = "getControls()")
    private final List<Control> controls = new ArrayList<>();

    @DDI(contextType = SequenceType.class,
            field = "getControlConstructReferenceList()" +
                    ".?[#this.getTypeOfObject().toString() == 'QuestionConstruct'" +
                    "or #this.getTypeOfObject().toString() == 'Sequence'" +
                    "or #this.getTypeOfObject().toString() == 'IfThenElse']" +
                    ".![#index.get(#this.getIDArray(0).getStringValue())]" +
                    ".![#this instanceof T(datacollection33.QuestionConstructType) ? " +
                    "#this.getQuestionReference().getIDArray(0).getStringValue() : " +
                    "#this instanceof T(datacollection33.SequenceType) ? " +
                    "#this.getIDArray(0).getStringValue() : " +
                    "#this instanceof T(datacollection33.IfThenElseType) ? " +
                    "#index.get(#index.get(#this.getThenConstructReference().getIDArray(0).getStringValue())" +
                    ".getControlConstructReferenceArray(0).getIDArray(0).getStringValue())" +
                    ".getQuestionReference().getIDArray(0).getStringValue() " +
                    ": null]")
    private final List<String> componentReferences = new ArrayList<>();

    @DDI(contextType = SequenceType.class, field = "getControlConstructReferenceList()")
    private final List<SequenceItem> sequenceItems = new ArrayList<>();

}
