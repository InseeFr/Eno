package fr.insee.eno.core.model;

import datacollection33.InstructionType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.DeclarationType;
import lombok.Getter;
import lombok.Setter;

/** Text displayed after a question or sequence. */
@Getter
@Setter
public class Instruction extends EnoObject {

    @DDI(contextType = InstructionType.class,
            field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = DeclarationType.class, field = "setId(#param)")
    String id;
    // TODO: why does Lunatic concatenate sequence id and declaration id ? -> no concrete reason, ok

    /**
     * In DDI, the declaration type (instruction / help / warning / ...) is written in the first
     * InstructionName element.
     * TODO: warning: not intended to be the first element (even if it actually is in practice), the element to get is the one that does not correspond to an enum that concerns the collection mode.
     * Warning: the value in DDI is lower case.
     */
    @DDI(contextType = InstructionType.class,
            field = "getInstructionNameArray(0).getStringArray(0).getStringValue()")
    @Lunatic(contextType = DeclarationType.class,
            field = "setDeclarationType(T(fr.insee.lunatic.model.flat.DeclarationTypeEnum).valueOf(#param.toUpperCase()))")
    String declarationType;

    @DDI(contextType = InstructionType.class,
            field = "getInstructionTextArray(0).getTextContentArray(0).getText().getStringValue()") //TODO: unsafe superclass method call
    @Lunatic(contextType = DeclarationType.class, field = "setLabel(#param)")
    String label;

    @Lunatic(contextType = DeclarationType.class,
            field = "setPosition(T(fr.insee.lunatic.model.flat.DeclarationPositionEnum).valueOf(#param))")
    String position = "AFTER_QUESTION_TEXT";

}
