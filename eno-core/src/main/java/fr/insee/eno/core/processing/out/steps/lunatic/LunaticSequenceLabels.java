package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

import java.util.List;

/** In Lunatic, the sequence label uses the common Lunatic label object.
 * It's default label type is 'VTL MD'.
 * Yet, sequences label style shouldn't contain Markdown (title styling).
 * This processing step changes the type of sequence labels to 'VTL'.
 */
public class LunaticSequenceLabels implements ProcessingStep<Questionnaire> {

    /**
     * Changes the label type of sequences to 'VTL' within the Lunatic questionnaire.
     * @param lunaticQuestionnaire A Lunatic questionnaire.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        editLabels(lunaticQuestionnaire.getComponents());
        //
        lunaticQuestionnaire.getComponents().stream()
                .filter(ComponentNestingType.class::isInstance).map(ComponentNestingType.class::cast)
                .map(ComponentNestingType::getComponents)
                .forEach(this::editLabels);
    }

    private void editLabels(List<ComponentType> lunaticComponents) {
        lunaticComponents.stream()
                .filter(Sequence.class::isInstance).map(Sequence.class::cast)
                .forEach(this::editSequenceLabel);
    }

    private void editSequenceLabel(Sequence sequence) {
        sequence.getLabel().setType(LabelTypeEnum.VTL);
    }

}
