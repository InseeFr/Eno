package fr.insee.eno.core.model.declaration;

import fr.insee.ddi.lifecycle33.datacollection.StatementItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.DeclarationType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Text displayed before a question or sequence. */
@Getter
@Setter
@Context(format = Format.POGUES, type = fr.insee.pogues.model.DeclarationType.class)
@Context(format = Format.DDI, type = StatementItemType.class)
@Context(format = Format.LUNATIC, type = DeclarationType.class)
public class Declaration extends EnoIdentifiableObject implements DeclarationInterface {

    @Pogues("")
    @DDI("getDisplayTextArray(0)")
    @Lunatic("setLabel(#param)")
    DynamicLabel label;

    @Lunatic("setDeclarationType(T(fr.insee.lunatic.model.flat.DeclarationTypeEnum).valueOf(#param))")
    String declarationType = "STATEMENT";

    @Lunatic("setPosition(T(fr.insee.lunatic.model.flat.DeclarationPositionEnum).valueOf(#param))")
    String position = "BEFORE_QUESTION_TEXT";

    /** List of concerned modes.
     * Only exists in 'in' formats, then used to do mode selection processing on the model.
     * In DDI, a StatementItem has a list of ConstructName that contains this information.
     * (Difference with Instruction: no selection to do.) */
    @Pogues("getDeclarationMode()")
    @DDI("getConstructNameList()" +
            ".![T(fr.insee.eno.core.model.mode.Mode).convertDDIMode(#this.getStringArray(0).getStringValue())]")
    private final List<Mode> modes = new ArrayList<>();

}
