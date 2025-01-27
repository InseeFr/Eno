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
import fr.insee.pogues.model.SurveyModeEnum;
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

    @Pogues("getText()")
    @DDI("getDisplayTextArray(0)")
    @Lunatic("setLabel(#param)")
    private DynamicLabel label;

    /** This information is missing in the DDI. The 'rule' is therefore to hardcode 'STATEMENT' as
     * the default declaration type. As such, the details provided by Pogues on this matter are
     * not taken into account. */
    @Lunatic("setDeclarationType(T(fr.insee.lunatic.model.flat.DeclarationTypeEnum).valueOf(#param))")
    private String declarationType = "STATEMENT";

    /** A declaration is systematically associated with the position 'BEFORE_QUESTION_TEXT':
     * this information is therefore hardcoded. Conversely, an instruction always follows
     * the assertion 'AFTER_QUESTION_TEXT'. */
    @Lunatic("setPosition(T(fr.insee.lunatic.model.flat.DeclarationPositionEnum).valueOf(#param))")
    private String position = "BEFORE_QUESTION_TEXT";

    /** List of concerned modes.
     * Only exists in 'in' formats, then used to do mode selection processing on the model.
     * In DDI, a StatementItem has a list of ConstructName that contains this information.
     * (Difference with Instruction: no selection to do.) */
    @Pogues("getDeclarationMode()"+
            ".![T(fr.insee.eno.core.model.mode.Mode).convertSurveyModeEnumMode(#this)]")
    @DDI("getConstructNameList()" +
            ".![T(fr.insee.eno.core.model.mode.Mode).convertDDIMode(#this.getStringArray(0).getStringValue())]")
    private final List<Mode> modes = new ArrayList<>();

}

