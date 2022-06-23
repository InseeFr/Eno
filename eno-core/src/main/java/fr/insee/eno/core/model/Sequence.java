package fr.insee.eno.core.model;

import datacollection33.SequenceType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.DeclarationType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Sequence {

    @DDI(contextType = SequenceType.class, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.SequenceType.class, field = "setId(#param)")
    private String id;

    @DDI(contextType = SequenceType.class, field = "getLabelArray(0).getContentArray(0).getStringValue()")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.SequenceType.class, field = "setLabel(#param)")
    private String label;

    @DDI(contextType = SequenceType.class,
            field = "getInterviewerInstructionReferenceList().![#index.get(#this.getIDArray(0).getStringValue())]")
    @Lunatic(contextType = fr.insee.lunatic.model.flat.SequenceType.class, field = "getDeclarations()",
            instanceType = DeclarationType.class)
    private final List<Instruction> instructions = new ArrayList<>();

    private final List<Declaration> declarations = new ArrayList<>();

    @Lunatic(contextType = fr.insee.lunatic.model.flat.SequenceType.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    private String componentType = "SEQUENCE";

}
