package fr.insee.eno.core.model.multimode;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.POGUES, type = fr.insee.pogues.model.Multimode.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.multimode.Multimode.class)
public class EnoMultimode extends EnoObject {

    @Pogues("getQuestionnaire()")
    @Lunatic("setQuestionnaire(#param)")
    private EnoQuestionnaireRules questionnaire;

    @Pogues("getLeaf()")
    @Lunatic("setLeaf(#param)")
    private EnoLeafRules leaf;

}
