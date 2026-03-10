package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.LabelTypeEnum;

import java.util.List;
import java.util.Optional;

import static fr.insee.eno.core.utils.LunaticUtils.findComponentById;

/**
 * Inserts dynamic option filter for UCQ based on VARIABLE choice type.
 */
public record LunaticInsertDynamicUCQOptionFilter(
        EnoQuestionnaire enoQuestionnaire) implements ProcessingStep<Questionnaire> {

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {

        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(UniqueChoiceQuestion.class::isInstance)
                .map(UniqueChoiceQuestion.class::cast)
                .forEach(ucq -> process(ucq, lunaticQuestionnaire));
    }

    private void process(UniqueChoiceQuestion ucq, Questionnaire lunaticQuestionnaire) {

        if (ucq.getOptionSource() == null || ucq.getOptionFilter() == null) {
            return;
        }

        Optional<ComponentType> optionalComponentType =
                findComponentById(lunaticQuestionnaire, ucq.getId());

        if (optionalComponentType.isEmpty()) {
            throw new MappingException(
                    "Cannot find Lunatic component for UCQ " + ucq.getId()
            );
        }

        ConditionFilterType filter = new ConditionFilterType();
        filter.setType(LabelTypeEnum.VTL);
        filter.setValue(ucq.getOptionFilter().getValue());
        filter.setBindingDependencies(List.of(ucq.getOptionSource()));

        ComponentType component = optionalComponentType.get();

        if (component instanceof Radio radio) {
            radio.setOptionFilter(filter);
        }
        if (component instanceof Dropdown dropdown) {
            dropdown.setOptionFilter(filter);
        }
        if (component instanceof CheckboxOne checkboxOne) {
            checkboxOne.setOptionFilter(filter);
        }
    }
}