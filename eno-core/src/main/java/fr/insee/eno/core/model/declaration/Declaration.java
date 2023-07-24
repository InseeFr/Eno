package fr.insee.eno.core.model.declaration;

import datacollection33.StatementItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
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
@Context(format = Format.DDI, type = StatementItemType.class)
@Context(format = Format.LUNATIC, type = DeclarationType.class)
public class Declaration extends EnoIdentifiableObject implements DeclarationInterface {

    @DDI("getDisplayTextArray(0)")
    @Lunatic("setLabel(#param)")
    DynamicLabel label;

    /** List of variable names that are used in the declarations' label.
     * This list is filled in an Eno processing, and used in Lunatic processing to fill 'bindingDependencies'. */
    List<String> variableNames = new ArrayList<>();

    @Lunatic("setDeclarationType(T(fr.insee.lunatic.model.flat.DeclarationTypeEnum).valueOf(#param))")
    String declarationType = "STATEMENT";

    @Lunatic("setPosition(T(fr.insee.lunatic.model.flat.DeclarationPositionEnum).valueOf(#param))")
    String position = "BEFORE_QUESTION_TEXT";

    /** List of concerned modes.
     * Only exists in 'in' formats, then used to do mode selection processing on the model.
     * In DDI, a StatementItem has a list of ConstructName that contains this information.
     * (Difference with Instruction: no selection to do.) */
    @DDI("getConstructNameList()" +
            ".![T(fr.insee.eno.core.model.mode.Mode).convertDDIMode(#this.getStringArray(0).getStringValue())]")
    private final List<Mode> modes = new ArrayList<>();

}
