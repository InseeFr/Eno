package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.EnoLabel;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.eno.core.utils.TooltipUtils;

/**
 * Processing to make tooltip in labels from Pogues or DDI compliant with Lunatic.
 * @see TooltipUtils for details.
 * */
public class EnoCleanTooltips implements ProcessingStep<EnoQuestionnaire> {

    private final TooltipUtils tooltipUtils = new TooltipUtils();

    private final EnoCatalog enoCatalog;

    public EnoCleanTooltips(EnoCatalog enoCatalog) {
        this.enoCatalog = enoCatalog;
    }

    /**
     * For each label present in the given Eno questionnaire, clean Lunatic tooltips.
     * @param enoQuestionnaire Eno questionnaire.
     */
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoCatalog.getLabels().forEach(this::cleanTooltipsInLabel);
    }

    private void cleanTooltipsInLabel(EnoLabel enoLabel) {
        enoLabel.setValue(tooltipUtils.cleanTooltips(enoLabel.getValue()));
    }

}
