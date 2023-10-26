package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.processing.common.steps.EnoAddResponseTimeSection;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;

import java.util.Optional;

public class LunaticResponseTimeQuestionPagination implements ProcessingStep<Questionnaire> {

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        Optional<ComponentType> hoursQuestionComponent = lunaticQuestionnaire.getComponents().stream()
                .filter(component -> EnoAddResponseTimeSection.HOURS_QUESTION_ID.equals(component.getId()))
                .findAny();
        if (hoursQuestionComponent.isPresent()) {
            String pageNumber = hoursQuestionComponent.get().getPage();
            int hoursQuestionComponentIndex = lunaticQuestionnaire.getComponents().indexOf(hoursQuestionComponent.get());
            // minutes question is just after the hours question
            lunaticQuestionnaire.getComponents().get(hoursQuestionComponentIndex + 1).setPage(pageNumber);
        }
    }

}
