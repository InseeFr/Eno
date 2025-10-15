package fr.insee.eno.core.model.multimode;


import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.multimode.MultimodeLeaf;
import fr.insee.lunatic.model.flat.multimode.MultimodeQuestionnaire;
import fr.insee.pogues.model.Rules;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Context(format = Format.POGUES, type = Rules.class)
@Context(format = Format.LUNATIC, type = {MultimodeQuestionnaire.class, MultimodeLeaf.class})
public class EnoMultimodeRules extends EnoObject {

    /** In Lunatic, this is mapped in a processing step.
     * @see fr.insee.eno.core.processing.out.steps.lunatic.LunaticMultimodeRules */
    @Pogues("getRules()")
    private final List<EnoMultimodeRule> rules = new ArrayList<>();

}
