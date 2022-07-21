package fr.insee.eno.core.model;

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

    private final List<Declaration> declarations = new ArrayList<>();

}
