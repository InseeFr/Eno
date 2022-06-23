package fr.insee.eno.core.model;

import datacollection33.SequenceType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.DeclarationType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent subsequences (i.e. a sequence within a sequence, but that cannot contain a sequence...).
 * (while waiting for Lunatic to take over n-number of depth levels in subsequences,
 * this class should disappear when it will be the case).
 */
@Getter
@Setter
public class Subsequence {

    @DDI(contextType = SequenceType.class, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Subsequence.class, field = "setId(#param)")
    private String id;

    @DDI(contextType = SequenceType.class, field = "getLabelArray(0).getContentArray(0).getStringValue()")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Subsequence.class, field = "setLabel(#param)")
    private String label;

    @DDI(contextType = SequenceType.class,
            field = "getInterviewerInstructionReferenceList().![#index.get(#this.getIDArray(0).getStringValue())]")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.Subsequence.class, field = "getDeclarations()",
            instanceType = DeclarationType.class)
    private final List<Instruction> instructions = new ArrayList<>();

    private final List<Declaration> declarations = new ArrayList<>();

    @Lunatic(contextType = fr.insee.lunatic.model.flat.Subsequence.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    private String componentType = "SUBSEQUENCE";
}
