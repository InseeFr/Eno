package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.business.LunaticLogicException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.multimode.EnoMultimode;
import fr.insee.eno.core.model.multimode.EnoMultimodeRules;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.Roundabout;
import fr.insee.lunatic.model.flat.multimode.MultimodeLeaf;
import fr.insee.lunatic.model.flat.multimode.MultimodeQuestionnaire;
import fr.insee.lunatic.model.flat.multimode.MultimodeRule;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Processing step to map what's not mapped by the mapper for then Lunatic multimode object.
 * WARNING: This step must be called after the roundabout step.
 * @see LunaticRoundaboutLoops
 */
public class LunaticMultimodeRules implements ProcessingStep<Questionnaire> {

    private final EnoMultimode enoMultimode;

    public LunaticMultimodeRules(EnoMultimode enoMultimode) {
        this.enoMultimode = enoMultimode;
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        if (enoMultimode == null)
            return;
        if (lunaticQuestionnaire.getMultimode() == null)
            throw new MappingException("There is a multimode object in the Eno model but not in Lunatic.");

        MultimodeQuestionnaire multimodeQuestionnaire = lunaticQuestionnaire.getMultimode().getQuestionnaire();
        if (multimodeQuestionnaire != null){
            lunaticQuestionnaire.getMultimode().getQuestionnaire().setRules(new LinkedHashMap<>());
            mapRules(enoMultimode.getQuestionnaire(), multimodeQuestionnaire.getRules());
        }

        MultimodeLeaf multimodeLeaf = lunaticQuestionnaire.getMultimode().getLeaf();
        if (multimodeLeaf != null) {
            lunaticQuestionnaire.getMultimode().getLeaf().setRules(new LinkedHashMap<>());
            mapRules(enoMultimode.getLeaf(), multimodeLeaf.getRules());
            mapLeafSource(lunaticQuestionnaire);
        }
    }

    private void mapRules(EnoMultimodeRules enoMultimodeRules, Map<String, MultimodeRule> lunaticMultimodeRules) {
        LunaticMapper lunaticMapper = new LunaticMapper();
        enoMultimodeRules.getRules().forEach(enoMultimodeRule -> {
            MultimodeRule lunaticMultimodeRule = new MultimodeRule();
            lunaticMapper.mapEnoObject(enoMultimodeRule, lunaticMultimodeRule);
            lunaticMultimodeRules.put(enoMultimodeRule.getName(), lunaticMultimodeRule);
        });
    }

    private void mapLeafSource(Questionnaire lunaticQuestionnaire) {
        Roundabout roundabout = findRoundabout(lunaticQuestionnaire);
        lunaticQuestionnaire.getMultimode().getLeaf().setSource(roundabout.getId());
    }

    private Roundabout findRoundabout(Questionnaire lunaticQuestionnaire) {
        List<Roundabout> roundaboutList = lunaticQuestionnaire.getComponents().stream()
                .filter(Roundabout.class::isInstance).map(Roundabout.class::cast).toList();
        if (roundaboutList.isEmpty())
            throw new LunaticLogicException(lunaticQuestionnaire + " has a multimode object but no roundabout.");
        if (roundaboutList.size() > 1)
            throw new LunaticLogicException(lunaticQuestionnaire + " has several roundabout components.");
        return roundaboutList.getFirst();
    }

}
