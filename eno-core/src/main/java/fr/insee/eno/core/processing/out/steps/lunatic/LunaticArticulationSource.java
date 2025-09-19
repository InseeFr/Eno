package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.business.LunaticLogicException;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.Roundabout;

import java.util.List;

/**
 * The Lunatic articulation component requires a "source" property, which is the identifier of the roundabout
 * of the questionnaire. This processing step inserts this piece of data.
 * Note: the current rule is that only one roundabout is allowed in a questionnaire, the processing uses this
 * hypothesis.
 * WARNING: This step must be called after the roundabout step.
 * @see LunaticRoundaboutLoops
 */
public class LunaticArticulationSource implements ProcessingStep<Questionnaire> {

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        if (lunaticQuestionnaire.getArticulation() == null)
            return;
        Roundabout roundabout = findRoundabout(lunaticQuestionnaire);
        lunaticQuestionnaire.getArticulation().setSource(roundabout.getId());
    }

    private Roundabout findRoundabout(Questionnaire lunaticQuestionnaire) {
        List<Roundabout> roundaboutList = lunaticQuestionnaire.getComponents().stream()
                .filter(Roundabout.class::isInstance).map(Roundabout.class::cast).toList();
        if (roundaboutList.isEmpty())
            throw new LunaticLogicException(lunaticQuestionnaire + " has an articulation component but no roundabout.");
        if (roundaboutList.size() > 1)
            throw new LunaticLogicException(lunaticQuestionnaire + " has several roundabout components.");
        return roundaboutList.getFirst();
    }

}
