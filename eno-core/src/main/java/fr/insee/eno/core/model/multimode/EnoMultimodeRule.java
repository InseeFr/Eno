package fr.insee.eno.core.model.multimode;


import fr.insee.eno.core.annotations.Contexts;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.multimode.MultimodeRule;
import fr.insee.pogues.model.Rules;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Contexts.Context(format = Format.POGUES, type = Rules.class)
@Contexts.Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.multimode.MultimodeLeaf.class)
public class EnoMultimodeRule {

    String source;

    @Pogues("getRules()")
    @Lunatic("setRules(#param)")
    List<EnoRule> rules;

    private Map<String, MultimodeRule> convertToLunaticRules(Rules poguesRules){
        Map<String, MultimodeRule> rules = new HashMap<>();

    };
}
