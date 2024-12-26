package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class LunaticAddResizing implements ProcessingStep<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;
    private final EnoIndex enoIndex;

    public LunaticAddResizing(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        this.enoIndex = enoQuestionnaire.getIndex();
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        ResizingType resizingType = new ResizingType();
        //
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        LunaticRosterResizingLogic rosterResizingLogic = new LunaticRosterResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire);
        LunaticPairwiseResizingLogic pairwiseResizingLogic = new LunaticPairwiseResizingLogic(
                lunaticQuestionnaire, enoIndex);
        // Note: roster for loop component don't generate resizing entries unless there is a loop linked to it
        // (this case is managed in the "loop resizing logic" class).
        //
        lunaticQuestionnaire.getComponents().forEach(component -> {
            ComponentTypeEnum componentType = component.getComponentType();
            if (Objects.requireNonNull(componentType) == ComponentTypeEnum.LOOP) {
                loopResizingLogic.buildResizingEntries((Loop) component, resizingType);
            }
            if (Objects.requireNonNull(componentType) == ComponentTypeEnum.ROSTER_FOR_LOOP) {
                rosterResizingLogic.buildResizingEntries((RosterForLoop) component, resizingType);
            }
            if (componentType == ComponentTypeEnum.PAIRWISE_LINKS) {
                pairwiseResizingLogic.buildPairwiseResizingEntries((PairwiseLinks) component, resizingType);
            }
        });
        lunaticQuestionnaire.setResizing(resizingType);
    }

}
