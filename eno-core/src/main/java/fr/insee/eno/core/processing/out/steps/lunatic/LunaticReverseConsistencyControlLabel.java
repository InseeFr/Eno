package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Processing reversing the VTL expression  value of controls. The VTL expression needs to be reversed in lunatic format
 */
public class LunaticReverseConsistencyControlLabel implements ProcessingStep<Questionnaire> {
    /**
     *
     * @param lunaticQuestionnaire lunatic questionnaire to be processed.
     */
    public void apply(Questionnaire lunaticQuestionnaire) {
        processComponents(lunaticQuestionnaire.getComponents());
    }

    private void processComponents(List<ComponentType> components) {
        components.stream()
                .map(ComponentType::getControls)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(control -> control.getTypeOfControl().equals(ControlTypeOfControlEnum.CONSISTENCY))
                .forEach(control -> {
                    LabelType label = control.getControl();
                    label.setValue("not(" + label.getValue() + ")");
                });

        components.stream()
                .filter(ComponentNestingType.class::isInstance)
                .map(ComponentNestingType.class::cast)
                .forEach(componentNesting -> processComponents(componentNesting.getComponents()));
    }
}
