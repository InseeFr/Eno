package fr.insee.eno.core.model.multimode;

import fr.insee.eno.core.annotations.Contexts;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.parameter.Format;
import fr.insee.pogues.model.Multimode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Contexts.Context(format = Format.POGUES, type = Multimode.class)
@Contexts.Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.multimode.Multimode.class)
public class EnoMultimode {

    @Pogues("getQuestionnaire()")
    @Lunatic("setQuestionnaire(#param)")
    private EnoMultimodeRule questionnaire;

    @Pogues("getLeaf()")
    @Lunatic("setLeaf(#param)")
    private EnoMultimodeRule leaf;

}
